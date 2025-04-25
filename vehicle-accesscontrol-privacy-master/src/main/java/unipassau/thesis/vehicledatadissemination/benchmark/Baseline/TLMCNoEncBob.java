//********** For just CPU load BM*********
package unipassau.thesis.vehicledatadissemination.benchmark.Baseline;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Scanner;

public class TLMCNoEncBob {

    private static final Logger LOG = LoggerFactory.getLogger(TLMCNoEncBob.class);
    public static String dataFolder = System.getProperty("user.dir") + "/data/";

    // CPU monitoring bean
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Please Enter the Start of the Record Count Range: ");
            int startCount = scanner.nextInt();

            System.out.print("Please Enter the End of the Record Count Range: ");
            int endCount = scanner.nextInt();

            int numberOfRuns = 1;

            // CPU load variables
            double maxCpuLoad = Double.MIN_VALUE;
            double minCpuLoad = Double.MAX_VALUE;
            double totalCpuLoad = 0;
            int cpuLoadSamples = 0;

            int availableProcessors = osBean.getAvailableProcessors();
            LOG.info("Available processors: " + availableProcessors);

            // Variables to track the time-based sampling
            long lastTimestamp = System.nanoTime();
            long interval = 100000000L; // Track CPU load every 1 second
            long elapsedTime;

            for (int run = 0; run < numberOfRuns; run++) {
                LOG.info("Starting run " + (run + 1) + " of " + numberOfRuns);

                for (int count = startCount; count <= endCount; count++) {
                    String filePath = dataFolder + "baseline_" + count + ".json";
                    File file = new File(filePath);

                    if (!file.exists()) {
                        LOG.warn("File not found: " + filePath);
                        continue;
                    }

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        StringBuilder fileContent = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }

                        JSONObject jsonObject = new JSONObject(fileContent.toString());
                        LOG.info("Read data for record " + count + ": " + jsonObject.toString(4));
                        System.out.println("Record " + count + ":");
                        System.out.println(jsonObject.toString(4));

                    } catch (IOException e) {
                        LOG.error("Error reading file " + filePath, e);
                    }

                    // Track CPU usage at regular intervals (1 second)
                    elapsedTime = System.nanoTime() - lastTimestamp;
                    if (elapsedTime > interval) {
                        double currentCpuLoad = osBean.getSystemCpuLoad() * 100; // Convert to percentage

                        // Update max and min CPU load
                        maxCpuLoad = Math.max(maxCpuLoad, currentCpuLoad);
                        minCpuLoad = Math.min(minCpuLoad, currentCpuLoad);

                        // Accumulate total CPU load for average calculation
                        totalCpuLoad += currentCpuLoad;
                        cpuLoadSamples++;

                        // Reset timestamp for the next interval
                        lastTimestamp = System.nanoTime();
                    }
                }
            }

            // After processing all records, calculate max, min, and average CPU load
            double averageCpuLoad = cpuLoadSamples > 0 ? totalCpuLoad / cpuLoadSamples : 0;

            LOG.info("Max CPU Load: " + String.format("%.2f", maxCpuLoad) + "%");
            LOG.info("Min CPU Load: " + (minCpuLoad == Double.MAX_VALUE ? "Not measured during this run" : String.format("%.2f", minCpuLoad)) + "%");
            LOG.info("Average CPU Load: " + String.format("%.2f", averageCpuLoad) + "%");
        }
    }
}


//**********FOR TIME, Latency And Memory***********
/*package unipassau.thesis.vehicledatadissemination.benchmark.Baseline;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Scanner;

public class TLCNoEncBob {

    private static final Logger LOG = LoggerFactory.getLogger(TLCNoEncBob.class);
    public static String dataFolder = System.getProperty("user.dir") + "/data/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        while (true) {
            System.out.print("Please Enter the Start of the Record Count Range: ");
            int startCount = scanner.nextInt();

            System.out.print("Please Enter the End of the Record Count Range: ");
            int endCount = scanner.nextInt();

            int numberOfRuns = 1000;
            long totalExecutionTime = 0;
            long totalLatency = 0;

            for (int run = 0; run < numberOfRuns; run++) {
                LOG.info("Starting run " + (run + 1) + " of " + numberOfRuns);
                long runStartTime = System.nanoTime();

                for (int count = startCount; count <= endCount; count++) {
                    String filePath = dataFolder + "baseline_" + count + ".json";
                    File file = new File(filePath);

                    if (!file.exists()) {
                        LOG.warn("File not found: " + filePath);
                        continue;
                    }

                    long fileStartTime = System.nanoTime();

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        StringBuilder fileContent = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }

                        JSONObject jsonObject = new JSONObject(fileContent.toString());
                        LOG.info("Read data for record " + count + ": " + jsonObject.toString(4));
                        System.out.println("Record " + count + ":");
                        System.out.println(jsonObject.toString(4));

                    } catch (IOException e) {
                        LOG.error("Error reading file " + filePath, e);
                    }

                    long fileEndTime = System.nanoTime();
                    long fileLatency = (fileEndTime - fileStartTime) / 1_000_000;  // Latency per file in milliseconds
                    totalLatency += fileLatency;
                    LOG.info("Latency for record " + count + ": " + fileLatency + " milliseconds");
                }

                long runEndTime = System.nanoTime();
                long runExecutionTime = runEndTime - runStartTime;
                long runExecutionTimeMillis = runExecutionTime / 1_000_000;

                totalExecutionTime += runExecutionTime;

                // Memory usage logging
                MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
                MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

                long heapUsed = heapMemoryUsage.getUsed();
                long nonHeapUsed = nonHeapMemoryUsage.getUsed();

                LOG.info("Heap memory used after run: " + (heapUsed / 1024 / 1024) + " MB");
                LOG.info("Non-heap memory used after run: " + (nonHeapUsed / 1024 / 1024) + " MB");
                LOG.info("Total memory used after run: " + ((heapUsed + nonHeapUsed) / 1024 / 1024) + " MB");

                LOG.info("Execution time for run " + (run + 1) + ": " + runExecutionTimeMillis + " milliseconds");
            }

            // Convert total execution time to milliseconds
            long totalExecutionTimeMillis = totalExecutionTime / 1_000_000;
            long averageExecutionTimeMillis = totalExecutionTimeMillis / numberOfRuns;

            LOG.info("Total latency for all records: " + totalLatency + " milliseconds");
            LOG.info("Total execution time for " + numberOfRuns + " runs: " + totalExecutionTimeMillis + " milliseconds");
            LOG.info("Average execution time per run: " + averageExecutionTimeMillis + " milliseconds");

        }
    }
}

*/
