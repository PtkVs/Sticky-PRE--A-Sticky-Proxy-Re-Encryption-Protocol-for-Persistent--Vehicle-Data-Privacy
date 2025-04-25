package tf.cyber.resourcemanager.pep.mapper.toxacml;

import java.time.LocalDateTime;

public class LocalDateTimeValueMapper implements JavaToXACMLMapper<LocalDateTime> {
    @Override
    public String map(LocalDateTime obj) {
        return obj.toString();
    }
}
