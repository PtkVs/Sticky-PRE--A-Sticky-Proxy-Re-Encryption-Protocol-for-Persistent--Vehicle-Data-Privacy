package tf.cyber.resourcemanager.pep.mapper.tojava;

import tf.cyber.resourcemanager.pep.data.UnsupportedDataTypeException;

import java.util.Map;

public class JavaMapper {
    public final static Map<Class<?>, XACMLToJavaMapper> dataMapper = Map.of(
            Boolean.class, new BooleanValueMapper(),
            Double.class, new DoubleValueMapper(),
            Integer.class, new IntegerValueMapper(),
            String.class, new StringValueMapper()
    );

    private JavaMapper() {
        // do not instantiate.
    }

    public static Object map(Class<?> type, String value) {
        if (dataMapper.containsKey(type)) {
            return dataMapper.get(type).map(value);
        } else {
            throw new UnsupportedDataTypeException();
        }
    }
}
