package tf.cyber.obdii.exceptions;

public class InvalidCommandException extends RuntimeException{
    public InvalidCommandException() {
        super("OBD Command is invalid.");
    }
}
