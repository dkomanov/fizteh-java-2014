package storeable.util;

@SuppressWarnings("serial")
public class ExitException extends Exception {
    public ExitException(String message) {
        super(message);
    }

    public ExitException() {
        super();
    }
}
