package DbCommands;

public class WrongSyntaxException extends IllegalArgumentException {
    public WrongSyntaxException() { }
    public WrongSyntaxException(final String msg) {
        super(msg + ": wrong syntax");
    }
}
