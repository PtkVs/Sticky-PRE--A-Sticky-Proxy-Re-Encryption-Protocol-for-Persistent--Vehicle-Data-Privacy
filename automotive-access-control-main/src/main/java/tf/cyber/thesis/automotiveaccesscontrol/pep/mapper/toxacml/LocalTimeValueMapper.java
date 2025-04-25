package tf.cyber.thesis.automotiveaccesscontrol.pep.mapper.toxacml;

import java.time.LocalTime;

public class LocalTimeValueMapper implements JavaToXACMLMapper<LocalTime> {
    @Override
    public String map(LocalTime obj) {
        return obj.toString();
    }
}
