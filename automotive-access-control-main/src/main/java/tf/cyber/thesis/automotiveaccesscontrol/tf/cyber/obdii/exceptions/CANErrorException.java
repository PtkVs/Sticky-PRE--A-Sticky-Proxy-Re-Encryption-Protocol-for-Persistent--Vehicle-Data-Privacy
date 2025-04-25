package tf.cyber.obdii.exceptions;

public class CANErrorException extends RuntimeException{
    public CANErrorException() {
        super("A CAN error occurred");
    }
}
