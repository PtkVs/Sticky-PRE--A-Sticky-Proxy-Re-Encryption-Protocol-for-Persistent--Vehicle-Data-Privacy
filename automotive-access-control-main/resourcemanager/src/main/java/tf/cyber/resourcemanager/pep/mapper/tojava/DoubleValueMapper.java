package tf.cyber.resourcemanager.pep.mapper.tojava;

public class DoubleValueMapper implements XACMLToJavaMapper<Double> {
    @Override
    public Double map(String obj) {
        return Double.parseDouble(obj);
    }
}
