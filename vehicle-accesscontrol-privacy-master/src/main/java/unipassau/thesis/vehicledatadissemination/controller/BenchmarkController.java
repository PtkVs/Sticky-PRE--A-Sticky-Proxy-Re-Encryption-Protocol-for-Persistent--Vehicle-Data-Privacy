//Controller with PDP and PRE response time logged
package unipassau.thesis.vehicledatadissemination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import unipassau.thesis.vehicledatadissemination.config.DatabaseConfiguration;
import unipassau.thesis.vehicledatadissemination.config.PolicyConfiguration;
import unipassau.thesis.vehicledatadissemination.model.MappingPolicyDB;
import unipassau.thesis.vehicledatadissemination.services.PDPService;
import unipassau.thesis.vehicledatadissemination.services.PolicyEnforcementService;
import unipassau.thesis.vehicledatadissemination.services.ProxyReEncryptionService;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class BenchmarkController {

    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static int count;

    private static final Logger LOG = LoggerFactory.getLogger(BenchmarkController.class);

    // Static variables to track time and memory metrics
    private static final AtomicLong totalPdpResponseTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalPdpLatencyMillis = new AtomicLong(0); // Total PDP latency
    private static final AtomicLong totalPreResponseTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalPreLatencyMillis = new AtomicLong(0); // Total PRE latency
    private static final AtomicLong totalRequestProcessingTimeMillis = new AtomicLong(0);
    private static final AtomicLong totalProcessingLatencyMillis = new AtomicLong(0); // Total processing latency
    private static final AtomicLong totalMemoryConsumed = new AtomicLong(0);
    private static final AtomicLong totalHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong totalNonHeapMemoryUsed = new AtomicLong(0);
    private static final AtomicLong requestCount = new AtomicLong(0);

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    @Autowired
    private PolicyEnforcementService policyEnforcementService;

    @Autowired
    private ProxyReEncryptionService proxyReEncryptionService;

    @Autowired
    private PDPService pdpService;

    @Autowired
    private PolicyConfiguration policyConfiguration;

    @Autowired
    private DatabaseConfiguration databaseConfiguration;

    @RequestMapping(method = RequestMethod.POST, value = "/benchmark")
    public ResponseEntity<byte[]> authorize(InputStream dataStream, @RequestParam("count") int count) throws Exception {
        this.count = count;

        // Record total request start time
        long requestStartTime = System.nanoTime();

        // Read binary file from request
        byte[] onlyHash = dataStream.readAllBytes();

        // Separate hash and data
        Map<String, byte[]> stickyDocumentMap = DataHandler.readOnlyHash(onlyHash);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Principal principal = request.getUserPrincipal();

        // Convert hash to string and get the policy filename
        String hashValue = Encoder.bytesToHex(stickyDocumentMap.get("hash"));
        String policyFilename = policyConfiguration.policyMap().get(hashValue);

        // Measure start-to-processing latency
        long processingStartTime = System.nanoTime();
        long startLatency = (processingStartTime - requestStartTime) / 1_000_000; // Convert to ms
      //  LOG.info("Start-to-processing latency: {} ms", startLatency);

        if (databaseConfiguration.authenticate(hashValue)) {
            MappingPolicyDB mapping = databaseConfiguration.getMappingByHashValue(hashValue);
            if (mapping != null) {
                String requestFilePath = mapping.getPolicyReqPath();
                String requestContent = new String(Files.readAllBytes(new File(requestFilePath).toPath()), StandardCharsets.UTF_8);

                // Measure Post-to-Processing latency
                long postProcessingStartTime = System.nanoTime();
                long postToProcessingLatency = (postProcessingStartTime - processingStartTime) / 1_000_000;
            //    LOG.info("Post-to-processing latency: {} ms", postToProcessingLatency);

                // Measure memory usage before PDP
          //      logMemoryUsage("Before PDP evaluation");

                // Measure Before-PDP latency
                long beforePdpStartTime = System.nanoTime();
                long beforePdpLatency = (beforePdpStartTime - postProcessingStartTime) / 1_000_000;
          //      LOG.info("Before-PDP latency: {} ms", beforePdpLatency);

                // PDP processing and response time
                long pdpStartTime = System.nanoTime();
                boolean pdpDecision = pdpService.updateRemotePDPServer(requestContent);
                long pdpEndTime = System.nanoTime();
                long pdpResponseTime = (pdpEndTime - pdpStartTime) / 1_000_000;
                long pdpLatency = (pdpStartTime - postProcessingStartTime) / 1_000_000;

            //    logMemoryUsage("After PDP evaluation");

                // Measure After-PDP latency
                long afterPdpLatency = (pdpStartTime - postProcessingStartTime) / 1_000_000;
            //    LOG.info("After-PDP latency: {} ms", afterPdpLatency);

                if (pdpDecision) {
                    // Measure Before-PRE latency
                    long beforePreStartTime = System.nanoTime();
                    long beforePreLatency = (beforePreStartTime - pdpEndTime) / 1_000_000;
             //       LOG.info("Before-PRE latency: {} ms", beforePreLatency);

                    byte[] onlyData;
                    try (FileInputStream read = new FileInputStream(new File(dataFolder + count))) {
                        onlyData = read.readAllBytes();
                    }

                    // Read data to be re-encrypted
                    Map<String, byte[]> data = DataHandler.readOnlyData(onlyData);

                    // Measure memory usage before PRE
             //       logMemoryUsage("Before PRE re-encryption");

                    // PRE processing and response time
                    long preStartTime = System.nanoTime();
                    byte[] reEncryptedData = proxyReEncryptionService.reEncrypt(data.get("data"), principal);
                    long preEndTime = System.nanoTime();
                    long preResponseTime = (preEndTime - preStartTime) / 1_000_000;
                    long preLatency = (preStartTime - beforePreStartTime) / 1_000_000;

            //        logMemoryUsage("After PRE re-encryption");

                    // Measure After-PRE latency
                    long afterPreLatency = (System.nanoTime() - preEndTime) / 1_000_000;
             //       LOG.info("After-PRE latency: {} ms", afterPreLatency);

                    // Increment request count
                    long currentCount = requestCount.incrementAndGet();

                    // Total end-to-end request time
                    long requestEndTime = System.nanoTime();
                    long totalRequestTime = (requestEndTime - requestStartTime) / 1_000_000;
                    // Accumulate total request time

                    // Calculate Total Processing Latency
                    long finishToProcessingLatency = (requestEndTime - postProcessingStartTime) / 1_000_000;
                    long totalProcessingLatency = startLatency + finishToProcessingLatency;


                    updateMetrics(totalProcessingLatency, totalRequestTime, preResponseTime, preLatency, pdpResponseTime, pdpLatency);

                    // Log cumulative metrics
                    logCumulativeMetrics(currentCount);

                    return ResponseEntity.ok(reEncryptedData);
                } else {
                    LOG.warn("Policy decision denied.");
                    return ResponseEntity.status(401).build();
                }
            } else {
                LOG.warn("Mapping not found for hash value.");
                return ResponseEntity.status(404).build();
            }
        }
        return ResponseEntity.status(401).build();
    }


    //Update all the cumulative metrics directly
    private void updateMetrics(long totalProcessingLatency, long totalRequestTime, long preResponseTime, long preLatency, long pdpResponseTime, long pdpLatency){
        totalPdpResponseTimeMillis.addAndGet(pdpResponseTime);
        totalPdpLatencyMillis.addAndGet(pdpLatency);
        totalProcessingLatencyMillis.addAndGet(totalProcessingLatency);
        totalRequestProcessingTimeMillis.addAndGet(totalRequestTime);
        totalPreResponseTimeMillis.addAndGet(preResponseTime);
        totalPreLatencyMillis.addAndGet(preLatency);
    }
    // Method to log cumulative metrics for benchmarking
    private void logCumulativeMetrics(long currentCount) {
        long totalPdpTime = totalPdpResponseTimeMillis.get();
        long totalPreTime = totalPreResponseTimeMillis.get();
        long totalProcessingTime = totalRequestProcessingTimeMillis.get();
        long totalProcessingLatency = totalProcessingLatencyMillis.get();
        long totalPdpLatency = totalPdpLatencyMillis.get();
        long totalPreLatency = totalPreLatencyMillis.get();
        long totalMemoryUsed = totalMemoryConsumed.get();
        long totalHeapMemoryUsedCumulative = totalHeapMemoryUsed.get();
        long totalNonHeapMemoryUsedCumulative = totalNonHeapMemoryUsed.get();

        LOG.info("Cumulative Metrics ({} requests):", currentCount);
        LOG.info("  - Total PDP Response Time: {} ms, Average: {} ms", totalPdpTime, totalPdpTime / currentCount);
        LOG.info("  - Total PDP Latency: {} ms, Average: {} ms", totalPdpLatency, totalPdpLatency / currentCount);
        LOG.info("  - Total PRE Response time: {} ms, Average: {} ms", totalPreTime, totalPreTime / currentCount);
        LOG.info("  - Total PRE Latency: {} ms, Average: {} ms", totalPreLatency, totalPreLatency / currentCount);
        LOG.info("  - Total Processing Time: {} ms, Average: {} ms", totalProcessingTime, totalProcessingTime / currentCount);
        LOG.info("  - Total Processing Latency: {} ms, Average: {} ms", totalProcessingLatency, totalProcessingLatency / currentCount);
        LOG.info("  - Total Memory Consumed: {} MB (Heap: {} MB, Non-Heap: {} MB), Average Memory Consumed: {} MB",
                totalMemoryUsed / 1024 / 1024,
                totalHeapMemoryUsedCumulative / 1024 / 1024,
                totalNonHeapMemoryUsedCumulative / 1024 / 1024,
                totalMemoryUsed / currentCount/ 1024 / 1024);
    }

    // Method to log memory usage and update total memory consumed
    private void logMemoryUsage(String phase) {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        long memoryUsed = heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed();
        totalMemoryConsumed.addAndGet(memoryUsed);

        totalHeapMemoryUsed.addAndGet(heapMemoryUsage.getUsed());
        totalNonHeapMemoryUsed.addAndGet(nonHeapMemoryUsage.getUsed());

        LOG.info("{} - Heap memory used: {} MB", phase, heapMemoryUsage.getUsed() / 1024 / 1024);
        LOG.info("{} - Non-heap memory used: {} MB", phase, nonHeapMemoryUsage.getUsed() / 1024 / 1024);
    }
}




/* //Controller without PDP and PRE Response Time
package unipassau.thesis.vehicledatadissemination.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import unipassau.thesis.vehicledatadissemination.config.DatabaseConfiguration;
import unipassau.thesis.vehicledatadissemination.config.PolicyConfiguration;
import unipassau.thesis.vehicledatadissemination.model.MappingPolicyDB;
import unipassau.thesis.vehicledatadissemination.services.PDPService;
import unipassau.thesis.vehicledatadissemination.services.PolicyEnforcementService;
import unipassau.thesis.vehicledatadissemination.services.ProxyReEncryptionService;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.Encoder;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Map;

@RestController
public class BenchmarkController {

    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static int count;

    private final String POLICY_STORE_PATH = "policies";

    Logger LOG = LoggerFactory.getLogger(BenchmarkController.class);

    @Autowired
    private PolicyEnforcementService policyEnforcementService;

    @Autowired
    private ProxyReEncryptionService proxyReEncryptionService;

    @Autowired
    private PDPService pdpService;

    @Autowired
    private PolicyConfiguration policyConfiguration;

    @Autowired
    private DatabaseConfiguration databaseConfiguration;

    @RequestMapping(method = RequestMethod.POST, value = "/benchmark")
    public ResponseEntity<byte[]> authorize(InputStream dataStream, @RequestParam("count") int count) throws Exception {
        this.count = count;

        // Read binary file from request
        byte[] onlyHash = dataStream.readAllBytes();

        // Separate hash and data
        Map<String, byte[]> stickyDocumentMap = DataHandler.readOnlyHash(onlyHash);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Principal principal = request.getUserPrincipal();

        // Convert hash to string and get the policy filename
        String hashValue = Encoder.bytesToHex(stickyDocumentMap.get("hash"));
        String policyFilename = policyConfiguration.policyMap().get(hashValue);

        if (databaseConfiguration.authenticate(hashValue)) {
            MappingPolicyDB mapping = databaseConfiguration.getMappingByHashValue(hashValue);
            if (mapping != null) {
                String requestFilePath = mapping.getPolicyReqPath();
                String requestContent = new String(Files.readAllBytes(new File(requestFilePath).toPath()), StandardCharsets.UTF_8);

                boolean pdpDecision = pdpService.updateRemotePDPServer(requestContent);

                if (pdpDecision) {
                    byte[] onlyData = null;
                    try (FileInputStream read = new FileInputStream(new File(dataFolder + count))) {
                        onlyData = read.readAllBytes();
                    } catch (IOException e) {
                        LOG.error("Error reading data file", e);
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                    // Read data to be re-encrypted
                    Map<String, byte[]> data = DataHandler.readOnlyData(onlyData);
                    byte[] reEncryptedData;
                    try {
                        reEncryptedData = proxyReEncryptionService.reEncrypt(data.get("data"), principal);
                    } catch (Exception e) {
                        LOG.error("Re-encryption error", e);
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    return new ResponseEntity<>(reEncryptedData, HttpStatus.OK);

                } else {
                    LOG.warn("Policy decision denied.");
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                LOG.warn("Mapping not found for hash value.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
*/