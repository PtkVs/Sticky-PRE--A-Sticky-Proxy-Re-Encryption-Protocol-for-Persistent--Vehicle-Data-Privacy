package tf.cyber.thesis.automotiveaccesscontrol.pep;

import okhttp3.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tf.cyber.thesis.automotiveaccesscontrol.pep.annotation.attributes.*;
import tf.cyber.thesis.automotiveaccesscontrol.pep.data.XACMLAttribute;
import tf.cyber.thesis.automotiveaccesscontrol.pep.obligation.Obligation;
import tf.cyber.thesis.automotiveaccesscontrol.pep.obligation.ObligationService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static tf.cyber.thesis.automotiveaccesscontrol.pep.util.XACMLConstants.*;

@Aspect
public class XACMLInterceptor {
    private final Logger logger = LoggerFactory.getLogger(XACMLInterceptor.class);

    private enum DECISION_TYPE {
        PERMIT,
        DENY,
        INDETERMINATE,
        NOT_APPLICABLE
    }

    @Autowired
    OkHttpClient httpClient;

    @Autowired
    org.springframework.core.env.Environment env;

    @Autowired
    ObligationService obligationService;

    @Around("@annotation(tf.cyber.thesis.automotiveaccesscontrol.pep.annotation" +
            ".XACMLAccessControl)" +
            " || @within(tf.cyber.thesis.automotiveaccesscontrol.pep.annotation" +
            ".XACMLAccessControl)")
    public Object interceptXACML(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Fetch current request from context in order to retrieve information about targeted URI.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())
                .getRequest();

        if (authentication == null) {
            // We assume that entities are authenticated, with their subject id available.
            logger.info("User tried to access XACML-protected route without authentication!");
            //throw new AccessDeniedException("Tried to access XACML-protected route without
            // authentication!");
        }

        // Assemble XACML request attributes.
        Map<String, List<XACMLAttribute<?>>> categories = new HashMap<>();

        categories.put(XACML_SUBJECT_CATEGORY, new LinkedList<>());
        categories.put(XACML_ACTION_CATEGORY, new LinkedList<>());
        categories.put(XACML_RESOURCE_CATEGORY, new LinkedList<>());

        // Add subject id, if available.
        XACMLAttribute<?> subjectID = new XACMLAttribute<>(XACML_SUBJECT_ID_ATTRIBUTE,
                                                           XACML_SUBJECT_CATEGORY,
                                                           authentication.getName());
        if (authentication.getName() != null) {
            categories.get(XACML_SUBJECT_CATEGORY).add(subjectID);
        }

        // Add resource id, if available.
        if (request.getRequestURI() != null) {
            XACMLAttribute<?> resourceID = new XACMLAttribute<>(XACML_RESOURCE_ID_ATTRIBUTE,
                                                                XACML_RESOURCE_CATEGORY,
                                                                request.getRequestURI());
            categories.get(XACML_RESOURCE_CATEGORY).add(resourceID);
        }

        // Add action id, if available.
        if (request.getMethod() != null) {
            XACMLAttribute<?> actionID = new XACMLAttribute<>(XACML_ACTION_ID_ATTRIBUTE,
                                                              XACML_ACTION_CATEGORY,
                                                              request.getMethod());
            categories.get(XACML_ACTION_CATEGORY).add(actionID);
        }

        // Check if called function has annotated parameters that should be evaluated as part
        // of the PDP request.
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        AtomicInteger i = new AtomicInteger(0);
        Arrays.stream(signature.getMethod().getParameterAnnotations()).forEach(parameterAnnotations -> {
            Arrays.stream(parameterAnnotations).forEach(annotation -> {
                if (annotation instanceof Subject) {
                    Subject subject = (Subject) annotation;
                    XACMLAttribute<?> subjectAttr = new XACMLAttribute<>(subject.value(),
                                                                         XACML_SUBJECT_CATEGORY,
                                                                         args[i.get()]);

                    categories.get(XACML_SUBJECT_CATEGORY).add(subjectAttr);
                } else if (annotation instanceof Action) {
                    Action action = (Action) annotation;
                    XACMLAttribute<?> actionAttr = new XACMLAttribute<>(action.value(),
                                                                        XACML_ACTION_CATEGORY,
                                                                        args[i.get()]);

                    categories.get(XACML_ACTION_CATEGORY).add(actionAttr);
                } else if (annotation instanceof Resource) {
                    Resource resource = (Resource) annotation;
                    XACMLAttribute<?> actionAttr = new XACMLAttribute<>(resource.value(),
                                                                        XACML_RESOURCE_CATEGORY,
                                                                        args[i.get()]);

                    categories.get(XACML_RESOURCE_CATEGORY).add(actionAttr);
                } else if (annotation instanceof Environment) {
                    Environment environment = (Environment) annotation;
                    XACMLAttribute<?> environmentAttr = new XACMLAttribute<>(environment.value(),
                                                                             XACML_ENVIRONMENT_CATEGORY,
                                                                             args[i.get()]);

                    categories.putIfAbsent(XACML_ENVIRONMENT_CATEGORY, new LinkedList<>());
                    categories.get(XACML_ENVIRONMENT_CATEGORY).add(environmentAttr);
                } else if (annotation instanceof CustomCategory) {
                    CustomCategory other = (CustomCategory) annotation;
                    XACMLAttribute<?> otherAttr = new XACMLAttribute<>(other.value(),
                                                                       other.category(),
                                                                       args[i.get()]);

                    categories.putIfAbsent(other.category(), new LinkedList<>());
                    categories.get(other.category()).add(otherAttr);
                }
            });
            i.addAndGet(1);
        });

        // Evaluate XACML request through PDP service.
        JSONObject requestJSON = assembleRequestJSON(categories);
        DECISION_TYPE decision = evaluateRequest(requestJSON);

        if (decision == DECISION_TYPE.PERMIT) {
            return joinPoint.proceed();
        }

        throw new AccessDeniedException("XACML Policy evaluation resulted in DENY.");
    }

    private JSONObject assembleRequestJSON(Map<String, List<XACMLAttribute<?>>> attributes) {
        JSONObject root = new JSONObject();
        JSONObject request = new JSONObject();
        JSONArray categoryList = new JSONArray();

        root.put("Request", request);
        request.put("Category", categoryList);

        // Walk through all specified categories.
        attributes.forEach((category, attributeList) -> {
            JSONObject currentCategory = new JSONObject();
            JSONArray attributesJSON = new JSONArray();

            currentCategory.put("CategoryId", category);
            currentCategory.put("Attribute", attributesJSON);

            attributeList.forEach((attr) -> {
                JSONObject attrJson = new JSONObject();

                attrJson.put("AttributeId", attr.getId());
                attrJson.put("DataType", attr.getDataType());
                attrJson.put("Value", attr.getXACMLValue());

                attributesJSON.put(attrJson);
            });

            categoryList.put(currentCategory);
        });

        return root;
    }

    private DECISION_TYPE evaluateRequest(JSONObject request) {
        DECISION_TYPE decision = null;

        Request xacmlHTTPRequest = new Request.Builder()
                .url(env.getProperty("pdp.location"))
                .post(RequestBody.create(request.toString(),
                                         MediaType.get("application/json; charset=utf-8")))
                .build();

        try (Response response = httpClient.newCall(xacmlHTTPRequest).execute()) {
            JSONObject res = new JSONObject(response.body().string());
            JSONArray resArr = (JSONArray) res.get("Response");

            JSONObject evaluation = (JSONObject) resArr.get(0);

            switch (evaluation.get("Decision").toString()) {
                case "Permit":
                    decision = DECISION_TYPE.PERMIT;
                    break;
                case "Deny":
                    decision = DECISION_TYPE.DENY;
                    break;
                case "Indeterminate":
                    decision = DECISION_TYPE.INDETERMINATE;
                    break;
                case "NotApplicable":
                    decision = DECISION_TYPE.NOT_APPLICABLE;
                    break;
                default:
                    throw new XACMLEvaluationException("Failed to determine decision type from " +
                                                               "PDP response.");
            }

            // Process Obligations.
            if (evaluation.has("Obligations")) {
                processObligations((JSONArray) evaluation.get("Obligations"));
            }

        } catch (IOException e) {
            throw new XACMLEvaluationException();
        }

        return decision;
    }

    private void processObligations(JSONArray obligations) {
        obligations.forEach(obligation -> {
            JSONArray attributesJSON = ((JSONObject) obligation).has("AttributeAssignment") ?
                    (JSONArray) ((JSONObject) obligation).get("AttributeAssignment") : null;

            Obligation obl = obligationService.get(((JSONObject) obligation).getString("Id"));

            List<XACMLAttribute<?>> attributes = new LinkedList<>();

            // Assemble obligation attributes.
            if (attributesJSON != null) {
                attributesJSON.forEach(attributeObject -> {
                    JSONObject attrObj = (JSONObject) attributeObject;

                    attributes.add(XACMLAttribute.of(
                            attrObj.getString("AttributeId"),
                            attrObj.getString("DataType"),
                            null, // Obligations do not specify any data types.
                            attrObj.getString("Value")
                    ));
                });
            }

            // Execute obligation.
            obl.execute(attributes);
        });
    }
}
