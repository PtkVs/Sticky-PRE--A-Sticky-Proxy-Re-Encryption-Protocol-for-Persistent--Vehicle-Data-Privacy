

package unipassau.thesis.vehicledatadissemination.demo;

import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class BobDS {
    private static Logger LOG = LoggerFactory
            .getLogger(BobDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";

    public static String serverUrl = "http://localhost:8080/";

    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";

    public static byte[] data = null;
    public static String res = "";

    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
        // build client with authentication information
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return httpClient;
    }

    public static void main(String[] args) {
        // Getting user input for the range
        Scanner scanner = new Scanner(System.in);
        OkHttpClient httpClient = createAuthenticatedClient("bob", "bob");

        while(true) {
            System.out.print("Please Enter the Start of the Encrypted Count Range: ");
            int startCount = scanner.nextInt();

            System.out.print("Please Enter the End of the Encrypted Count Range: ");
            int endCount = scanner.nextInt();

            // Check if the user wants to exit (0 0 input)
            if (startCount == 0 && endCount == 0) {
                System.out.println("Exiting program...");
                break;
            }


            // Loop through the count range
            for (int count = startCount; count <= endCount; count++) {
                byte[] stickyDocument = null;
                try {
                    // FileInputStream read = new FileInputStream(new File(dataFolder + count ));
                    FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin"));  // reading just hash
                    stickyDocument = read.readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                // ?count= Query Parameter
                Request reEncryptionRequest = new Request.Builder()
                        .url(serverUrl + "authorize?count=" + count)
                        .post(RequestBody.create(stickyDocument))
                        .build();

                try (Response response = httpClient.newCall(reEncryptionRequest).execute()) { // jump to DataAccessController
                    data = response.body().bytes();
                    Files.write(Path.of(tmpFolder + count), data);
                    res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);
                    LOG.info("Decrypted response for count " + count + " is: " + res);

                    JSONObject jsonObject = new JSONObject(res);
                    // ki yeha yesari garnu paryo data manipulation hoin vane re-encrypt garnu vanda agadi nai garnu paryo cz suru ma data Alice le encrypt garda sabbai data encrypt gareko hunxa and hash add hunxa, tesma attribute filter garera feri encrypt garnu parxa by alice then only re-encrypt garna milxa cz tyo vayena vane cerial exception auxa cz crypto keys match nai hudaina as encrypt vako binary file lai naya binary file banayo jasko kei link xaina with alice and tyo file lai re-encrypt garna pathauda cerial exception error ayo
                    // ????can make this dynamic ?? according to the request of xml automatically detect attributes and change the desired attribute instead of eg. header to lattitude and show in json format  ?????
                    JSONObject filteredResponse = new JSONObject();
                    if (jsonObject.has("header")) {
                        filteredResponse.put("header", jsonObject.get("header"));
                    }
                    if (jsonObject.has("timestamp")) {
                        filteredResponse.put("timestamp", jsonObject.get("timestamp"));
                    }
                    if (jsonObject.has("quality")) {
                        filteredResponse.put("quality", jsonObject.get("quality"));
                    }

                    // Print the filtered JSON response
                    System.out.println("Decrypted JSON response for count " + count + ": ");
                    System.out.println(filteredResponse.toString(4));  // Pretty-print filtered JSON


                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(); // Add spacing between outputs for different counts
            }
        }
    }
}



/*

//FOR SINGLE COUNT

package unipassau.thesis.vehicledatadissemination.demo;

import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BobDS {
    private static Logger LOG = LoggerFactory
            .getLogger(Bob.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";

    public static String serverUrl = "http://localhost:8080/";

    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";


    public static int count;

    public static byte[] data = null;
    public static String res = "";


    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
        // build client with authentication information.
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return httpClient;
    }

    public static void main(String[] args) {
        //Getting user input for count
        Scanner scanner = new Scanner(System.in);

        OkHttpClient httpClient = createAuthenticatedClient("bob", "bob");

        while (true) {
            System.out.print("Please Enter the Encrypted Count Number: ");
            count = scanner.nextInt();

           // ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            //exec.scheduleAtFixedRate(new Runnable() {
              //  @Override
                //public void run() {

                    byte[] stickyDocument = null;
                    try {
                        // FileInputStream read = new FileInputStream(new File(dataFolder + count ));
                        FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin"));  //reading just hash
                        stickyDocument = read.readAllBytes();
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

/*?count= Query Parameter
?count= is part of the query string that starts with a ?.
Query parameters are used to send additional data to the server. In this case, count is the name of the parameter, and its value is appended to it.
For example, if count is 5, then this part of the URL will be ?count=5.
Query parameters are added after the endpoint path and are separated by & if there are multiple parameters. Since there is only one parameter here, it starts with ?.*/
/*Request reEncryptionRequest = new Request.Builder()
        .url(serverUrl + "authorize?count=" + count)
        .post(RequestBody.create(stickyDocument))
        .build();

                    try (Response response = httpClient.newCall(reEncryptionRequest).execute()) { //yeha bata jump to DataAccesscontroller
data = response.body().bytes();
                        Files.write(Path.of(tmpFolder + count), data);
res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);
                        LOG.info("Decrypted response is:" + res);

JSONObject jsonObject = new JSONObject(res);
                        System.out.print(jsonObject.toString(4) + " ");

        } catch (IOException e) {
        //Change -commented below line because we were getting an error
        //throw new NullPointerException();
        e.printStackTrace();
                    }

                            // if (++count >= Integer.parseInt(args[0])) {
                            //    exec.shutdown();
                            System.out.println();
                    }
                            }
                            }//, 0, 1, TimeUnit.SECONDS);




*/
