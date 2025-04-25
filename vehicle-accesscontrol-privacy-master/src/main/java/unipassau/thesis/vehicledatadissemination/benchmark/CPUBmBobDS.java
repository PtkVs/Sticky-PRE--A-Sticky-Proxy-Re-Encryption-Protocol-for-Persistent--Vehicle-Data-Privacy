package unipassau.thesis.vehicledatadissemination.benchmark;

import com.sun.management.OperatingSystemMXBean;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

public class CPUBmBobDS {

    private static Logger LOG = LoggerFactory.getLogger(CPUBmBobDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";
    public static String serverUrl = "http://localhost:8080/";
    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";
    public static byte[] data = null;
    public static String res = "";

    // Cumulative Metrics

    private static final AtomicLong requestCount = new AtomicLong(0);



    // OperatingSystemMXBean for CPU usage tracking
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private static OkHttpClient createAuthenticatedClient(final String username, final String password) {
        return new OkHttpClient.Builder().authenticator((route, response) -> {
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder().header("Authorization", credential).build();
        }).build();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OkHttpClient httpClient = createAuthenticatedClient("bob", "bob");

        while (true) {
            System.out.print("Please Enter the Start of the Encrypted Count Range: ");
            int count = scanner.nextInt();



            int numberOfRuns = 10000;  // Number of times to repeat the process

            double maxCpuLoad = Double.MIN_VALUE;
            double minCpuLoad = Double.MAX_VALUE;
            double totalCpuLoad = 0;
            int cpuLoadSamples = 0;

            // Number of available processors (cores)
            int availableProcessors = osBean.getAvailableProcessors();
            //  LOG.info("Available processors: {}", availableProcessors);

            for (int run = 1; run <= numberOfRuns; run++) {
                //     LOG.info("Starting run {} of {}", run, numberOfRuns);


                long lastTimestamp = System.nanoTime();
                long interval = 100000000L;


                    processRecord(count, httpClient);

                    // Track CPU usage at intervals
                    long elapsedTime = System.nanoTime() - lastTimestamp;
                    if (elapsedTime > interval) {
                        double currentCpuLoad = osBean.getSystemCpuLoad() * 100; // Convert to percentage
                        currentCpuLoad /= availableProcessors; // Adjust for multi-core systems

                        maxCpuLoad = Math.max(maxCpuLoad, currentCpuLoad);

                        if (currentCpuLoad > 0) { // Ignore zero CPU load values
                            minCpuLoad = Math.min(minCpuLoad, currentCpuLoad);
                        }

                        totalCpuLoad += currentCpuLoad;
                        cpuLoadSamples++;
                        lastTimestamp = System.nanoTime();
                    }



            }

            // Calculate CPUload metrics
            double averageCpuLoad = cpuLoadSamples > 0 ? totalCpuLoad / cpuLoadSamples : 0;
            LOG.info("  - Max CPU Load: {}%", String.format("%.2f", maxCpuLoad));
            LOG.info("  - Min CPU Load: {}%", minCpuLoad == Double.MAX_VALUE ? "Not measured during this run" : String.format("%.2f", minCpuLoad));
            LOG.info("  - Average CPU Load: {}%", String.format("%.2f", averageCpuLoad));



        }
    }

    private static void processRecord(int count, OkHttpClient httpClient) {
        try (FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin"))) {
            byte[] stickyDocument = read.readAllBytes();

            Request reEncryptionRequest = new Request.Builder()
                    .url(serverUrl + "authorize?count=" + count)
                    .post(RequestBody.create(stickyDocument))
                    .build();

            try (Response response = httpClient.newCall(reEncryptionRequest).execute()) {
                data = response.body().bytes();
                Files.write(Path.of(tmpFolder + count), data);
                res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);

             //  JSONObject jsonObject = new JSONObject(res);
              //       LOG.info("Decrypted JSON response for count {}: {}", count, jsonObject.toString(4));
            }





        } catch (IOException e) {
            LOG.error("Error processing record {}", count, e);
        }
    }
}


//*******This is for BM of CPU usage, decryption but without ABAC******
/*
package unipassau.thesis.vehicledatadissemination.benchmark;

import com.sun.management.OperatingSystemMXBean;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class CPUBmBobDS {

    private static Logger LOG = LoggerFactory.getLogger(CPUBmBobDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";
    public static String serverUrl = "http://localhost:8080/";

    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";

    public static byte[] data = null;
    public static String res = "";

    // OperatingSystemMXBean for CPU usage tracking
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private static OkHttpClient createAuthenticatedClient(final String username, final String password) {
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

            System.out.print("Please Enter the Number of Runs: ");
            int numberOfRuns = scanner.nextInt();

            double maxCpuLoad = Double.MIN_VALUE;
            double minCpuLoad = Double.MAX_VALUE;
            double totalCpuLoad = 0;
            int cpuLoadSamples = 0;

            // Number of available processors (cores)
            int availableProcessors = osBean.getAvailableProcessors();
            LOG.info("Available processors: " + availableProcessors);

            // Running the process for the specified number of runs
            for (int run = 0; run < numberOfRuns; run++) {
                LOG.info("Starting run " + (run + 1) + " of " + numberOfRuns);

                long runStartTime = System.nanoTime(); // Start time for this run
                long lastTimestamp = System.nanoTime();
                long interval = 100000000L; // 1 second interval (in nanoseconds)

                for (int count = startCount; count <= endCount; count++) {
                    byte[] stickyDocument = null;
                    try {
                        FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin")); // Reading hash
                        stickyDocument = read.readAllBytes();
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    Request reEncryptionRequest = new Request.Builder()
                            .url(serverUrl + "benchmark?count=" + count)
                            .post(RequestBody.create(stickyDocument))
                            .build();

                    try (Response response = httpClient.newCall(reEncryptionRequest).execute()) {
                        data = response.body().bytes();
                        Files.write(Path.of(tmpFolder + count), data);
                        res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);
                        LOG.info("Decrypted response for count " + count + " is: " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        System.out.println("Decrypted JSON response for count " + count + ": ");
                        System.out.println(jsonObject.toString(4)); // Pretty-print JSON

                        // Track CPU usage at intervals
                        long elapsedTime = System.nanoTime() - lastTimestamp;
                        if (elapsedTime > interval) {
                            double currentCpuLoad = osBean.getSystemCpuLoad() * 100; // Convert to percentage
                            currentCpuLoad /= availableProcessors; // Adjust for multi-core systems

                            maxCpuLoad = Math.max(maxCpuLoad, currentCpuLoad);

                            if (currentCpuLoad > 0) { // Ignore zero CPU load values
                                minCpuLoad = Math.min(minCpuLoad, currentCpuLoad);
                            }

                            totalCpuLoad += currentCpuLoad;
                            cpuLoadSamples++;
                            lastTimestamp = System.nanoTime();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println(); // Add spacing between outputs for different counts
                }

                long runEndTime = System.nanoTime();
                LOG.info("Run " + (run + 1) + " completed in " + (runEndTime - runStartTime) / 1_000_000 + " milliseconds.");
            }

            // Calculate and log average CPU load
            double averageCpuLoad = cpuLoadSamples > 0 ? totalCpuLoad / cpuLoadSamples : 0;
            LOG.info("Max CPU Load: " + String.format("%.2f", maxCpuLoad) + "%");
            LOG.info("Min CPU Load: " + (minCpuLoad == Double.MAX_VALUE ? "Not measured during this run" : String.format("%.2f", minCpuLoad) + "%"));
            LOG.info("Average CPU Load: " + String.format("%.2f", averageCpuLoad) + "%");
            LOG.info("Total Number of Runs: " + numberOfRuns);
        }
    }
}

/* ORIGINAL CODES ********/
/*
// *********BM of CPU usage, decryption with ABAC**********
package unipassau.thesis.vehicledatadissemination.benchmark;

import com.sun.management.OperatingSystemMXBean;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

public class CPUBmBobDS {

    private static Logger LOG = LoggerFactory.getLogger(CPUBmBobDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";
    public static String serverUrl = "http://localhost:8080/";
    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";
    public static byte[] data = null;
    public static String res = "";

    // Cumulative Metrics

    private static final AtomicLong requestCount = new AtomicLong(0);



    // OperatingSystemMXBean for CPU usage tracking
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private static OkHttpClient createAuthenticatedClient(final String username, final String password) {
        return new OkHttpClient.Builder().authenticator((route, response) -> {
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder().header("Authorization", credential).build();
        }).build();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OkHttpClient httpClient = createAuthenticatedClient("bob", "bob");

        while (true) {
            System.out.print("Please Enter the Start of the Encrypted Count Range: ");
            int startCount = scanner.nextInt();

            System.out.print("Please Enter the End of the Encrypted Count Range: ");
            int endCount = scanner.nextInt();

            int numberOfRuns = 10000;  // Number of times to repeat the process

            double maxCpuLoad = Double.MIN_VALUE;
            double minCpuLoad = Double.MAX_VALUE;
            double totalCpuLoad = 0;
            int cpuLoadSamples = 0;

            // Number of available processors (cores)
            int availableProcessors = osBean.getAvailableProcessors();
          //  LOG.info("Available processors: {}", availableProcessors);

            for (int run = 1; run <= numberOfRuns; run++) {
           //     LOG.info("Starting run {} of {}", run, numberOfRuns);


                long lastTimestamp = System.nanoTime();
                long interval = 100000000L;

                for (int count = startCount; count <= endCount; count++) {
                    processRecord(count, httpClient);

                    // Track CPU usage at intervals
                    long elapsedTime = System.nanoTime() - lastTimestamp;
                    if (elapsedTime > interval) {
                        double currentCpuLoad = osBean.getSystemCpuLoad() * 100; // Convert to percentage
                        currentCpuLoad /= availableProcessors; // Adjust for multi-core systems

                        maxCpuLoad = Math.max(maxCpuLoad, currentCpuLoad);

                        if (currentCpuLoad > 0) { // Ignore zero CPU load values
                            minCpuLoad = Math.min(minCpuLoad, currentCpuLoad);
                        }

                        totalCpuLoad += currentCpuLoad;
                        cpuLoadSamples++;
                        lastTimestamp = System.nanoTime();
                    }
                }


            }

            // Calculate CPUload metrics
            double averageCpuLoad = cpuLoadSamples > 0 ? totalCpuLoad / cpuLoadSamples : 0;
            LOG.info("  - Max CPU Load: {}%", String.format("%.2f", maxCpuLoad));
            LOG.info("  - Min CPU Load: {}%", minCpuLoad == Double.MAX_VALUE ? "Not measured during this run" : String.format("%.2f", minCpuLoad));
            LOG.info("  - Average CPU Load: {}%", String.format("%.2f", averageCpuLoad));



        }
    }

    private static void processRecord(int count, OkHttpClient httpClient) {
        try (FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin"))) {
            byte[] stickyDocument = read.readAllBytes();

            Request reEncryptionRequest = new Request.Builder()
                    .url(serverUrl + "authorize?count=" + count)
                    .post(RequestBody.create(stickyDocument))
                    .build();

            try (Response response = httpClient.newCall(reEncryptionRequest).execute()) {
                data = response.body().bytes();
                Files.write(Path.of(tmpFolder + count), data);
                res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);

          //      JSONObject jsonObject = new JSONObject(res);
          //      LOG.info("Decrypted JSON response for count {}: {}", count, jsonObject.toString(4));
            }





        } catch (IOException e) {
            LOG.error("Error processing record {}", count, e);
        }
    }
}


//*******This is for BM of CPU usage, decryption but without ABAC******
/*
package unipassau.thesis.vehicledatadissemination.benchmark;

import com.sun.management.OperatingSystemMXBean;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class CPUBmBobDS {

    private static Logger LOG = LoggerFactory.getLogger(CPUBmBobDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";
    public static String serverUrl = "http://localhost:8080/";

    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";

    public static byte[] data = null;
    public static String res = "";

    // OperatingSystemMXBean for CPU usage tracking
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private static OkHttpClient createAuthenticatedClient(final String username, final String password) {
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

            System.out.print("Please Enter the Number of Runs: ");
            int numberOfRuns = scanner.nextInt();

            double maxCpuLoad = Double.MIN_VALUE;
            double minCpuLoad = Double.MAX_VALUE;
            double totalCpuLoad = 0;
            int cpuLoadSamples = 0;

            // Number of available processors (cores)
            int availableProcessors = osBean.getAvailableProcessors();
            LOG.info("Available processors: " + availableProcessors);

            // Running the process for the specified number of runs
            for (int run = 0; run < numberOfRuns; run++) {
                LOG.info("Starting run " + (run + 1) + " of " + numberOfRuns);

                long runStartTime = System.nanoTime(); // Start time for this run
                long lastTimestamp = System.nanoTime();
                long interval = 100000000L; // 1 second interval (in nanoseconds)

                for (int count = startCount; count <= endCount; count++) {
                    byte[] stickyDocument = null;
                    try {
                        FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin")); // Reading hash
                        stickyDocument = read.readAllBytes();
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    Request reEncryptionRequest = new Request.Builder()
                            .url(serverUrl + "benchmark?count=" + count)
                            .post(RequestBody.create(stickyDocument))
                            .build();

                    try (Response response = httpClient.newCall(reEncryptionRequest).execute()) {
                        data = response.body().bytes();
                        Files.write(Path.of(tmpFolder + count), data);
                        res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);
                        LOG.info("Decrypted response for count " + count + " is: " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        System.out.println("Decrypted JSON response for count " + count + ": ");
                        System.out.println(jsonObject.toString(4)); // Pretty-print JSON

                        // Track CPU usage at intervals
                        long elapsedTime = System.nanoTime() - lastTimestamp;
                        if (elapsedTime > interval) {
                            double currentCpuLoad = osBean.getSystemCpuLoad() * 100; // Convert to percentage
                            currentCpuLoad /= availableProcessors; // Adjust for multi-core systems

                            maxCpuLoad = Math.max(maxCpuLoad, currentCpuLoad);

                            if (currentCpuLoad > 0) { // Ignore zero CPU load values
                                minCpuLoad = Math.min(minCpuLoad, currentCpuLoad);
                            }

                            totalCpuLoad += currentCpuLoad;
                            cpuLoadSamples++;
                            lastTimestamp = System.nanoTime();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println(); // Add spacing between outputs for different counts
                }

                long runEndTime = System.nanoTime();
                LOG.info("Run " + (run + 1) + " completed in " + (runEndTime - runStartTime) / 1_000_000 + " milliseconds.");
            }

            // Calculate and log average CPU load
            double averageCpuLoad = cpuLoadSamples > 0 ? totalCpuLoad / cpuLoadSamples : 0;
            LOG.info("Max CPU Load: " + String.format("%.2f", maxCpuLoad) + "%");
            LOG.info("Min CPU Load: " + (minCpuLoad == Double.MAX_VALUE ? "Not measured during this run" : String.format("%.2f", minCpuLoad) + "%"));
            LOG.info("Average CPU Load: " + String.format("%.2f", averageCpuLoad) + "%");
            LOG.info("Total Number of Runs: " + numberOfRuns);
        }
    }
}
*/