package tf.cyber.resourcemanager.pep.obligation;

public class ObligationFulfilmentFailedException extends RuntimeException {
    public ObligationFulfilmentFailedException() {
        super("Failed to fulfil obligation!");
    }

    public ObligationFulfilmentFailedException(String message) {
        super(message);
    }
}
