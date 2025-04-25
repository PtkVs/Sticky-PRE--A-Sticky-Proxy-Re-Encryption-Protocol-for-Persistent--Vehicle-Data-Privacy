package tf.cyber.resourcemanager.pep.obligation;

public class ObligationNotImplementedException extends RuntimeException {
    public ObligationNotImplementedException() {
        super("Obligation type has not been implemented!");
    }
}
