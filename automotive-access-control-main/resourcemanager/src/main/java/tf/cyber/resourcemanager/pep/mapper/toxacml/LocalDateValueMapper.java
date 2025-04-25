package tf.cyber.resourcemanager.pep.mapper.toxacml;

import java.time.LocalDate;

public class LocalDateValueMapper implements JavaToXACMLMapper<LocalDate> {
    @Override
    public String map(LocalDate obj) {
        return obj.toString();
    }
}
