public class CalculatorException extends Exception {
    public CalculatorException (String msg)
    {
        super("ERROR: " + msg);
    }
}
