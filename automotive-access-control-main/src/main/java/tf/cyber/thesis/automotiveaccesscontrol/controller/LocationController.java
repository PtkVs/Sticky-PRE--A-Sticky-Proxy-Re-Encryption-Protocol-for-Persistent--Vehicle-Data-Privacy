package tf.cyber.thesis.automotiveaccesscontrol.controller;

import com.fasterxml.jackson.annotation.JsonRawValue;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tf.cyber.thesis.automotiveaccesscontrol.pep.XACMLEvaluationException;
import tf.cyber.thesis.automotiveaccesscontrol.pep.annotation.XACMLAccessControl;

import java.io.IOException;

@RestController
@XACMLAccessControl
public class LocationController {
    @Autowired
    OkHttpClient httpClient;

    @Autowired
    org.springframework.core.env.Environment env;

    @RequestMapping(value="/vehicle/location", produces="application/json")
    @JsonRawValue
    public String getLocation() {
        Request locationRequest = new Request.Builder()
                .url(env.getProperty("gps.location"))
                .get()
                .build();

        try (Response response = httpClient.newCall(locationRequest).execute()) {
            JSONObject res = new JSONObject(response.body().string());
            return res.toString();

        } catch (IOException e) {
            throw new XACMLEvaluationException();
        }
    }
}
