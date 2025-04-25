package tf.cyber.resourcemanager.pep.mapper.tojava;

public class StringValueMapper implements XACMLToJavaMapper<String> {
    @Override
    public String map(String obj) {
        return obj;
    }
}
