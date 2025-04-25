package tf.cyber.resourcemanager.pep;

public class XACMLEvaluationException extends RuntimeException{
    public XACMLEvaluationException() {
        super("Failed evaluating XACML request against the PDP. Is the PDP working?");
    }

    public XACMLEvaluationException(String message) {
        super(message);
    }
}
