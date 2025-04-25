/* package cyber.tf.authzforcepdpservice.controller;

import cyber.tf.authzforcepdpservice.benchmark.Benchmark;
import cyber.tf.authzforcepdpservice.benchmark.BenchmarkResult;
import cyber.tf.authzforcepdpservice.service.PDPService;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;

@Controller
@RequestMapping("/benchmark")
public class BenchmarkController {
    private final Logger logger = LoggerFactory.getLogger(PDPController.class);

    @Autowired
    private Environment env;

    @Autowired
    private PDPService pdpService;

    @RequestMapping(value = "/authorize",
            method = RequestMethod.POST,
            consumes = {"application/xml", "application/xacml+xml"},
            produces = {"text/csv"}
    )
    public ResponseEntity evaluateXML(@RequestBody String requestString) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Request.class);
        Request request = (Request) context.createUnmarshaller().unmarshal(new StringReader(requestString));

        int runs = Integer.parseInt(env.getProperty("benchmark.runs"));
        int warmupRuns = Integer.parseInt(env.getProperty("benchmark.warmup.runs"));

        logger.info("Warm-Up for benchmarking XACXML XML request.");

        for (int i = 0; i < warmupRuns; i++) {
            Response res = pdpService.getXMLAdapter().evaluate(request);
        }

        Benchmark benchmark = new Benchmark();

        logger.info("Benchmarking XACXML XML request.");

        for (int i = 0; i < runs; i++) {
            long before = System.nanoTime();
            Response res = pdpService.getXMLAdapter().evaluate(request);
            long after = System.nanoTime();

            benchmark.addResult(new BenchmarkResult(i, after - before));
        }

        logger.info("Finished benchmarking XACXML XML request.");

        return ResponseEntity.ok().body(benchmark.toCSV());
    }

    @RequestMapping(value = "/authorize",
            method = RequestMethod.POST,
            consumes = {"application/json", "application/xacml+json"},
            produces = {"text/csv"}
    )
    public ResponseEntity evaluateJSON(@RequestBody String request) {
        JSONObject json = new JSONObject(new JSONTokener(request));

        int runs = Integer.parseInt(env.getProperty("benchmark.runs"));
        int warmupRuns = Integer.parseInt(env.getProperty("benchmark.warmup.runs"));

        logger.info("Warm-Up for benchmarking XACXML JSON request.");

        for (int i = 0; i < warmupRuns; i++) {
            JSONObject res = pdpService.getJSONAdapter().evaluate(json);
        }

        Benchmark benchmark = new Benchmark();

        logger.info("Benchmarking XACXML JSON request.");

        for (int i = 0; i < runs; i++) {
            long before = System.nanoTime();
            JSONObject res = pdpService.getJSONAdapter().evaluate(json);
            long after = System.nanoTime();

            benchmark.addResult(new BenchmarkResult(i, after - before));
        }

        logger.info("Finished benchmarking XACXML JSON request.");

        return ResponseEntity.ok().body(benchmark.toCSV());
    }
}
*/

