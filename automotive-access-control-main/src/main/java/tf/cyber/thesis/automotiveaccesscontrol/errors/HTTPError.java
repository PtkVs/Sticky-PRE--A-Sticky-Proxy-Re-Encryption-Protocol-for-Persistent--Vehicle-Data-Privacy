package tf.cyber.thesis.automotiveaccesscontrol.errors;

public class HTTPError {
    private final int code;
    private final String message;

    public HTTPError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
