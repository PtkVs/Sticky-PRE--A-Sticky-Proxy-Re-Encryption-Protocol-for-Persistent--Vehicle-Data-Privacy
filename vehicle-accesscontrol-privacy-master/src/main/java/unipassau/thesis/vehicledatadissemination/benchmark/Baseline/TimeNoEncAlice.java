//***** NOTE ********************************************
//Baseline without encryption but with saving the data(i.e without data handler)

package unipassau.thesis.vehicledatadissemination.benchmark.Baseline;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class TimeNoEncAlice {

    private static final Logger LOG = LoggerFactory.getLogger(TimeNoEncAlice.class);

    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String csvFilePath = System.getProperty("user.dir") + "/csv/DS1-1000.csv";

    public static int count = 0;

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

        int numberOfRuns = 1;  // Number of times to repeat the process
        long totalBaselineTime = 0;  // To store total baseline time for all runs

        // Ensure the data directory exists
        File dataDirectory = new File(dataFolder);
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }

        // === Baseline Benchmark (Load and Save without Encryption) ===
        LOG.info("Starting baseline benchmark (load and save without encryption).");

        for (int run = 0; run < numberOfRuns; run++) {
            LOG.info("Baseline run " + (run + 1) + " of " + numberOfRuns);

            long baselineStartTime = System.nanoTime(); // Start time for this run

            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                List<String[]> records = reader.readAll();
                LOG.info("Total records found: " + records.size());

                int processedCount = 0;

                for (String[] record : records) {
                    // Create JSON object from the record
                    JSONObject res = new JSONObject();
                    res.put("header", record[0]);
                    res.put("timestamp", record[1]);
                    res.put("antennaAltitudeUnit", record[2]);
                    res.put("antennaAltitude", record[3]);
                    res.put("usedSatellites", record[4]);
                    res.put("quality", record[5]);
                    res.put("longitude", record[6]);
                    res.put("latitude", record[7]);

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
                }

                LOG.info("Total records processed in baseline: " + processedCount);

            } catch (IOException | CsvException e) {
                LOG.error("Error reading CSV file during baseline benchmark", e);
            }

            long baselineEndTime = System.nanoTime(); // End time for this run
            long baselineRunTime = baselineEndTime - baselineStartTime; // Total time for this run

            totalBaselineTime += baselineRunTime;  // Add to total baseline time

            LOG.info("Baseline execution time for run " + (run + 1) + ": "
                    + (baselineRunTime / 1_000_000) + " milliseconds");
        }

        // Log the total and average baseline times
        LOG.info("Total baseline execution time for " + numberOfRuns + " runs: "
                + (totalBaselineTime / 1_000_000) + " milliseconds");
        LOG.info("Average baseline execution time per run: "
                + ((totalBaselineTime / numberOfRuns) / 1_000_000) + " milliseconds");
    }
}






/*
//******** NOTE *************************************************************
//Baseline without encryption but without saving the data(i.e with data handler)
package unipassau.thesis.vehicledatadissemination.benchmark.Baseline;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class TimeNoEncAlice {

    private static final Logger LOG = LoggerFactory.getLogger(TimeNoEncAlice.class);

    public static String csvFilePath = System.getProperty("user.dir") + "/csv/DS1-1000.csv";
    public static int count = 0;

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

        int numberOfRuns = 1000;  // Number of times to repeat the process
        long totalBaselineTime = 0;  // To store total baseline time for all runs

        // === Baseline Benchmark (Load and Process without Saving) ===
        LOG.info("Starting baseline benchmark (load and process without saving).");

        for (int run = 0; run < numberOfRuns; run++) {
            LOG.info("Baseline run " + (run + 1) + " of " + numberOfRuns);

            long baselineStartTime = System.nanoTime(); // Start time for this run

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


                    // Log the processed data (for debugging purposes)
                    LOG.info("Processed record: " + res.toString());

                    count++;
                    processedCount++;

                    if (processedCount >= recordsToProcess) {
                        break;
                    }
                }

                LOG.info("Total records processed in baseline: " + processedCount);

            } catch (IOException | CsvException e) {
                LOG.error("Error reading CSV file during baseline benchmark", e);
            }

            long baselineEndTime = System.nanoTime(); // End time for this run
            long baselineRunTime = baselineEndTime - baselineStartTime; // Total time for this run

            totalBaselineTime += baselineRunTime;  // Add to total baseline time

            LOG.info("Baseline execution time for run " + (run + 1) + ": "
                    + (baselineRunTime / 1_000_000) + " milliseconds");
        }

        // Log the total and average baseline times
        LOG.info("Total baseline execution time for " + numberOfRuns + " runs: "
                + (totalBaselineTime / 1_000_000) + " milliseconds");
        LOG.info("Average baseline execution time per run: "
                + ((totalBaselineTime / numberOfRuns) / 1_000_000) + " milliseconds");
    }
}

*/
