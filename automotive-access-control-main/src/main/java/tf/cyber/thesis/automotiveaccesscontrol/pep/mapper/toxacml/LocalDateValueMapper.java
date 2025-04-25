package tf.cyber.thesis.automotiveaccesscontrol.pep.mapper.toxacml;

import java.time.LocalDate;

public class LocalDateValueMapper implements JavaToXACMLMapper<LocalDate> {
    @Override
    public String map(LocalDate obj) {
        return obj.toString();
    }
}
