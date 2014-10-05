package ru.fizteh.fivt.students.SergeyAksenov.shell;

public class ErrorHandler {

    protected final static void countArguments(final String command)
            throws ShellException {
        throw new ShellException(
                command + ": invalid number of arguments");
    }

    protected final static void noFile(String command, String file)
            throws ShellException {
        throw new ShellException(
                command + ": " + file + ": No such file or directory");
    }

    protected final static void fileExist(String command, String file)
            throws ShellException {
        throw new ShellException(
                command + ": " + file + ": File already exists");
    }

    protected final static void canNotCreate(String command)
            throws ShellException {
        throw new ShellException(
                command + ": cannot create file or directory");
    }

    protected final static void isDirectory(String command, String file)
            throws ShellException {
        throw new ShellException(
                command + ": " + file + ": is a directory");
    }

    protected final static void invalidArgument(String command, String arg)
            throws ShellException {
        throw new ShellException(
                command + ": " + arg + ": Invalid argument");
    }

    protected final static void canNotPerform(String command)
            throws ShellException {
        throw new ShellException(
                command + ": cannot perform this operation");
    }

    protected final static void unknownCommand(String command)
            throws ShellException {
        throw new ShellException(
                command + ": unknown command");
    }
}
