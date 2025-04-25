package tf.cyber.thesis.automotiveaccesscontrol.pep.mapper.tojava;

public class IntegerValueMapper implements XACMLToJavaMapper<Integer>{
    @Override
    public Integer map(String obj) {
        return Integer.parseInt(obj);
    }
}
