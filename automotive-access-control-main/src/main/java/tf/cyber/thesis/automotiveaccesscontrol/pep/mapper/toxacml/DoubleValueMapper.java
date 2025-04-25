package tf.cyber.thesis.automotiveaccesscontrol.pep.mapper.toxacml;

public class DoubleValueMapper implements JavaToXACMLMapper<Double> {
    @Override
    public String map(Double obj) {
        return obj.toString();
    }
}
