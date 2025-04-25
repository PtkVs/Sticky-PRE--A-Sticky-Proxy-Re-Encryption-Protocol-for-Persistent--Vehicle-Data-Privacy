package unipassau.thesis.vehicledatadissemination.benchmark;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class abacTLMBmAliceDS {

    private static final Logger LOG = LoggerFactory.getLogger(abacTLMBmAliceDS.class);

    // File and directory paths
    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";
    public static String requestDir = System.getProperty("user.dir") + "/requests/";
    public static String csvFilePath = System.getProperty("user.dir") + "/csv/DS-1.csv";
    public static int count = 0;

    // Benchmark metrics
    private static final AtomicLong totalEncryptionTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalMemoryConsumed = new AtomicLong(0);
    private static final AtomicLong totalProcessingLatencyMillis = new AtomicLong(0);
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

        // Load requested attributes from the ABAC policy file
        List<String> requestedAttributes = loadRequestedAttributes(requestDir + "77.xml");
        if (requestedAttributes.isEmpty()) {
            LOG.error("No attributes were loaded from the policy file. Exiting.");
            System.exit(1);
        }

        // Number of runs for the benchmark
        int numberOfRuns = 10
                ;
        long totalRunTime = 0;

        for (int run = 1; run <= numberOfRuns; run++) {
         //   LOG.info("Starting run {} of {}", run, numberOfRuns);

            long runStartTime = System.nanoTime();

            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                List<String[]> records = reader.readAll();
           //     LOG.info("Total records found: {}", records.size());

                int processedCount = 0;

                for (String[] record : records) {
                    if (processedCount >= recordsToProcess) break;

                    processRecord(record, requestedAttributes);
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

         //   LOG.info("Time Taken for Run {}: {} ms", run, runTimeMillis);
        }

        // Calculate and log cumulative metrics
        long averageProcessingTimePerRecord = totalProcessingTimeMillis.get() / requestCount.get();
        long averageEncryptionTimePerRecord = totalEncryptionTimeMillis.get() / requestCount.get();
        long averageRunTime = totalRunTime / numberOfRuns;

        // Calculate total latencies
        long totalProcessingLatency = totalProcessingLatencyMillis.get();
        long totalExecutionLatency = totalRunTime - totalProcessingTimeMillis.get();
        long totalEncryptionLatency = totalExecutionLatency - totalProcessingLatency;

        LOG.info("Cumulative Metrics Across" + " " +numberOfRuns+ " " +  "Runs ({} records total):", requestCount.get());
        LOG.info("  - Total Processing Time: {} ms, Average Processing Time per Record: {} ms", totalProcessingTimeMillis.get(), averageProcessingTimePerRecord);
        LOG.info("  - Total Encryption Time: {} ms, Average Encryption Time per Record: {} ms", totalEncryptionTimeMillis.get(), averageEncryptionTimePerRecord);
      //  LOG.info("  - Total Execution Time for All Runs: {} ms, Average Execution Time per Run: {} ms", totalRunTime, averageRunTime);
        LOG.info("  - Total Processing Latency (Excluding Encryption): {} ms", totalProcessingLatency);
        LOG.info("  - Total Encryption Latency: {} ms", totalEncryptionLatency);
      //  LOG.info("  - Total Execution Latency (Including Overhead): {} ms", totalExecutionLatency);

        LOG.info("  - Total Memory Consumed: {} MB (Heap: {} MB, Non-Heap: {} MB), Average Memory Consumed: {} MB",
                totalMemoryConsumed.get() / (1024 * 1024),
                totalHeapMemoryUsed.get() / (1024 * 1024),
                totalNonHeapMemoryUsed.get() / (1024 * 1024),
        (totalMemoryConsumed.get() / requestCount.get()) / 1024 / 1024);

    }

    private static void processRecord(String[] record, List<String> requestedAttributes) {
        JSONObject filteredData = new JSONObject();

        // Apply ABAC filtering based on requested attributes
        for (String attribute : requestedAttributes) {
            int columnIndex = getColumnIndex(attribute);
            if (columnIndex != -1) {
                filteredData.put(attribute, record[columnIndex]);
            }
        }

        if (filteredData.length() == 0) {
            LOG.info("No data to process for this record based on the ABAC policy.");
            return;
        }

        long startProcessingTime = System.nanoTime();

    //    logMemoryUsage("Before encryption");

        // Encrypt the data
   //     LOG.info("Encrypting Data...");
        long encryptionStartTime = System.nanoTime();
        OpenPRE.INSTANCE.encrypt(pubKey, filteredData.toString(), dataFolder + count);

        // Attach hash of the policy
   //     LOG.info("Sticking hash of the policy to the data...");
        DataHandler.writer(policyFolder + "77.xml", dataFolder + count, count);

        long encryptionEndTime = System.nanoTime();
        long encryptionTimeMillis = (encryptionEndTime - encryptionStartTime) / 1_000_000;
        totalEncryptionTimeMillis.addAndGet(encryptionTimeMillis);

        logMemoryUsage();

        // Update processing time
        long endProcessingTime = System.nanoTime();
        long processingTimeMillis = (endProcessingTime - startProcessingTime) / 1_000_000;
        totalProcessingTimeMillis.addAndGet(processingTimeMillis);

        // Calculate processing latency (excluding encryption)
        long processingLatencyMillis = processingTimeMillis - encryptionTimeMillis;
        totalProcessingLatencyMillis.addAndGet(processingLatencyMillis);
     //   LOG.info("Total processing time for record {}: {} ms", count, processingTimeMillis);
     //   LOG.info("Processing latency for record {} (excluding encryption): {} ms", count, processingLatencyMillis);

        count++;
    }

    private static void logMemoryUsage() {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        long heapMemoryUsedMB = heapMemoryUsage.getUsed() / (1024 * 1024);
        long nonHeapMemoryUsedMB = nonHeapMemoryUsage.getUsed() / (1024 * 1024);

        long memoryUsed = heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed();
        totalMemoryConsumed.addAndGet(memoryUsed);
        totalHeapMemoryUsed.addAndGet(heapMemoryUsage.getUsed());
        totalNonHeapMemoryUsed.addAndGet(nonHeapMemoryUsage.getUsed());

    //    LOG.info("{} - Memory usage (Heap: {} MB, Non-Heap: {} MB)", heapMemoryUsedMB, nonHeapMemoryUsedMB);
    }

    private static List<String> loadRequestedAttributes(String requestFilePath) {
        List<String> attributes = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(requestFilePath)));
            JSONObject requestJson = new JSONObject(content);

            if (requestJson.has("Request")) {
                JSONObject requestObject = requestJson.getJSONObject("Request");

                if (requestObject.has("Category")) {
                    JSONArray categories = requestObject.getJSONArray("Category");

                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject category = categories.getJSONObject(i);

                        if (category.has("CategoryId") &&
                                "urn:oasis:names:tc:xacml:3.0:attribute-category:resource".equals(category.getString("CategoryId"))) {

                            if (category.has("Attribute")) {
                                JSONArray attributesArray = category.getJSONArray("Attribute");

                                for (int j = 0; j < attributesArray.length(); j++) {
                                    JSONObject attributeObject = attributesArray.getJSONObject(j);

                                    if (attributeObject.has("Value")) {
                                        attributes.add(attributeObject.getString("Value"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error loading requested attributes from file {}", requestFilePath, e);
        }
        return attributes;
    }

    private static int getColumnIndex(String attribute) {
        switch (attribute) {
            case "header":
                return 0;
            case "timestamp":
                return 1;
            case "antennaAltitudeUnit":
                return 2;
            case "antennaAltitude":
                return 3;
            case "usedSatellites":
                return 4;
            case "quality":
                return 5;
            case "longitude":
                return 6;
            case "latitude":
                return 7;
            default:
                return -1;
        }
    }
}
