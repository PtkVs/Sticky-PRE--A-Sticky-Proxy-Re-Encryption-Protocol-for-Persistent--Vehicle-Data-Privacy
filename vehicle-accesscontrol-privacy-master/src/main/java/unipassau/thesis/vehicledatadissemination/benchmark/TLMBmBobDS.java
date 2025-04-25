//For Single Counts
package unipassau.thesis.vehicledatadissemination.benchmark;

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

public class TLMBmBobDS {

    private static Logger LOG = LoggerFactory.getLogger(TLMBmBobDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";
    public static String serverUrl = "http://localhost:8080/";
    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";
    public static byte[] data = null;
    public static String res = "";

    // Cumulative Metrics
    private static final AtomicLong totalDecryptionTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalDecryptionLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalExecutionLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalMemoryConsumed = new AtomicLong(0);
    private static final AtomicLong totalHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong totalNonHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong requestCount = new AtomicLong(0);

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
        return new OkHttpClient.Builder().authenticator((route, response) -> {
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder().header("Authorization", credential).build();
        }).build();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OkHttpClient httpClient = createAuthenticatedClient("bob", "bob");

        while (true) {
            System.out.print("Please Enter the Encrypted Count to Decrypt: ");
            int count = scanner.nextInt();

            int numberOfRuns = 10000;  // Number of times to repeat the process
            long totalRunTime = 0;  // To calculate total time for all runs

            for (int run = 1; run <= numberOfRuns; run++) {
                long runStartTime = System.nanoTime();

                processRecord(count, httpClient);

                long runEndTime = System.nanoTime();
                long runTimeMillis = (runEndTime - runStartTime) / 1_000_000;
                totalRunTime += runTimeMillis;
            }

            // Calculate cumulative metrics

            long averageDecryptionTimePerRecord = requestCount.get() > 0 ? totalDecryptionTimeMillis.get() / requestCount.get() : 0;
            long averageProcessingTimePerRecord = requestCount.get() > 0 ? totalProcessingTimeMillis.get() / requestCount.get() : 0;
            long averageRunTime = totalRunTime / numberOfRuns;

            long totalHeapMemoryUsedCumulative = totalHeapMemoryUsed.get();
            long totalNonHeapMemoryUsedCumulative = totalNonHeapMemoryUsed.get();

            long totalExecutionLatency = totalRunTime - totalProcessingTimeMillis.get();
            long totalProcessingLatency = totalProcessingLatencyMillis.get();
            long totalDecryptionLatency = totalExecutionLatency - totalProcessingLatency;

            long avgMemoryConsumedMB = requestCount.get() > 0
                    ? (totalMemoryConsumed.get() / requestCount.get()) / (1024 * 1024)
                    : 0; // Prevent division by zero

            LOG.info("Cumulative Metrics Across" + " " +numberOfRuns+ " " +  "Runs ({} records total):", requestCount.get());
            LOG.info("  - Total Decryption Time: {} ms, Average Decryption Time per Record: {} ms",
                    totalDecryptionTimeMillis.get(), averageDecryptionTimePerRecord);
            LOG.info("  - Total Processing Time: {} ms, Average Processing Time per Record: {} ms",
                    totalProcessingTimeMillis.get(), averageProcessingTimePerRecord);
          //  LOG.info("  - Total Execution Time : {} ms, Average Execution Time per Run: {} ms",
           //         totalRunTime, averageRunTime);

            LOG.info("  - Total Decryption Latency: {} ms", totalDecryptionLatency);
            LOG.info("  - Total Processing Latency (Excluding Decryption): {} ms", totalProcessingLatency);
         //   LOG.info("  - Total Execution Latency (Including Overhead): {} ms", totalExecutionLatency);

            LOG.info("  - Total Memory Consumed: {} MB (Heap: {} MB, Non-Heap: {} MB), Average Memory Consumed: {} MB",
                    totalMemoryConsumed.get() / (1024 * 1024),
                    totalHeapMemoryUsedCumulative / (1024 * 1024),
                    totalNonHeapMemoryUsedCumulative / (1024 * 1024),
                    avgMemoryConsumedMB);
        }
    }

    private static void processRecord(int count, OkHttpClient httpClient) {
        try (FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin"))) {
            byte[] stickyDocument = read.readAllBytes();

            Request reEncryptionRequest = new Request.Builder()
                    .url(serverUrl + "authorize?count=" + count)
                    .post(RequestBody.create(stickyDocument))
                    .build();

            long startProcessingTime = System.nanoTime();  // Start of processing phase

           // logMemoryUsage("Before decryption");

            long decryptionStartTime;
            long decryptionEndTime;

            try (Response response = httpClient.newCall(reEncryptionRequest).execute()) {
                data = response.body().bytes();
                Files.write(Path.of(tmpFolder + count), data);

                // Start decryption process
                decryptionStartTime = System.nanoTime();
                res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);
                decryptionEndTime = System.nanoTime();

                // Calculate decryption time
                long decryptionTimeMillis = (decryptionEndTime - decryptionStartTime) / 1_000_000;
                totalDecryptionTimeMillis.addAndGet(decryptionTimeMillis);
            }

            long endProcessingTime = System.nanoTime();

            // Calculate processing time
            long processingTimeMillis = (endProcessingTime - startProcessingTime) / 1_000_000;
            totalProcessingTimeMillis.addAndGet(processingTimeMillis);

            logMemoryUsage();

            requestCount.incrementAndGet();

        } catch (IOException e) {
            LOG.error("Error processing record {}", count, e);
        }
    }

    private static void logMemoryUsage() {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        long heapMemoryUsed = heapMemoryUsage.getUsed();
        long nonHeapMemoryUsed = nonHeapMemoryUsage.getUsed();
        long totalUsedMemory = heapMemoryUsed + nonHeapMemoryUsed;

        totalMemoryConsumed.addAndGet(totalUsedMemory);
        totalHeapMemoryUsed.addAndGet(heapMemoryUsed);
        totalNonHeapMemoryUsed.addAndGet(nonHeapMemoryUsed);

      // LOG.info("{} - Memory usage (Heap: {} MB, Non-Heap: {} MB)",
        //        heapMemoryUsed / (1024 * 1024), nonHeapMemoryUsed / (1024 * 1024));
    }
}




//For Multiple Counts
/*
package unipassau.thesis.vehicledatadissemination.benchmark;

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

public class TLMBmBobDS {

    private static Logger LOG = LoggerFactory.getLogger(TLMBmBobDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String privateKey = cryptoFolder + "bob-private-key";
    public static String tmpFolder = System.getProperty("user.dir") + "/tmp/";
    public static String serverUrl = "http://localhost:8080/";
    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";
    public static byte[] data = null;
    public static String res = "";

    // Cumulative Metrics
    private static final AtomicLong totalDecryptionTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalDecryptionLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalExecutionLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalMemoryConsumed = new AtomicLong(0);
    private static final AtomicLong totalHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong totalNonHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong requestCount = new AtomicLong(0);

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
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

            int numberOfRuns = 1;  // Number of times to repeat the process
            long totalRunTime = 0;  // To calculate total time for all runs

            for (int run = 1; run <= numberOfRuns; run++) {
                LOG.info("Starting run {} of {}", run, numberOfRuns);

                long runStartTime = System.nanoTime();

                for (int count = startCount; count <= endCount; count++) {
                    processRecord(count, httpClient);
                }

                long runEndTime = System.nanoTime();
                long runTimeMillis = (runEndTime - runStartTime) / 1_000_000;
                totalRunTime += runTimeMillis;

                LOG.info("Execution time for run {}: {} ms", run, runTimeMillis);
            }

            // Calculate cumulative metrics
            long averageDecryptionTimePerRecord = totalDecryptionTimeMillis.get() / requestCount.get();
            long averageProcessingTimePerRecord = totalProcessingTimeMillis.get() / requestCount.get();
            long averageRunTime = totalRunTime / numberOfRuns;

            long totalHeapMemoryUsedCumulative = totalHeapMemoryUsed.get();
            long totalNonHeapMemoryUsedCumulative = totalNonHeapMemoryUsed.get();

            long totalExecutionLatency = totalRunTime - totalProcessingTimeMillis.get();
            long totalProcessingLatency = totalProcessingLatencyMillis.get();
            long totalDecryptionLatency = totalExecutionLatency - totalProcessingLatency;

            LOG.info("Cumulative Metrics Across All Runs ({} records total):", requestCount.get());
            LOG.info("  - Total Decryption Time: {} ms, Average Decryption Time per Record: {} ms",
                    totalDecryptionTimeMillis.get(), averageDecryptionTimePerRecord);
            LOG.info("  - Total Processing Time: {} ms, Average Processing Time per Record: {} ms",
                    totalProcessingTimeMillis.get(), averageProcessingTimePerRecord);
            LOG.info("  - Total Execution Time : {} ms, Average Execution Time per Run: {} ms",
                    totalRunTime, averageRunTime);

            LOG.info("  - Total Decryption Latency: {} ms", totalDecryptionLatency);
            LOG.info("  - Total Processing Latency (Excluding Decryption): {} ms", totalProcessingLatency);
            LOG.info("  - Total Execution Latency (Including Overhead): {} ms", totalExecutionLatency);

            LOG.info("  - Total Memory Consumed: {} MB (Heap: {} MB, Non-Heap: {} MB), Average Memory Consumed: {} MB",
                    totalMemoryConsumed.get() / 1024 / 1024,
                    totalHeapMemoryUsedCumulative / 1024 / 1024,
                    totalNonHeapMemoryUsedCumulative / 1024 / 1024,
                    (totalMemoryConsumed.get() / requestCount.get()) / 1024 / 1024);

        }
    }

    private static void processRecord(int count, OkHttpClient httpClient) {
        try (FileInputStream read = new FileInputStream(new File(hashFolder + count + ".bin"))) {
            byte[] stickyDocument = read.readAllBytes();

            Request reEncryptionRequest = new Request.Builder()
                    .url(serverUrl + "benchmark?count=" + count)
                    .post(RequestBody.create(stickyDocument))
                    .build();

            long startProcessingTime = System.nanoTime();  // Start of processing phase

            logMemoryUsage("Before decryption");

            long decryptionStartTime = 0;
            long decryptionEndTime = 0;
            long processingTimeMillis = 0;
            long processingLatencyMillis = 0;
            long decryptionLatencyMillis = 0;

            try (Response response = httpClient.newCall(reEncryptionRequest).execute()) {
                data = response.body().bytes();
                Files.write(Path.of(tmpFolder + count), data);

                // Start decryption process
                decryptionStartTime = System.nanoTime();
                res = OpenPRE.INSTANCE.decrypt(privateKey, tmpFolder + count);
                decryptionEndTime = System.nanoTime();

                // Calculate decryption latency (delay between the processing phase and decryption start)
                decryptionLatencyMillis = (decryptionStartTime - startProcessingTime) / 1_000_000;
                totalDecryptionLatencyMillis.addAndGet(decryptionLatencyMillis); // Cumulative decryption latency

                // Calculate decryption time and add it to the cumulative decryption time
                long decryptionTimeMillis = (decryptionEndTime - decryptionStartTime) / 1_000_000;
                totalDecryptionTimeMillis.addAndGet(decryptionTimeMillis); // Cumulative decryption time

                JSONObject jsonObject = new JSONObject(res);
                LOG.info("Decrypted JSON response for count {}: {}", count, jsonObject.toString(4));
            }

            long endProcessingTime = System.nanoTime(); // End of processing phase

            // Calculate processing latency (delay between the end of decryption and total execution end)
            processingLatencyMillis = (endProcessingTime - decryptionEndTime) / 1_000_000;
            totalProcessingLatencyMillis.addAndGet(processingLatencyMillis); // Cumulative processing latency

            // Calculate total processing time (which includes network time, I/O time, and other processing excluding decryption)
            processingTimeMillis = (endProcessingTime - startProcessingTime) / 1_000_000;
            totalProcessingTimeMillis.addAndGet(processingTimeMillis); // Cumulative processing time

            // Calculate total execution latency (sum of decryption latency and processing latency)
            long totalExecutionLatency = decryptionLatencyMillis + processingLatencyMillis;
            totalExecutionLatencyMillis.addAndGet(totalExecutionLatency); // Cumulative execution latency

            logMemoryUsage("After decryption");

            requestCount.incrementAndGet();

        } catch (IOException e) {
            LOG.error("Error processing record {}", count, e);
        }
    }



    private static void logMemoryUsage(String phase) {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        long heapMemoryUsedMB = heapMemoryUsage.getUsed() / (1024 * 1024);
        long nonHeapMemoryUsedMB = nonHeapMemoryUsage.getUsed() / (1024 * 1024);

        long memoryUsed = heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed();
        totalMemoryConsumed.addAndGet(memoryUsed);

        totalHeapMemoryUsed.addAndGet(heapMemoryUsage.getUsed());
        totalNonHeapMemoryUsed.addAndGet(nonHeapMemoryUsage.getUsed());

        LOG.info("{} - Memory usage (Heap: {} MB, Non-Heap: {} MB)", phase, heapMemoryUsedMB, nonHeapMemoryUsedMB);
    }
}
*/