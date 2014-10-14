package ru.fizteh.fivt.students.Oktosha.ConsoleUtility;

/**
 * The exception which is thrown when there is a syntax error in arguments
 * given to command of ConsoleUtility.
 */
public class CommandArgumentSyntaxException extends ConsoleUtilitySyntaxException {
    public CommandArgumentSyntaxException(String s) {
        super(s);
    }
}
