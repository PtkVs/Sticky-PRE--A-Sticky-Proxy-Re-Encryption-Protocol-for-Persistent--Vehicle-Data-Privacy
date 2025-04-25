package unipassau.thesis.vehicledatadissemination.benchmark;

import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class LatencyBmBobDS {

    private static final Logger LOG = LoggerFactory.getLogger(LatencyBmBobDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";

    public static String serverUrl = "http://localhost:8080/";

    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";

    private static byte[] data = null;
    private static String res = "";

    private static OkHttpClient createAuthenticatedClient(final String username, final String password) {
        // Build client with authentication information
        return new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
    }

    public static void main(String[] args) {
        // Getting user input for the range
        Scanner scanner = new Scanner(System.in);

        OkHttpClient httpClient = createAuthenticatedClient("bob", "bob");

        while (true) {
            System.out.print("Please Enter the Start of the Encrypted Count Range: ");
            int startCount = scanner.nextInt();

            System.out.print("Please Enter the End of the Encrypted Count Range: ");
            int endCount = scanner.nextInt();

            int numberOfRuns = 10;  // Number of times to repeat the process
            long totalDecryptionLatency = 0;  // To store total decryption latency across all runs
            int totalRecordsProcessed = 0;  // Total number of records processed across all runs

            LOG.info("Starting latency benchmark for Bob (decryption).");

            for (int run = 0; run < numberOfRuns; run++) {
                LOG.info("Starting run " + (run + 1) + " of " + numberOfRuns);

                int recordsProcessedThisRun = 0;

                for (int count = startCount; count <= endCount; count++) {
                    byte[] stickyDocument = null;

                    try {
                        FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin")); // Reading just hash
                        stickyDocument = read.readAllBytes();
                    } catch (IOException e) {
                        LOG.error("Error reading hash file for count " + count, e);
                        continue;
                    }

                    // Query Parameter `?count=`
                    Request reEncryptionRequest = new Request.Builder()
                            .url(serverUrl + "benchmark?count=" + count)
                            .post(RequestBody.create(stickyDocument))
                            .build();

                    try (Response response = httpClient.newCall(reEncryptionRequest).execute()) {
                        data = response.body().bytes();
                        Files.write(Path.of(tmpFolder + count), data);

                        // Measure decryption latency
                        LOG.info("Decrypting data for count " + count + "...");
                        long decryptionStartTime = System.nanoTime(); // Start time before decryption
                        res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);
                        long decryptionEndTime = System.nanoTime(); // End time after decryption
                        long decryptionLatency = (decryptionEndTime - decryptionStartTime) / 1_000_000; // Convert to milliseconds
                        totalDecryptionLatency += decryptionLatency;

                        LOG.info("Decryption latency for count " + count + ": " + decryptionLatency + " milliseconds");

                        JSONObject jsonObject = new JSONObject(res);
                        System.out.println("Decrypted JSON response for count " + count + ": ");
                        System.out.println(jsonObject.toString(4)); // Pretty-print JSON

                        recordsProcessedThisRun++;
                    } catch (IOException e) {
                        LOG.error("Error during decryption or request for count " + count, e);
                    }

                    System.out.println(); // Add spacing between outputs for different counts
                }

                totalRecordsProcessed += recordsProcessedThisRun;

                LOG.info("Total records processed in run " + (run + 1) + ": " + recordsProcessedThisRun);
            }

            // Calculate average decryption latency per record
            long averageDecryptionLatency = totalRecordsProcessed > 0
                    ? totalDecryptionLatency / totalRecordsProcessed
                    : 0;

            // Log the total and average latencies
            LOG.info("Total decryption latency for all runs: " + totalDecryptionLatency + " milliseconds");
            LOG.info("Average decryption latency per record: " + averageDecryptionLatency + " milliseconds");
            LOG.info("Total number of records processed: " + totalRecordsProcessed);
            LOG.info("Total number of runs: " + numberOfRuns);
        }
    }
}



/* //Original Code
package unipassau.thesis.vehicledatadissemination.benchmark;

import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class LatencyBmBobDS {
    private static Logger LOG = LoggerFactory.getLogger(LatencyBmBobDS.class);

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
        // Build client with authentication information
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

        long totalLatency = 0;  // Variable to accumulate total latency

        while (true) {
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
                    // Reading just the hash
                    FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin"));
                    stickyDocument = read.readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                // Measure latency for re-encryption and decryption
                long reEncryptionStartTime = System.nanoTime();  // Start time for re-encryption request
                Request reEncryptionRequest = new Request.Builder()
                        .url(serverUrl + "benchmark?count=" + count)
                        .post(RequestBody.create(stickyDocument))
                        .build();

                try (Response response = httpClient.newCall(reEncryptionRequest).execute()) {
                    long reEncryptionEndTime = System.nanoTime();  // End time for re-encryption request
                    long reEncryptionLatency = (reEncryptionEndTime - reEncryptionStartTime) / 1_000_000;  // Convert to milliseconds
                    LOG.info("Re-encryption latency for count " + count + ": " + reEncryptionLatency + " milliseconds");

                    data = response.body().bytes();
                    Files.write(Path.of(tmpFolder + count), data);

                    // Measure latency for decryption
                    long decryptionStartTime = System.nanoTime();  // Start time before decryption
                    res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);
                    long decryptionEndTime = System.nanoTime();  // End time after decryption
                    long decryptionLatency = (decryptionEndTime - decryptionStartTime) / 1_000_000;  // Convert to milliseconds
                    LOG.info("Decryption latency for count " + count + ": " + decryptionLatency + " milliseconds");

                    // Add both latencies to total latency
                    long totalCountLatency = reEncryptionLatency + decryptionLatency;
                    totalLatency += totalCountLatency;

                    LOG.info("Decrypted response for count " + count + " is: " + res);

                    JSONObject jsonObject = new JSONObject(res);
                    System.out.println("Decrypted JSON response for count " + count + ": ");
                    System.out.println(jsonObject.toString(4));  // Pretty-print JSON

                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println();  // Add spacing between outputs for different counts
            }

            // After each loop, log the total latency for all counts processed so far
            LOG.info("Total latency for all decrypted counts so far: " + totalLatency + " milliseconds");
        }
    }
}
*/