package unipassau.thesis.vehicledatadissemination.benchmark.Baseline;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class TLMNoEncAliceDS {

    private static final Logger LOG = LoggerFactory.getLogger(TLMNoEncAliceDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";
    public static String csvFilePath = System.getProperty("user.dir") + "/csv/DS1-1000.csv";
    public static int count = 0;

    // Benchmark metrics
    private static final AtomicLong totalEncryptionTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalEncryptionLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalMemoryConsumed = new AtomicLong(0);
    private static final AtomicLong totalHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong totalNonHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong requestCount = new AtomicLong(0);

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    public static void main(String[] args) {

        if (args.length == 0) {
            LOG.error("No argument provided for the number of records to process. Exiting.");
            System.exit(1);
        }

        int recordsToProcess;
        try {
            recordsToProcess = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            LOG.error("Invalid number format for the argument. Exiting.", e);
            System.exit(1);
            return;
        }

        int numberOfRuns = 10; // Number of times to repeat the process
        long totalRunTime = 0; // To calculate total time for all runs

        for (int run = 1; run <= numberOfRuns; run++) {
            LOG.info("Starting run {} of {}", run, numberOfRuns);

            long runStartTime = System.nanoTime();

            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                List<String[]> records = reader.readAll();
                LOG.info("Total records found: {}", records.size());

                int processedCount = 0;

                for (String[] record : records) {
                    if (processedCount >= recordsToProcess) break;

                    processRecord(record);
                    processedCount++;
                }

                // Add processed count for cumulative metrics
                requestCount.addAndGet(processedCount);

            } catch (IOException | CsvException e) {
                LOG.error("Error reading CSV file", e);
            }

            long runEndTime = System.nanoTime();
            long runTimeMillis = (runEndTime - runStartTime) / 1_000_000;
            totalRunTime += runTimeMillis;

            LOG.info("Time Taken for only Run {}: {} ms", run, runTimeMillis);
        }

        // Calculate cumulative averages
        long averageProcessingTimePerRecord = totalProcessingTimeMillis.get() / requestCount.get();
        long averageEncryptionTimePerRecord = totalEncryptionTimeMillis.get() / requestCount.get();
        long averageRunTime = totalRunTime / numberOfRuns;

        // Calculate total latencies
        long totalProcessingLatency = totalProcessingLatencyMillis.get();
        long totalExecutionLatency = totalRunTime - totalProcessingTimeMillis.get();
        long totalEncryptionLatency = totalExecutionLatency - totalProcessingLatency;

        //Cumulative Heap and Non heap
        long totalHeapMemoryUsedCumulative = totalHeapMemoryUsed.get();
        long totalNonHeapMemoryUsedCumulative = totalNonHeapMemoryUsed.get();

        // Log cumulative metrics across all runs
        LOG.info("Cumulative Metrics Across All Runs ({} records total):", requestCount.get());
        LOG.info("  - Total Processing Time: {} ms, Average Processing Time per Record: {} ms", totalProcessingTimeMillis.get(), averageProcessingTimePerRecord);
        LOG.info("  - Total Processing Latency (Excluding Encryption): {} ms", totalProcessingLatency);
        LOG.info("  - Total Encryption Time: {} ms, Average Encryption Time per Record: {} ms", totalEncryptionTimeMillis.get(), averageEncryptionTimePerRecord);
        LOG.info("  - Total Encryption Latency: {} ms", totalEncryptionLatency);

        // Log total memory consumed with accurate heap and non-heap values, and average memory consumed
        LOG.info("  - Total Memory Consumed: {} MB (Heap: {} MB, Non-Heap: {} MB), Average Memory Consumed: {} MB",
                totalMemoryConsumed.get() / 1024 / 1024,
                totalHeapMemoryUsedCumulative / 1024 / 1024,
                totalNonHeapMemoryUsedCumulative / 1024 / 1024,
                (totalMemoryConsumed.get() / requestCount.get()) / 1024 / 1024);

        LOG.info("  - Total Execution Time for All Runs: {} ms, Average Execution Time per Run: {} ms", totalRunTime, averageRunTime);
        LOG.info("  - Total Execution Latency (Including Overhead): {} ms", totalExecutionLatency);

    }


    private static void processRecord(String[] record) {
        JSONObject res = new JSONObject();
        res.put("header", record[0]);
        res.put("timestamp", record[1]);
        res.put("antennaAltitudeUnit", record[2]);
        res.put("antennaAltitude", record[3]);
        res.put("usedSatellites", record[4]);
        res.put("quality", record[5]);
        res.put("longitude", record[6]);
        res.put("latitude", record[7]);

        long startProcessingTime = System.nanoTime();

        logMemoryUsage("Before encryption");

        // Step 1: Encrypt the data
        LOG.info("Saving  Data..........................................");
        long encryptionStartTime = System.nanoTime();

        // Save JSON object to a file in the data folder
        String outputFilePath = dataFolder + "baseline_" + count + ".json";
        try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
            fileWriter.write(res.toString());
        } catch (IOException e) {
            LOG.error("Error writing data to file " + outputFilePath, e);
        }


        long encryptionEndTime = System.nanoTime();
        long encryptionTimeMillis = (encryptionEndTime - encryptionStartTime) / 1_000_000;
        totalEncryptionTimeMillis.addAndGet(encryptionTimeMillis);

        // Calculate encryption latency (time taken for encryption operation)
        long encryptionLatencyMillis = encryptionTimeMillis; // Encryption time itself
        totalEncryptionLatencyMillis.addAndGet(encryptionLatencyMillis);
        LOG.info("Encryption time for record {}: {} ms", count, encryptionTimeMillis);
        LOG.info("Encryption latency for record {}: {} ms", count, encryptionLatencyMillis);

        logMemoryUsage("After encryption");

        // Step 2: Total processing time (including encryption)
        long endProcessingTime = System.nanoTime();
        long processingTimeMillis = (endProcessingTime - startProcessingTime) / 1_000_000;
        totalProcessingTimeMillis.addAndGet(processingTimeMillis);

        // Calculate processing latency (excluding encryption)
        long processingLatencyMillis = processingTimeMillis - encryptionTimeMillis;
        totalProcessingLatencyMillis.addAndGet(processingLatencyMillis);
        LOG.info("Total processing time for record {}: {} ms", count, processingTimeMillis);
        LOG.info("Processing latency for record {} (excluding encryption): {} ms", count, processingLatencyMillis);

        count++;
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
