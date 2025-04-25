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
import java.util.List;

public class TimeBmAliceDS {

    private static Logger LOG = LoggerFactory.getLogger(TimeBmAliceDS.class);

    public static String cryptoFolder = System.getProperty("user.dir") + "/crypto/";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String pubKey = cryptoFolder + "alice-public-key";
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";

    public static String csvFilePath = System.getProperty("user.dir") + "/csv/DS1-10.csv";

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
            return; // This return is just to satisfy the compiler that recordsToProcess is initialized
        }

        int numberOfRuns = 1;  // Number of times to repeat the process
        long totalExecutionTime = 0;  // To store total time for all runs

        // Running the process multiple times
        for (int run = 0; run < numberOfRuns; run++) {
            LOG.info("Starting run " + (run + 1) + " of " + numberOfRuns);

            long runStartTime = System.nanoTime();  // Start time for this run

            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                List<String[]> records = reader.readAll();
                LOG.info("Total records found: " + records.size());

                int processedCount = 0;

                long totalEncryptionTime = 0; // Variable to store total encryption time for this run

                for (String[] record : records) {
                    // Logging each record for debugging
                    LOG.info("Processing record: " + String.join(", ", record));

                    JSONObject res = new JSONObject();
                    res.put("header", record[0]);
                    res.put("timestamp", record[1]);
                    res.put("antennaAltitudeUnit", record[2]);
                    res.put("antennaAltitude", record[3]);
                    res.put("usedSatellites", record[4]);
                    res.put("quality", record[5]);
                    res.put("longitude", record[6]);
                    res.put("latitude", record[7]);

                    // Benchmarking encryption process
                    LOG.info("Encrypting Data ...");
                    long startTime = System.nanoTime(); // Start time before encryption

                    OpenPRE.INSTANCE.encrypt(pubKey, res.toString(), dataFolder + count);

                    long endTime = System.nanoTime(); // End time after encryption
                    long encryptionTime = endTime - startTime;
                    totalEncryptionTime += encryptionTime;

                    LOG.info("Encryption time for record " + count + ": " + (encryptionTime / 1_000_000) + " milliseconds");

                    LOG.info("Sticking hash of the policy to the data ...");
                    DataHandler.writer(policyFolder + "77.xml", dataFolder + count, count);

                    count++;
                    processedCount++;

                    if (processedCount >= recordsToProcess) {
                        break;
                    }
                }

                LOG.info("Total records processed: " + processedCount);
                LOG.info("Total encryption time for this run: " + (totalEncryptionTime / 1_000_000) + " milliseconds");
                LOG.info("Average encryption time per record for this run: " + ((totalEncryptionTime / processedCount) / 1_000_000) + " milliseconds");

            } catch (IOException | CsvException e) {
                LOG.error("Error reading CSV file", e);
            }

            long runEndTime = System.nanoTime();  // End time for this run
            long runExecutionTime = runEndTime - runStartTime;  // Total time for this run

            totalExecutionTime += runExecutionTime;  // Add to total execution time

            LOG.info("Execution time for run " + (run + 1) + ": " + (runExecutionTime / 1_000_000) + " milliseconds");
        }

        // After all runs, log the total and average execution times
        LOG.info("Total execution time for " + numberOfRuns + " runs: " + (totalExecutionTime / 1_000_000) + " milliseconds");
        LOG.info("Average execution time per run: " + ((totalExecutionTime / numberOfRuns) / 1_000_000) + " milliseconds");
    }
}
