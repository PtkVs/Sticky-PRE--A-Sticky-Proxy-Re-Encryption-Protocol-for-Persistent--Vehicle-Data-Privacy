package tf.cyber.thesis.automotiveaccesscontrol.pep.mapper.tojava;

public class BooleanValueMapper implements XACMLToJavaMapper<Boolean> {
    @Override
    public Boolean map(String obj) {
        return Boolean.parseBoolean(obj);
    }
}
