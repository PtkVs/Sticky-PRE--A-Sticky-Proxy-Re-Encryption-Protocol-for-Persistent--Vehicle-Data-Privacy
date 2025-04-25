//With TLC Logged
package unipassau.thesis.preserver.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipassau.thesis.preserver.util.OpenPRE;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PREService {

    @Autowired
    org.springframework.core.env.Environment env;

    @Autowired
    private HashMap<String, String> reEncryptionKeysMap;

    private static final Logger LOG = LoggerFactory.getLogger(PREService.class);

    // Static variables for tracking cumulative metrics
    private static final AtomicLong totalReEncryptionTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalStartToReEncryptionLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalPostReEncryptionLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalRequestProcessingTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalMemoryConsumed = new AtomicLong(0);
    private static final AtomicLong totalHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong totalNonHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong requestCount = new AtomicLong(0);

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    public PREService() {
        // Add a shutdown hook to log cumulative metrics when the application stops
        Runtime.getRuntime().addShutdownHook(new Thread(this::logFinalCumulativeMetrics));
    }

    public byte[] reEncryptionService(byte[] encryptedData, String subjectID) throws IOException {
        // Track request start time
        long requestStartTime = System.nanoTime();

        String tempFileName = RandomStringUtils.randomAlphanumeric(12);

        // Write incoming data to a temporary file
        try (var out = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(env.getProperty("app.tmpFolder") + tempFileName, true)))) {
            out.write(encryptedData);
        } catch (IOException e) {
            LOG.error("Error writing encrypted data to temp file", e);
            throw e;
        }

        // Measure latency from start to re-encryption initiation
        long reEncryptionStartTime = System.nanoTime();
        long startToReEncryptionLatencyMillis = (reEncryptionStartTime - requestStartTime) / 1_000_000;
        totalStartToReEncryptionLatencyMillis.addAndGet(startToReEncryptionLatencyMillis);

        // Perform re-encryption
        long preProcessStartTime = System.nanoTime();
        OpenPRE.INSTANCE.reEncrypt(
                env.getProperty("app.tmpFolder") + tempFileName,
                env.getProperty("app.reEncKeysFolder") + reEncryptionKeysMap.get(subjectID),
                env.getProperty("app.tmpFolder") + tempFileName + "-re");
        long preProcessEndTime = System.nanoTime();

        long reEncryptionDurationMillis = (preProcessEndTime - preProcessStartTime) / 1_000_000;
        totalReEncryptionTimeMillis.addAndGet(reEncryptionDurationMillis);

        // Log memory usage after re-encryption
        trackMemoryUsage();

        // Post re-encryption operations
        long postReEncryptionStartTime = System.nanoTime();
        byte[] reEncryptedData;
        try (FileInputStream read = new FileInputStream(
                new File(env.getProperty("app.tmpFolder") + tempFileName + "-re"))) {
            reEncryptedData = read.readAllBytes();
        } catch (IOException e) {
            LOG.error("Error reading re-encrypted data from temp file", e);
            throw e;
        }
        long postReEncryptionEndTime = System.nanoTime();
        long postReEncryptionLatencyMillis = (postReEncryptionEndTime - postReEncryptionStartTime) / 1_000_000;
        totalPostReEncryptionLatencyMillis.addAndGet(postReEncryptionLatencyMillis);

        // Calculate total request processing time
        long totalProcessingTimeMillis = (postReEncryptionEndTime - requestStartTime) / 1_000_000;
        totalRequestProcessingTimeMillis.addAndGet(totalProcessingTimeMillis);

        // Increment request count
        requestCount.incrementAndGet();

        return reEncryptedData;
    }

    // Track memory usage cumulatively
    private void trackMemoryUsage() {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        totalHeapMemoryUsed.addAndGet(heapMemoryUsage.getUsed());
        totalNonHeapMemoryUsed.addAndGet(nonHeapMemoryUsage.getUsed());
        totalMemoryConsumed.addAndGet(heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed());
    }

    // Log cumulative metrics at the end when the application stops
    private void logFinalCumulativeMetrics() {
        long currentRequestCount = requestCount.get();

        // Prevent division by zero errors
        double avgReEncTime = currentRequestCount > 0 ? (double) totalReEncryptionTimeMillis.get() / currentRequestCount : 0;
     //   double avgStartToReEncLatency = currentRequestCount > 0 ? (double) totalStartToReEncryptionLatencyMillis.get() / currentRequestCount : 0;
      //  double avgPostReEncLatency = currentRequestCount > 0 ? (double) totalPostReEncryptionLatencyMillis.get() / currentRequestCount : 0;
        double avgProcessingTime = currentRequestCount > 0 ? (double) totalRequestProcessingTimeMillis.get() / currentRequestCount : 0;
        double avgMemoryUsedMB = currentRequestCount > 0 ? (double) totalMemoryConsumed.get() / (1024 * 1024 * currentRequestCount) : 0;
        long totalMemoryUsedMB = totalMemoryConsumed.get() / (1024 * 1024);
        long totalHeapMB = totalHeapMemoryUsed.get() / (1024 * 1024);
        long totalNonHeapMB = totalNonHeapMemoryUsed.get() / (1024 * 1024);

        LOG.info(" Final Cumulative Metrics ({}) Requests ", currentRequestCount);
        LOG.info("  - Total re-encryption time: {} ms, Average: {} ms", totalReEncryptionTimeMillis.get(), avgReEncTime);
      //  LOG.info("  - Total start-to-re-encryption latency: {} ms, Average: {} ms", totalStartToReEncryptionLatencyMillis.get(), avgStartToReEncLatency);
       // LOG.info("  - Total post-re-encryption latency: {} ms, Average: {} ms", totalPostReEncryptionLatencyMillis.get(), avgPostReEncLatency);
        LOG.info("  - Total processing time: {} ms, Average: {} ms", totalRequestProcessingTimeMillis.get(), avgProcessingTime);
        LOG.info("  - Total memory consumed: {} MB, Average: {} MB (Heap: {} MB, Non-Heap: {} MB)", totalMemoryUsedMB, avgMemoryUsedMB, totalHeapMB, totalNonHeapMB);

    }
}





// Original Codes Below

//With TLC Logged
/*
package unipassau.thesis.preserver.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipassau.thesis.preserver.util.OpenPRE;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PREService {

    @Autowired
    org.springframework.core.env.Environment env;

    @Autowired
    private HashMap<String, String> reEncryptionKeysMap;

    private static final Logger LOG = LoggerFactory.getLogger(PREService.class);

    // Static variables for tracking total time, latency, memory, and request count
    private static final AtomicLong totalReEncryptionTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalStartToReEncryptionLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalPostReEncryptionLatencyMillis = new AtomicLong(0);
    private static final AtomicLong totalRequestProcessingTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalMemoryConsumed = new AtomicLong(0);
    private static final AtomicLong requestCount = new AtomicLong(0);

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    public byte[] reEncryptionService(byte[] encryptedData, String subjectID) throws IOException {

        // Track request start time
        long requestStartTime = System.nanoTime();

        String tempFileName = RandomStringUtils.randomAlphanumeric(12);

        // Write incoming data to a temporary file
        try (var out = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(env.getProperty("app.tmpFolder") + tempFileName, true)))) {
            out.write(encryptedData);
        } catch (IOException e) {
            LOG.error("Error writing encrypted data to temp file", e);
            throw e;
        }

        // Measure latency from start to re-encryption initiation
        long reEncryptionStartTime = System.nanoTime();
        long startToReEncryptionLatencyMillis = (reEncryptionStartTime - requestStartTime) / 1_000_000;
        totalStartToReEncryptionLatencyMillis.addAndGet(startToReEncryptionLatencyMillis);
        LOG.info("Start-to-re-encryption latency: {} ms", startToReEncryptionLatencyMillis);

        // Log memory usage before re-encryption
        logMemoryUsage("Before re-encryption");

        // Perform re-encryption
        long preProcessStartTime = System.nanoTime(); // Start timing re-encryption process
        OpenPRE.INSTANCE.reEncrypt(
                env.getProperty("app.tmpFolder") + tempFileName,
                env.getProperty("app.reEncKeysFolder") + reEncryptionKeysMap.get(subjectID),
                env.getProperty("app.tmpFolder") + tempFileName + "-re");
        long preProcessEndTime = System.nanoTime(); // End timing

        long reEncryptionDurationMillis = (preProcessEndTime - preProcessStartTime) / 1_000_000;
        totalReEncryptionTimeMillis.addAndGet(reEncryptionDurationMillis);
        LOG.info("Re-encryption process for subjectID {} took {} ms", subjectID, reEncryptionDurationMillis);

        // Log memory usage after re-encryption
        logMemoryUsage("After re-encryption");

        // Post re-encryption operations (e.g., reading the re-encrypted file)
        long postReEncryptionStartTime = System.nanoTime();
        byte[] reEncryptedData;
        try (FileInputStream read = new FileInputStream(
                new File(env.getProperty("app.tmpFolder") + tempFileName + "-re"))) {
            reEncryptedData = read.readAllBytes();
        } catch (IOException e) {
            LOG.error("Error reading re-encrypted data from temp file", e);
            throw e;
        }
        long postReEncryptionEndTime = System.nanoTime();
        long postReEncryptionLatencyMillis = (postReEncryptionEndTime - postReEncryptionStartTime) / 1_000_000;
        totalPostReEncryptionLatencyMillis.addAndGet(postReEncryptionLatencyMillis);
        LOG.info("Post-re-encryption latency: {} ms", postReEncryptionLatencyMillis);

        // Calculate and log total processing time
        long totalProcessingTimeMillis = (postReEncryptionEndTime - requestStartTime) / 1_000_000;
        totalRequestProcessingTimeMillis.addAndGet(totalProcessingTimeMillis);
        long currentRequestCount = requestCount.incrementAndGet();
        LOG.info("Total processing time for subjectID {}: {} ms", subjectID, totalProcessingTimeMillis);

        // Log cumulative metrics
        logCumulativeMetrics(currentRequestCount);

        return reEncryptedData;
    }

    // Method to log cumulative metrics for benchmarking
    private void logCumulativeMetrics(long currentRequestCount) {
        long totalReEncTime = totalReEncryptionTimeMillis.get();
        long totalLatencyBeforeReEnc = totalStartToReEncryptionLatencyMillis.get();
        long totalLatencyAfterReEnc = totalPostReEncryptionLatencyMillis.get();
        long totalProcessingTime = totalRequestProcessingTimeMillis.get();
        long totalMemoryUsed = totalMemoryConsumed.get();

        LOG.info("Cumulative Metrics ({} requests):", currentRequestCount);
        LOG.info("  - Total re-encryption time: {} ms, Average: {} ms",
                totalReEncTime, (double) totalReEncTime / currentRequestCount);
        LOG.info("  - Total start-to-re-encryption latency: {} ms, Average: {} ms",
                totalLatencyBeforeReEnc, (double) totalLatencyBeforeReEnc / currentRequestCount);
        LOG.info("  - Total post-re-encryption latency: {} ms, Average: {} ms",
                totalLatencyAfterReEnc, (double) totalLatencyAfterReEnc / currentRequestCount);
        LOG.info("  - Total processing time: {} ms, Average: {} ms",
                totalProcessingTime, (double) totalProcessingTime / currentRequestCount);
        LOG.info("  - Total memory consumed: {} MB, Average: {} MB",
                totalMemoryUsed / 1024 / 1024, (double) totalMemoryUsed / 1024 / 1024 / currentRequestCount);
    }

    // Method to log memory usage and update total memory consumed
    private void logMemoryUsage(String phase) {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        long memoryUsed = heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed();
        totalMemoryConsumed.addAndGet(memoryUsed);

        LOG.info("{} - Heap memory used: {} MB", phase, heapMemoryUsage.getUsed() / 1024 / 1024);
        LOG.info("{} - Non-heap memory used: {} MB", phase, nonHeapMemoryUsage.getUsed() / 1024 / 1024);
        LOG.info("{} - Total memory used: {} MB", phase, memoryUsed / 1024 / 1024);
    }
}




/* //Without TLC logged
package unipassau.thesis.preserver.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipassau.thesis.preserver.util.OpenPRE;

import java.io.*;
import java.util.HashMap;

@Service
public class PREService {

    @Autowired
    org.springframework.core.env.Environment env;

    @Autowired
    private HashMap<String, String> reEncryptionKeysMap;

    public byte[] reEncryptionService(byte[] encryptedData, String subjectID) throws IOException {

        String tempFileName = RandomStringUtils.randomAlphanumeric(12);
        try (var out = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(env.getProperty("app.tmpFolder") + tempFileName, true)))){

            out.write(encryptedData);

        } catch (IOException e) {
            e.printStackTrace();
        }

        OpenPRE.INSTANCE.reEncrypt(env.getProperty("app.tmpFolder") + tempFileName,
                env.getProperty("app.reEncKeysFolder") + reEncryptionKeysMap.get(subjectID),
                env.getProperty("app.tmpFolder") + tempFileName + "-re");
        FileInputStream read = new FileInputStream(new File(env.getProperty("app.tmpFolder") + tempFileName + "-re"));

        return read.readAllBytes();
    }
}
*/

