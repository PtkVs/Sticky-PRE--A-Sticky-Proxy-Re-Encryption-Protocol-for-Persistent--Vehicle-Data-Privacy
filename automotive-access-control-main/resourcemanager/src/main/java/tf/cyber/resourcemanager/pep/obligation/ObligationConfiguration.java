package tf.cyber.resourcemanager.pep.obligation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ObligationConfiguration {
    @Autowired
    private List<Obligation> obligations;

    @Bean("obligations")
    public Map<String, Obligation> obligationsMap() {
        Map<String, Obligation> obligationMap = new HashMap<>();

        for (Obligation obligation : obligations) {
            obligationMap.put(obligation.getType(), obligation);
        }

        return obligationMap;
    }
}
