package tf.cyber.resourcemanager.pep.obligation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ObligationService {
    @Autowired
    @Qualifier("obligations")
    private Map<String, Obligation> obligations;

    public Obligation get(String id) {
        if (!obligations.containsKey(id)) {
            throw new ObligationNotImplementedException();
        }

        return obligations.get(id);
    }
}
