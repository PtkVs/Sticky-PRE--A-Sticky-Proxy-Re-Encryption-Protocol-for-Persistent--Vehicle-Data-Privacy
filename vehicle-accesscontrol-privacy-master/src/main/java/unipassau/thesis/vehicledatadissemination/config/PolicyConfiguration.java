package unipassau.thesis.vehicledatadissemination.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unipassau.thesis.vehicledatadissemination.model.MappingPolicyDB;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

@Configuration
public class PolicyConfiguration {



    private final Logger logger = LoggerFactory.getLogger(PolicyConfiguration.class);

    // Set the location of the stored policies
    private final String POLICY_STORE_PATH = "policies";

    @Autowired //yesle suppose DB x = new Db(); 'new ' ko kaam autowired le garxa so new instantiate
    private DatabaseConfiguration policyMapping; //direct inject,  so new object instantiate vayo autowired le kun class vanda DatabaseConfiguration class ko and given to object variable policyMapping

    /* private final DatabaseConfiguration policyMapping;
    @Autowired
    //public PolicyConfiguration(DatabaseConfiguration policyMapping) {     yo garnu ra line 23-24 eutai ho cz yesma Autowire garda DatabaseConfi. ko object innstace create vayera constructor ko policyMapping object ma janxa tya bata this.policyMapping ma pass vayera final DB.. policyMapping object ma pass hunxa
    // this.policyMapping = policyMapping;
    } */

    // Loading policies into a HashMap that maps policies to their hash value

    @Bean
    public HashMap<String, String> policyMap() throws FileNotFoundException {
        logger.info("Loading policies configuration.");
        HashMap<String, String> policyMap = new HashMap<>() ;
        File directoryPath = new File(POLICY_STORE_PATH);
        File filesList[] = directoryPath.listFiles();


            // Load hash value of each policy into a HashMap
            for (File file : filesList) {
                logger.info("Loading policy : " + file.getName());

                //Note this is different to original code
                String hashValue = Encoder.bytesToHex(Encoder.xmlToHash(file.getAbsolutePath()));
                String policyName = file.getName();

                //Loading XML policy for sending to PDP server. This line creates a PolicyFile object representing a policy file along with its name and file reference.
                // PolicyFile policyFile = new PolicyFile(policyName, file);

                //Store policy in the database using the service
                policyMapping.saveMappings(hashValue, policyName);


                policyMap.put(hashValue, policyName);
            }

        logger.info(policyMap.toString());
        logger.info("Loaded policies configuration.");


        // Print all mappings which was saved in the db just checking ko lagi matra yeha PolicyConfiguratin ma rakheko nothing else
       policyMapping.printAllMappings();

        return policyMap;
    }





}



