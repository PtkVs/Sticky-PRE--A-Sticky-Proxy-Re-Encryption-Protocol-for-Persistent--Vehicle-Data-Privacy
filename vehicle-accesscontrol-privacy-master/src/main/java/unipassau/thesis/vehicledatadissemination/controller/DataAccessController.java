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
import unipassau.thesis.vehicledatadissemination.config.PolicyFile;
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
import java.util.HashMap;
import java.util.Map;

@RestController
public class DataAccessController {

    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static int count;

    private final String POLICY_STORE_PATH = "policies";

    Logger LOG = LoggerFactory.getLogger(DataAccessController.class);


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


    @RequestMapping(method = RequestMethod.POST, value = "/authorize")
    public ResponseEntity<byte[]> authorize(InputStream dataStream, @RequestParam("count") int count) throws Exception {   //bob le request garyo with necessary crediantials along with binary data which is encrypted data(stickyDocument) aba teslai evaluate ko lagi processing
        //Set the dynamic count value
        this.count = count;
        // Read the binary file contained in the body of the request
        byte[] onlyHash = dataStream.readAllBytes();

        // Seperate the hash value and the ciphertext from the input file
        Map<String, byte[]> stickyDocumentMap = DataHandler.readOnlyHash(onlyHash);  // Datahandler class lai call garera binary data ko hash ra data lai sperate hune kam garyo and returnd hash and data


        // Get the attributes of the user from the context handler
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())  //attributes like bob ko username and password  etc haru liyo
                .getRequest();

        //Principal class le match garyo like above attributes request ma ayeko  for authentication and authorization
        Principal principal = request.getUserPrincipal();

        //Get the attributes of the user from the request
        String uri = request.getRequestURI();
        String method = request.getMethod();


        //Conversion of byte[] to string
        String hashValue = Encoder.bytesToHex(stickyDocumentMap.get("hash"));
        String policyFilename = policyConfiguration.policyMap().get(hashValue);

        // Authenticate the hash value
        if (databaseConfiguration.authenticate(hashValue)) {
            // Fetch the request file path from the database
            MappingPolicyDB mapping = databaseConfiguration.getMappingByHashValue(hashValue);
            if (mapping != null) {
                String requestFilePath = mapping.getPolicyReqPath();

                // Read the content of the request file
                String requestContent = new String(Files.readAllBytes(new File(requestFilePath).toPath()), StandardCharsets.UTF_8);

                // Send the content to the PDP server
                boolean pdpDecision = pdpService.updateRemotePDPServer(requestContent);

                if (pdpDecision) {
                    // Permit decision
                    byte[] onlyData = null;
                    try {
                        FileInputStream read = new FileInputStream(new File(dataFolder + count));
                        onlyData = read.readAllBytes();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Map<String, byte[]> data = DataHandler.readOnlyData(onlyData);
                    return new ResponseEntity<>(proxyReEncryptionService.reEncrypt
                            (data.get("data"), principal), HttpStatus.OK);

                    // ki yeha yesari garnu paryo data manipulation hoin vane re-encrypt garnu vanda agadi nai garnu paryo cz suru ma data Alice le encrypt garda sabbai data encrypt gareko hunxa and hash add hunxa, tesma attribute filter garera feri encrypt garnu parxa by alice then only re-encrypt garna milxa cz tyo vayena vane cerial exception auxa cz crypto keys match nai hudaina as encrypt vako binary file lai naya binary file banayo jasko kei link xaina with alice and tyo file lai re-encrypt garna pathauda cerial exception error ayo
                        /* byte[] onlyData = null;
                try {
                    FileInputStream read = new FileInputStream(new File(dataFolder + count));
                    onlyData = read.readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Package data with flags for authorized attributes
                Map<String, byte[]> data = DataHandler.readOnlyData(onlyData);
                Map<String, byte[]> authorizedData = new HashMap<>();
                authorizedData.put("header", data.get("header"));
                authorizedData.put("timestamp", data.get("timestamp"));
                authorizedData.put("quality", data.get("quality"));

                // Prepare authorized data for re-encryption
                byte[] encryptedData = proxyReEncryptionService.reEncrypt(authorizedData, principal);*/

                } else {
                    System.out.println("Process Terminated as the hash do not match");
                    System.exit(0);

                }
            } else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}