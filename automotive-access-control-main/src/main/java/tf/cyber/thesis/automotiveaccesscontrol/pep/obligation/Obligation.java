package tf.cyber.thesis.automotiveaccesscontrol.pep.obligation;

import org.springframework.stereotype.Component;
import tf.cyber.thesis.automotiveaccesscontrol.pep.data.XACMLAttribute;

import java.util.List;

@Component
public abstract class Obligation {
    public abstract void execute(List<XACMLAttribute<?>> args);
    public abstract String getType();
}
