package tf.cyber.xacml.pollingfrequency;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import okhttp3.*;
import org.json.JSONObject;
import org.ow2.authzforce.core.pdp.api.*;
import org.ow2.authzforce.core.pdp.api.value.*;
import org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory;
import org.ow2.authzforce.xacml.identifiers.XacmlAttributeId;
import org.ow2.authzforce.xacml.identifiers.XacmlDatatypeId;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

import javax.xml.bind.annotation.XmlRegistry;
import java.io.IOException;
import java.util.*;

@XmlRegistry
public class PollingFrequencyProvider extends BaseNamedAttributeProvider {
    private final String logUrl;

    private final OkHttpClient httpClient;

    private final NamedAttributeProvider dependencyProvider;

    // Provided designators.
    private final AttributeDesignatorType ms;
    private final AttributeDesignatorType s;
    private final AttributeDesignatorType hz;

    private final static String PROVIDER_ID = "urn:tf:cyber:xacml:polling-frequency";

    private final static String ATTRIBUTE_ID_MS = "urn:tf:cyber:xacml:polling-frequency:time" +
            "-since-last-access:ms";
    private final static String ATTRIBUTE_ID_S = "urn:tf:cyber:xacml:polling-frequency:time-since" +
            "-last-access:s";
    private final static String ATTRIBUTE_ID_HZ = "urn:tf:cyber:xacml:polling-frequency:hz";

    private PollingFrequencyProvider(String logUrl, NamedAttributeProvider dependencyProvider) throws IllegalArgumentException {
        super(PROVIDER_ID);
        this.logUrl = logUrl;
        this.dependencyProvider = dependencyProvider;

        this.httpClient = new OkHttpClient();

        this.ms =
                createAttributeDesignator(AttributeFqns.newInstance(XacmlAttributeCategory.XACML_3_0_ENVIRONMENT.value(),
                                                                    Optional.empty(),
                                                                    ATTRIBUTE_ID_MS),
                                          StandardDatatypes.INTEGER, false);

        this.s =
                createAttributeDesignator(AttributeFqns.newInstance(XacmlAttributeCategory.XACML_3_0_ENVIRONMENT.value(),
                                                                    Optional.empty(),
                                                                    ATTRIBUTE_ID_S),
                                          StandardDatatypes.INTEGER, false);

        this.hz =
                createAttributeDesignator(AttributeFqns.newInstance(XacmlAttributeCategory.XACML_3_0_ENVIRONMENT.value(),
                                                                    Optional.empty(),
                                                                    ATTRIBUTE_ID_HZ),
                                          StandardDatatypes.DOUBLE, false);
    }

    @Override
    public Set<AttributeDesignatorType> getProvidedAttributes() {
        return Set.of(ms, s, hz);
    }

    @Override
    public <AV extends AttributeValue> AttributeBag<AV> get(AttributeFqn attributeFqn,
                                                            Datatype<AV> datatype,
                                                            EvaluationContext evaluationContext,
                                                            Optional<EvaluationContext> optional) throws IndeterminateEvaluationException {

        String subjectID = dependencyProvider.get(AttributeFqns.newInstance(XacmlAttributeCategory.XACML_1_0_ACCESS_SUBJECT.value(),
                                                                                 Optional.empty(),
                                                                                 XacmlAttributeId.XACML_1_0_SUBJECT_ID.value()),
                                                       StandardDatatypes.STRING,
                                                       evaluationContext,
                                                       Optional.empty())
                .getSingleElement().getUnderlyingValue();

        String actionID = dependencyProvider.get(AttributeFqns.newInstance(XacmlAttributeCategory.XACML_3_0_ACTION.value(),
                                                                                 Optional.empty(),
                                                                                 XacmlAttributeId.XACML_1_0_ACTION_ID.value()),
                                                       StandardDatatypes.STRING,
                                                       evaluationContext,
                                                       Optional.empty())
                .getSingleElement().getUnderlyingValue();

        String resourceID = dependencyProvider.get(AttributeFqns.newInstance(XacmlAttributeCategory.XACML_3_0_RESOURCE.value(),
                                                                                Optional.empty(),
                                                                                XacmlAttributeId.XACML_1_0_RESOURCE_ID.value()),
                                                      StandardDatatypes.STRING,
                                                      evaluationContext,
                                                      Optional.empty())
                .getSingleElement().getUnderlyingValue();

        // Query last access time from log.
        JSONObject request = new JSONObject();
        request.put("subject", subjectID);
        request.put("action", actionID);
        request.put("resource", resourceID);

        Long lastAccess = null;

        Request accessLogRequest = new Request.Builder()
                .url(logUrl)
                .post(RequestBody.create(request.toString(), MediaType.get("application/json; charset=utf-8")))
                .build();

        try (Response response = httpClient.newCall(accessLogRequest).execute()) {
            JSONObject res = new JSONObject(response.body().string());
            lastAccess = res.getBigInteger("time").longValue();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IndeterminateEvaluationException("Failed to query last access time. Is the access log server working?",
                                                       XacmlStatusCode.PROCESSING_ERROR.value());
        }

        long timeDeltaMs = Calendar.getInstance().getTimeInMillis() - lastAccess;

        AttributeBag<AV> bag = null;

        if (attributeFqn.getId().equals(ATTRIBUTE_ID_MS)) {
            bag = (AttributeBag<AV>) Bags.newAttributeBag(StandardDatatypes.INTEGER,
                                                          List.of(new IntegerValue(new ArbitrarilyBigInteger(timeDeltaMs))),
                                                          AttributeSources.PDP);
        }

        if (attributeFqn.getId().equals(ATTRIBUTE_ID_S)) {
            bag = (AttributeBag<AV>) Bags.newAttributeBag(StandardDatatypes.INTEGER,
                                                          List.of(new IntegerValue(new ArbitrarilyBigInteger(Math.round(timeDeltaMs / 1000)))),
                                                          AttributeSources.PDP);
        }

        if (attributeFqn.getId().equals(ATTRIBUTE_ID_HZ)) {
            System.out.println(timeDeltaMs);
            System.out.println(1000d / timeDeltaMs);
            bag = (AttributeBag<AV>) Bags.newAttributeBag(StandardDatatypes.DOUBLE,
                                                          List.of(new DoubleValue(1000d / timeDeltaMs)),
                                                          AttributeSources.PDP);
        }

        return bag;
    }

    @Override
    public void close() {
    }

    private static AttributeDesignatorType createAttributeDesignator(AttributeFqn attributeFqn,
                                                                     Datatype<?> attributeType,
                                                                     boolean mustBePresent) {
        return new AttributeDesignatorType(attributeFqn.getCategory(), attributeFqn.getId(),
                                           attributeType.getId(),
                                           attributeFqn.getIssuer().orElse(null), mustBePresent);
    }

    public static class DependencyAwareAttributeProviderFactory implements DependencyAwareFactory {
        private final String logPath;

        private DependencyAwareAttributeProviderFactory(final String logPath) {
            this.logPath = logPath;
        }

        @Override
        public Set<AttributeDesignatorType> getDependencies() {
            AttributeDesignatorType subject =
                    createAttributeDesignator(AttributeFqns.newInstance(XacmlAttributeCategory.XACML_1_0_ACCESS_SUBJECT.value(),
                                                                        Optional.empty(),
                                                                        XacmlAttributeId.XACML_1_0_SUBJECT_ID.value()),
                                              StandardDatatypes.STRING,
                                              true);

            AttributeDesignatorType action =
                    createAttributeDesignator(AttributeFqns.newInstance(XacmlAttributeCategory.XACML_3_0_ACTION.value(),
                                                                        Optional.empty(),
                                                                        XacmlAttributeId.XACML_1_0_ACTION_ID.value()),
                                              StandardDatatypes.STRING,
                                              true);

            AttributeDesignatorType resource =
                    createAttributeDesignator(AttributeFqns.newInstance(XacmlAttributeCategory.XACML_3_0_RESOURCE.value(),
                                                                        Optional.empty(),
                                                                        XacmlAttributeId.XACML_1_0_RESOURCE_ID.value()),
                                              StandardDatatypes.STRING
                            , true);

            return Set.of(subject, action, resource);
        }

        @Override
        public CloseableNamedAttributeProvider getInstance(AttributeValueFactoryRegistry attributeValueFactoryRegistry,
                                                           NamedAttributeProvider attributeProvider) {
            {
                return new PollingFrequencyProvider(this.logPath, attributeProvider);
            }
        }
    }

    public static class Factory extends CloseableNamedAttributeProvider.FactoryBuilder<PollingFrequencyProviderDescriptor> {
        @Override
        public Class<PollingFrequencyProviderDescriptor> getJaxbClass() {
            return PollingFrequencyProviderDescriptor.class;
        }

        @Override
        public CloseableNamedAttributeProvider.DependencyAwareFactory getInstance(PollingFrequencyProviderDescriptor conf, EnvironmentProperties environmentProperties) throws IllegalArgumentException {
            return new DependencyAwareAttributeProviderFactory(conf.getLogUrl());
        }
    }
}

