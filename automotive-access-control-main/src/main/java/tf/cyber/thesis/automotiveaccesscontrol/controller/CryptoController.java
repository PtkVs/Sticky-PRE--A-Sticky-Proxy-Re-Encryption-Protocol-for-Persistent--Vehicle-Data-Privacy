package tf.cyber.thesis.automotiveaccesscontrol.controller;


import com.fasterxml.jackson.annotation.JsonRawValue;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tf.cyber.thesis.automotiveaccesscontrol.AutomotiveAccessControlApplication;
import tf.cyber.thesis.automotiveaccesscontrol.config.PREConfiguration;
import tf.cyber.thesis.automotiveaccesscontrol.util.OpenPRE;

import java.io.IOException;

@RestController
public class CryptoController {

    private static Logger LOG = LoggerFactory
            .getLogger(AutomotiveAccessControlApplication.class);

    OkHttpClient httpClient = new OkHttpClient();

    @Autowired
    org.springframework.core.env.Environment env;

    @Autowired
    PREConfiguration preConfiguration;

    @RequestMapping("/crypto/generateCryptoContext/{filename}")
    @ResponseBody
    public String generateCryptoContext(@PathVariable String filename) {

        OpenPRE.INSTANCE.cryptoContextGen(preConfiguration.getSchemeName(),
                preConfiguration.getCryptoFolder(),
                filename,
                preConfiguration.getPlainttextModulus(),
                preConfiguration.getMultiplicativeDepth() );


        return preConfiguration.getCryptoFolder()+filename;
    }
    @RequestMapping("/crypto/generateKeyPairs/{cryptoContext}/{filename}")
    @ResponseBody
    public String generateKeyPairs(@PathVariable String filename, @PathVariable String cryptoContext) {
        OpenPRE.INSTANCE.keysGen(preConfiguration.getCryptoFolder(), cryptoContext, filename);

        return preConfiguration.getCryptoFolder()+filename;
    }

    @RequestMapping(value="/vehicle/enclocation", produces="application/json")
    @JsonRawValue
    public String getLocation() {
        Request locationRequest = new Request.Builder()
                .url(env.getProperty("gps.location"))
                .get()
                .build();

        try (Response response = httpClient.newCall(locationRequest).execute()) {
            JSONObject res = new JSONObject(response.body().string());
            String a  = OpenPRE.INSTANCE.Encrypt( "alice-public-key.json",
                    res.toString(),
                    preConfiguration.getCryptoFolder(),
                    "omar.json");

            return a;

        } catch (IOException e) {
            throw new NullPointerException();
        }
    }


}
