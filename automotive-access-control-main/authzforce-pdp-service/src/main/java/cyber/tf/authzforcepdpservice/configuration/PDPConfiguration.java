package cyber.tf.authzforcepdpservice.configuration;

import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class PDPConfiguration {
    private final Logger logger = LoggerFactory.getLogger(PDPConfiguration.class);

    private final String PDP_CONFIG_PATH = "pdp/pdp.xml";
    private final String PDP_CATALOG_PATH = "pdp/catalog.xml";
    private final String PDP_EXTENSION_PATH = "pdp/pdp-ext.xsd";

    @Bean
    public PdpEngineConfiguration pdpEngineConfiguration() throws IOException {
        // Instantiate PDP which also loads policies from disk.
        logger.info("Loading XACML PDP configuration.");

        PdpEngineConfiguration pdpEngineConfiguration;

        pdpEngineConfiguration = PdpEngineConfiguration
                .getInstance(PDP_CONFIG_PATH, PDP_CATALOG_PATH, PDP_EXTENSION_PATH);

        logger.info("Loaded XACML PDP configuration.");

        return pdpEngineConfiguration;
    }
}
