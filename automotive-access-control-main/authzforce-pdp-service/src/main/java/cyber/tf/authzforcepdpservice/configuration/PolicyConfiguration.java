package cyber.tf.authzforcepdpservice.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cyber.tf.authzforcepdpservice.util.Encoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

@Configuration
public class PolicyConfiguration {

    private final Logger logger = LoggerFactory.getLogger(PolicyConfiguration.class);

    // Set the location of the stored policies
    private final String POLICY_STORE_PATH = "policies";

    // Loading policies into a HashMap that maps policies to their hash value
    @Bean
    public HashMap<String, String> policyMap() throws FileNotFoundException {
        logger.info("Loading policies configuration.");
        HashMap<String, String> policyMap = new HashMap<>() ;
        File directoryPath = new File(POLICY_STORE_PATH);
        File filesList[] = directoryPath.listFiles();
        // Load hash value of each policy into a HashMap
        for(File file : filesList) {
            logger.info("Loading policy : "+ file.getName() );
            policyMap.put(Encoder.bytesToHex(Encoder.xmlToHash(file.getAbsolutePath()))
                    , file.getName());
        }
        logger.info(policyMap.toString());
        logger.info("Loaded policies configuration.");

        return policyMap;
    }
}
