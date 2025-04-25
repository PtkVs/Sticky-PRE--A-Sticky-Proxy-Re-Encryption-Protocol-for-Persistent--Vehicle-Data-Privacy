package tf.cyber.thesis.automotiveaccesscontrol.pep.data;

public class UnsupportedDataTypeException extends RuntimeException{
    public UnsupportedDataTypeException() {
        super("Data type is not supported by any of the available mappers.");
    }
}
