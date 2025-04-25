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

public class LatencyBmAliceDS {

    private static final Logger LOG = LoggerFactory.getLogger(LatencyBmAliceDS.class);

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

        int numberOfRuns = 10;  // Number of times to repeat the process
        long totalEncryptionLatency = 0;  // To store total encryption latency across all runs
        long totalPolicyHashLatency = 0;  // To store total policy hash latency across all runs

        LOG.info("Starting latency benchmark for Alice (encryption and policy attachment).");

        for (int run = 0; run < numberOfRuns; run++) {
            LOG.info("Run " + (run + 1) + " of " + numberOfRuns);

            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                List<String[]> records = reader.readAll();
                LOG.info("Total records found: " + records.size());

                int processedCount = 0;

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

                    // Measure encryption latency
                    LOG.info("Encrypting Data ...");
                    long encryptionStartTime = System.nanoTime();  // Start time before encryption
                    OpenPRE.INSTANCE.encrypt(pubKey, res.toString(), dataFolder + count);
                    long encryptionEndTime = System.nanoTime();  // End time after encryption
                    long encryptionLatency = (encryptionEndTime - encryptionStartTime) / 1_000_000;  // Convert to milliseconds
                    totalEncryptionLatency += encryptionLatency;
                    LOG.info("Encryption latency for record " + count + ": " + encryptionLatency + " milliseconds");

                    // Measure policy hash latency
                    LOG.info("Sticking hash of the policy to the data ...");
                    long policyHashStartTime = System.nanoTime();  // Start time before policy attachment
                    DataHandler.writer(policyFolder + "77.xml", dataFolder + count, count);
                    long policyHashEndTime = System.nanoTime();  // End time after policy attachment
                    long policyHashLatency = (policyHashEndTime - policyHashStartTime) / 1_000_000;  // Convert to milliseconds
                    totalPolicyHashLatency += policyHashLatency;
                    LOG.info("Policy hash latency for record " + count + ": " + policyHashLatency + " milliseconds");

                    count++;
                    processedCount++;

                    if (processedCount >= recordsToProcess) {
                        break;
                    }
                }

                LOG.info("Total records processed in this run: " + processedCount);

            } catch (IOException | CsvException e) {
                LOG.error("Error reading CSV file during latency benchmark", e);
            }
        }

        // Calculate the average latency per run
        long averageEncryptionLatency = totalEncryptionLatency / (numberOfRuns * recordsToProcess);
        long averagePolicyHashLatency = totalPolicyHashLatency / (numberOfRuns * recordsToProcess);

        // Log the total and average latencies
        LOG.info("Total encryption latency for all runs: " + totalEncryptionLatency + " milliseconds");
        LOG.info("Average encryption latency per record: " + averageEncryptionLatency + " milliseconds");
        LOG.info("Total policy hash latency for all runs: " + totalPolicyHashLatency + " milliseconds");
        LOG.info("Average policy hash latency per record: " + averagePolicyHashLatency + " milliseconds");
        LOG.info("Total number of runs: " + numberOfRuns);
    }
}
