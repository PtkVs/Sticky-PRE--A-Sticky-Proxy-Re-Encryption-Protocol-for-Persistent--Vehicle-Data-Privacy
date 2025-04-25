package unipassau.thesis.vehicledatadissemination.demo;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Alice {

    private static Logger LOG = LoggerFactory
            .getLogger(Alice.class);

    public static String cryptoFolder = System.getProperty("user.dir")+"/crypto/";
    public static String dataFolder = System.getProperty("user.dir")+"/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String policyFolder = System.getProperty("user.dir")+"/policies/";



    public static String gpsUrl = "http://localhost:8081/";

    public static int count= 77;


    public static JSONObject res = new JSONObject();
    public static String randomPlaintext;


    public static void main(String[] args) {

        OkHttpClient httpClient = new OkHttpClient();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                Request locationRequest = new Request.Builder()
                        .url(gpsUrl)
                        .get()
                        .build();

                try (Response response = httpClient.newCall(locationRequest).execute()) {
                    res = new JSONObject(response.body().string());

                } catch (IOException e) {
                    //Change -commented below line because we were getting an error
                    throw new NullPointerException();
                }

                System.out.print(res.toString(4) + " ");
                //Change - If no GPS data then we generate Random data below - comment below line when using real data
                //randomPlaintext= RandomStringUtils.randomAlphanumeric(8192);



                LOG.info("Encrypting Data ...");
                OpenPRE.INSTANCE.encrypt(pubKey, res.toString(), dataFolder + count);


                LOG.info("Sticking hash of the policy to the data ...");
                DataHandler.writer(policyFolder + count + ".xml", dataFolder + count, count);

                //Change - including Random generated data as a parameter to the encrypt function - comment below line when using real data
                //OpenPRE.INSTANCE.encrypt(pubKey, randomPlaintext, dataFolder + count);



                if (++count>= Integer.parseInt(args[0]) ) {
                    exec.shutdown();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

    }
}






