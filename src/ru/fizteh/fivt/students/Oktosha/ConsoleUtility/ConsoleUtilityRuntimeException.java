package ru.fizteh.fivt.students.Oktosha.ConsoleUtility;

/**
 * This exception is thrown when an error during running command occurs.
 */
public class ConsoleUtilityRuntimeException extends ConsoleUtilityException {
    public ConsoleUtilityRuntimeException(String s) {
        super(s);
    }
    public ConsoleUtilityRuntimeException(String s, Exception e) {
        super(s, e);
    }
}
