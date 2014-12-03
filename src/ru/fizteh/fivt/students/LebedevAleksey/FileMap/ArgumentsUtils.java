package ru.fizteh.fivt.students.LebedevAleksey.FileMap;

import javafx.util.Pair;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParsedCommand;

public class ArgumentsUtils {
    public static void assertNoArgs(ParsedCommand command) throws ArgumentException {
        if (command.getArguments().length != 0) {
            throw new ArgumentException("This command should have no args.");
        }
    }

    public static String get1Args(ParsedCommand command) throws ArgumentException {
        if (command.getArguments().length == 1) {
            return command.getArguments()[0];
        } else {
            throw new ArgumentException("This command should have one argument.");
        }
    }

    public static Pair<String, String> get2Args(ParsedCommand command) throws ArgumentException {
        String[] arguments = command.getArguments();
        if (arguments.length == 2) {
            return new Pair<>(arguments[0], arguments[1]);
        } else {
            throw new ArgumentException("This command should have two arguments.");
        }
    }
}
