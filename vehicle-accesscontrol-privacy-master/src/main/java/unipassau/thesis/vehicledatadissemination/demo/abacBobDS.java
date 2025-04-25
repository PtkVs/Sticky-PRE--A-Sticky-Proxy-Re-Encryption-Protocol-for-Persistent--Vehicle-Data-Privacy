

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

public class abacBobDS {
    private static Logger LOG = LoggerFactory
            .getLogger(abacBobDS.class);

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
                    System.out.println("Decrypted JSON response for count " + count + ": ");
                    System.out.println(jsonObject.toString(4));


                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(); // Add spacing between outputs for different counts
            }
        }
    }
}