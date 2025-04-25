package tf.cyber.thesis.automotiveaccesscontrol.pep.obligation;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tf.cyber.thesis.automotiveaccesscontrol.pep.data.XACMLAttribute;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LogObligation extends Obligation {

    private final static String SUBJECT_ATTRIBUTE = "LogObligation:Subject";
    private final static String ACTION_ATTRIBUTE = "LogObligation:Action";
    private final static String RESOURCE_ATTRIBUTE = "LogObligation:Resource";

    @Autowired
    org.springframework.core.env.Environment env;

    @Autowired
    OkHttpClient http;

    @Override
    public void execute(List<XACMLAttribute<?>> args) {
        Map<String, XACMLAttribute> attributes = new HashMap<>();

        for (XACMLAttribute<?> arg : args) {
            attributes.put(arg.getId(), arg);
        }

        // Logging requires subject id, action id as well as resource id.
        if (!attributes.containsKey(SUBJECT_ATTRIBUTE)) {
            throw new ObligationFulfilmentFailedException();
        }

        if (!attributes.containsKey(ACTION_ATTRIBUTE)) {
            throw new ObligationFulfilmentFailedException();
        }

        if (!attributes.containsKey(RESOURCE_ATTRIBUTE)) {
            throw new ObligationFulfilmentFailedException();
        }

        JSONObject root = new JSONObject();

        root.put("subject", attributes.get(SUBJECT_ATTRIBUTE).getValue());
        root.put("action", attributes.get(ACTION_ATTRIBUTE).getValue());
        root.put("resource", attributes.get(RESOURCE_ATTRIBUTE).getValue());

        Request req = new Request.Builder()
                .url(env.getProperty("log.location"))
                .post(RequestBody.create(root.toString(),
                                         MediaType.get("application/json; charset=utf-8")))
                .build();

        try (Response response = http.newCall(req).execute()) {
            // Ignore response, data is logged already now.
        } catch (IOException e) {
            throw new ObligationFulfilmentFailedException("Failed to submit access to log server!");
        }
    }

    @Override
    public String getType() {
        return "LogObligation";
    }
}
