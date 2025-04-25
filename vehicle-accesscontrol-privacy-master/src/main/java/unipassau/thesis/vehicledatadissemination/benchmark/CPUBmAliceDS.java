
//*******BM of CPU usage, encryption and with ABAC******
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
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class CPUBmAliceDS{

    private static final Logger LOG = LoggerFactory.getLogger(CPUBmAliceDS.class);

    // File and directory paths
    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";
    public static String requestDir = System.getProperty("user.dir") + "/requests/";
    public static String csvFilePath = System.getProperty("user.dir") + "/csv/DS-1.csv";
    public static int count = 0;

    // Benchmark metrics

    private static final AtomicLong requestCount = new AtomicLong(0);


    // OperatingSystemMXBean for CPU usage tracking
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

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
        int numberOfRuns = 10;

        // CPU benchmarking metrics
        double maxCpuLoad = Double.MIN_VALUE;
        double minCpuLoad = Double.MAX_VALUE;
        double totalCpuLoad = 0;
        int cpuLoadSamples = 0;

        // Number of available processors (cores)
        int availableProcessors = osBean.getAvailableProcessors();
      //  LOG.info("Available processors: " + availableProcessors);

        // Track CPU usage at intervals
        long lastTimestamp = System.nanoTime();
        long interval = 100000000L; // 1 second interval (1 second = 1 billion nanoseconds)
        long elapsedTime;

        for (int run = 1; run <= numberOfRuns; run++) {
        //    LOG.info("Starting run {} of {}", run, numberOfRuns);



            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                List<String[]> records = reader.readAll();
          //      LOG.info("Total records found: {}", records.size());

                int processedCount = 0;

                for (String[] record : records) {
                    if (processedCount >= recordsToProcess) break;

                    processRecord(record, requestedAttributes);
                    processedCount++;

                    // Track CPU usage at intervals
                    elapsedTime = System.nanoTime() - lastTimestamp;
                    if (elapsedTime > interval) {
                        double currentCpuLoad = osBean.getSystemCpuLoad() * 100; // Convert to percentage
                        currentCpuLoad /= availableProcessors; // Adjust if needed for multi-core systems

                        // Update max CPU load
                        maxCpuLoad = Math.max(maxCpuLoad, currentCpuLoad);

                        // Update min CPU load only if the current value is lower than the existing minimum
                        if (currentCpuLoad > 0) { // Ignore zero CPU load values
                            minCpuLoad = Math.min(minCpuLoad, currentCpuLoad);
                        }

                        // Update total CPU load and sample count
                        totalCpuLoad += currentCpuLoad;
                        cpuLoadSamples++;

                        // Reset timestamp for the next interval
                        lastTimestamp = System.nanoTime();
                    }
                }

                // Add processed count for cumulative metrics
                requestCount.addAndGet(processedCount);

            } catch (IOException | CsvException e) {
                LOG.error("Error reading CSV file", e);
            }




        }

        // Calculate and log CPU metrics
        double averageCpuLoad = cpuLoadSamples > 0 ? totalCpuLoad / cpuLoadSamples : 0;
        LOG.info("Max CPU Load: " + String.format("%.2f", maxCpuLoad) + "%");

        // Log minimum CPU load with a condition
        if (minCpuLoad == Double.MAX_VALUE) {
            LOG.info("Min CPU Load: Not measured during this run");
        } else {
            LOG.info("Min CPU Load: " + String.format("%.2f", minCpuLoad) + "%");
        }

        LOG.info("Average CPU Load: " + String.format("%.2f", averageCpuLoad) + "%");


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

        // Encrypt the data
     //   LOG.info("Encrypting Data...");
        OpenPRE.INSTANCE.encrypt(pubKey, filteredData.toString(), dataFolder + count);

        // Attach hash of the policy
    //    LOG.info("Sticking hash of the policy to the data...");
        DataHandler.writer(policyFolder + "77.xml", dataFolder + count, count);
        count++;
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


/*
//********This is for BM of CPU usage, encryption but without ABAC*******

package unipassau.thesis.vehicledatadissemination.benchmark;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.List;

public class CPUBmAliceDS {

    private static Logger LOG = LoggerFactory.getLogger(CPUBmAliceDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";

    public static String csvFilePath = System.getProperty("user.dir") + "/csv/DS-1.csv";

    public static int count = 0;

    // OperatingSystemMXBean for CPU usage tracking
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

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

        int numberOfRuns = 10000; // Number of times to repeat the process

     //   LOG.info("Starting CPU usage benchmark for Alice with " + numberOfRuns + " runs.");

        double maxCpuLoad = Double.MIN_VALUE;
        double minCpuLoad = Double.MAX_VALUE;
        double totalCpuLoad = 0;
        int cpuLoadSamples = 0;

        // Number of available processors (cores)
        int availableProcessors = osBean.getAvailableProcessors();
   //     LOG.info("Available processors: " + availableProcessors);

        // Track CPU usage at intervals
        long lastTimestamp = System.nanoTime();
        long interval = 100000000L; // 1 second interval (1 second = 1 billion nanoseconds)
        long elapsedTime;

        for (int run = 0; run < numberOfRuns; run++) {
        //    LOG.info("Run " + (run + 1) + " of " + numberOfRuns);

            long runStartTime = System.nanoTime(); // Start time for this run

            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                List<String[]> records = reader.readAll();
     //           LOG.info("Total records found: " + records.size());

                int processedCount = 0;

                for (String[] record : records) {
                    JSONObject res = new JSONObject();
                    res.put("header", record[0]);
                    res.put("timestamp", record[1]);
                    res.put("antennaAltitudeUnit", record[2]);
                    res.put("antennaAltitude", record[3]);
                    res.put("usedSatellites", record[4]);
                    res.put("quality", record[5]);
                    res.put("longitude", record[6]);
                    res.put("latitude", record[7]);

    //                LOG.info("Encrypting Data ...");
                    OpenPRE.INSTANCE.encrypt(pubKey, res.toString(), dataFolder + count);

      //              LOG.info("Sticking hash of the policy to the data ...");
                    DataHandler.writer(policyFolder + "77.xml", dataFolder + count, count);

                    count++;
                    processedCount++;

                    if (processedCount >= recordsToProcess) {
                        break;
                    }

                    // Track CPU usage at intervals
                    elapsedTime = System.nanoTime() - lastTimestamp;
                    if (elapsedTime > interval) {
                        double currentCpuLoad = osBean.getSystemCpuLoad() * 100; // Convert to percentage
                        currentCpuLoad /= availableProcessors; // Adjust if needed for multi-core systems

                        // Update max CPU load
                        maxCpuLoad = Math.max(maxCpuLoad, currentCpuLoad);

                        // Update min CPU load only if the current value is lower than the existing minimum
                        if (currentCpuLoad > 0) { // Ignore zero CPU load values
                            minCpuLoad = Math.min(minCpuLoad, currentCpuLoad);
                        }

                        // Update total CPU load and sample count
                        totalCpuLoad += currentCpuLoad;
                        cpuLoadSamples++;

                        // Reset timestamp for the next interval
                        lastTimestamp = System.nanoTime();
                    }
                }

   //             LOG.info("Total records processed in this run: " + processedCount);

            } catch (IOException | CsvException e) {
                LOG.error("Error reading CSV file during benchmark", e);
            }

        }

        // Calculate and log average CPU load
        double averageCpuLoad = cpuLoadSamples > 0 ? totalCpuLoad / cpuLoadSamples : 0;
        LOG.info("Max CPU Load: " + String.format("%.2f", maxCpuLoad) + "%");

        // Log minimum CPU load with a condition
        if (minCpuLoad == Double.MAX_VALUE) {
            LOG.info("Min CPU Load: Not measured during this run");
        } else {
            LOG.info("Min CPU Load: " + String.format("%.2f", minCpuLoad) + "%");
        }

        LOG.info("Average CPU Load: " + String.format("%.2f", averageCpuLoad) + "%");
        LOG.info("Total Number of Runs: " + numberOfRuns);
        LOG.info("Total Records Processed: " + (count));
    }
}
*/