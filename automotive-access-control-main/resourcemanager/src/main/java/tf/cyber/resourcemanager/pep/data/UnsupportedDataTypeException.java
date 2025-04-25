package tf.cyber.resourcemanager.pep.data;

public class UnsupportedDataTypeException extends RuntimeException{
    public UnsupportedDataTypeException() {
        super("Data type is not supported by any of the available mappers.");
    }

    public UnsupportedDataTypeException(Class<?> clazz) {
        super("Data type (" + clazz.getName() + ") is not supported by any of the available mappers.");
    }
}
