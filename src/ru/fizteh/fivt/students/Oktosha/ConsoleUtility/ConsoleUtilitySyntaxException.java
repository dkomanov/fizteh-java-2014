package ru.fizteh.fivt.students.Oktosha.ConsoleUtility;

/**
 * Child class of ConsoleUtilityException.
 * It is thrown when command has invalid syntax.
 */

public class ConsoleUtilitySyntaxException extends ConsoleUtilityException {
    ConsoleUtilitySyntaxException(String s) {
        super(s);
    }
}
