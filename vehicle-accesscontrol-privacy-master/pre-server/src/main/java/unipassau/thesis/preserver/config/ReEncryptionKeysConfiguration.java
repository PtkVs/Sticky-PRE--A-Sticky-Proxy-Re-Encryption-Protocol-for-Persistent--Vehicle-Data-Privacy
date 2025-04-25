package unipassau.thesis.preserver.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

@Configuration
public class ReEncryptionKeysConfiguration {

    private final Logger logger = LoggerFactory.getLogger(ReEncryptionKeysConfiguration.class);

    private final String RE_ENCRYPTION_KEYS_FOLDER = "crypto/re-encryption-keys";


    @Bean
    public HashMap<String, String> reEncryptionKeysMap() throws FileNotFoundException {

        logger.info("Loading re-encryption keys configuration.");
        HashMap<String, String> reEncryptionKeysMap = new HashMap<>() ;
        File directoryPath = new File(RE_ENCRYPTION_KEYS_FOLDER);
        File filesList[] = directoryPath.listFiles();

        for(File file : filesList) {
            logger.info("Loading re-enryption key : "+ file.getName() );
            String[] stngArray = StringUtils. substringsBetween(file.getName(), "2", "-");

            reEncryptionKeysMap.put(stngArray[0], file.getName());
        }
        logger.info(reEncryptionKeysMap.toString());
        logger.info("Loaded re-encryption keys configuration.");

        return reEncryptionKeysMap;
    }
}
