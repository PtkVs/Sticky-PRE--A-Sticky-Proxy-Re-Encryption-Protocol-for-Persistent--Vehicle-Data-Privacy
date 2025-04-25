package tf.cyber.thesis.automotiveaccesscontrol.pep.obligation;

public class ObligationNotImplementedException extends RuntimeException {
    public ObligationNotImplementedException() {
        super("Obligation type has not been implemented!");
    }
}
