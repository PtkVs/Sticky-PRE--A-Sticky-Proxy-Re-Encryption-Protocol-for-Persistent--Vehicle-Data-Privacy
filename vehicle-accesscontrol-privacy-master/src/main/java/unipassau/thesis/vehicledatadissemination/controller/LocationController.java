package unipassau.thesis.vehicledatadissemination.controller;

import com.fasterxml.jackson.annotation.JsonRawValue;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class LocationController {
    OkHttpClient httpClient = new OkHttpClient();

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
            throw new NullPointerException();
        }
    }


}
