package unipassau.thesis.vehicledatadissemination.benchmark.Baseline;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.sun.management.OperatingSystemMXBean;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

public class CPUNoEncAlice {

    private static final Logger LOG = LoggerFactory.getLogger(CPUNoEncAlice.class);

    public static String csvFilePath = System.getProperty("user.dir") + "/csv/DS1-1000.csv";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static int count = 0;

    // Memory management bean to track memory usage
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

        int numberOfRuns = 10;  // Number of times to repeat the process

        LOG.info("Starting CPU usage benchmark for Alice with " + numberOfRuns + " runs.");

        double maxCpuLoad = Double.MIN_VALUE;
        double minCpuLoad = Double.MAX_VALUE;  // This will be updated properly
        double totalCpuLoad = 0;
        int cpuLoadSamples = 0;

        // Number of available processors (cores)
        int availableProcessors = osBean.getAvailableProcessors();
        LOG.info("Available processors: " + availableProcessors);

        // Track CPU usage at intervals
        long lastTimestamp = System.nanoTime();
        long interval = 100000000L; // 1 millisecond interval
        long elapsedTime;

        for (int run = 0; run < numberOfRuns; run++) {
            LOG.info("Run " + (run + 1) + " of " + numberOfRuns);



            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                List<String[]> records = reader.readAll();
                LOG.info("Total records found: " + records.size());

                int processedCount = 0;

                for (String[] record : records) {

                    // Create JSON object from the record (simulating data processing)
                    JSONObject res = new JSONObject();
                    res.put("header", record[0]);
                    res.put("timestamp", record[1]);
                    res.put("antennaAltitudeUnit", record[2]);
                    res.put("antennaAltitude", record[3]);
                    res.put("usedSatellites", record[4]);
                    res.put("quality", record[5]);
                    res.put("longitude", record[6]);
                    res.put("latitude", record[7]);
                    res.put("temperature", record[8]);



                    LOG.info("Saving  Data..........................................");


                    // Save JSON object to a file in the data folder
                    String outputFilePath = dataFolder + "baseline_" + count + ".json";
                    try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
                        fileWriter.write(res.toString());
                    } catch (IOException e) {
                        LOG.error("Error writing data to file " + outputFilePath, e);
                    }

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

                        // Update min CPU load (ensure it's captured properly)
                        if (cpuLoadSamples == 0 || currentCpuLoad < minCpuLoad) {
                            minCpuLoad = currentCpuLoad; // Capture the first value or any value smaller than the current minimum
                        }

                        // Update total CPU load and sample count
                        totalCpuLoad += currentCpuLoad;
                        cpuLoadSamples++;

                        // Reset timestamp for the next interval
                        lastTimestamp = System.nanoTime();
                    }
                }

                LOG.info("Total records processed in this run: " + processedCount);

            } catch (IOException | CsvException e) {
                LOG.error("Error reading CSV file during benchmark", e);
            }

        }

        // Calculate and log average CPU load
        double averageCpuLoad = cpuLoadSamples > 0 ? totalCpuLoad / cpuLoadSamples : 0;
        LOG.info("Max CPU Load: " + String.format("%.2f", maxCpuLoad) + "%");

        // Log minimum CPU load with a condition, logging the real value, even if it's negative or very small
        if (minCpuLoad == Double.MAX_VALUE) {
            LOG.info("Min CPU Load: Not measured during this run");
        } else {
            // Directly log the actual minCpuLoad value, even if it's a small positive number or negative
            LOG.info("Min CPU Load: " + String.format("%.4f", minCpuLoad) + "%");
        }

        LOG.info("Average CPU Load: " + String.format("%.2f", averageCpuLoad) + "%");
        LOG.info("Total Number of Runs: " + numberOfRuns);
        LOG.info("Total Records Processed: " + (count));
    }
}
