package unipassau.thesis.vehicledatadissemination.services;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;

@Service
public class ProxyReEncryptionService {
    @Autowired
    org.springframework.core.env.Environment env;

    public byte[] reEncrypt(byte[] data, Principal principal){
        OkHttpClient httpClient = new OkHttpClient();
        Request locationRequest = new Request.Builder()
                .url(env.getProperty("pre.location") + "re-encrypt")  //calling pre server
                .post(RequestBody.create(data))
                .addHeader("subjectID", principal.getName())
                .build();



        try (Response response = httpClient.newCall(locationRequest).execute()) {
            byte[] result = response.body().bytes();

            return result;

        } catch (IOException e) {
            throw new NullPointerException();
        }
    }

}
