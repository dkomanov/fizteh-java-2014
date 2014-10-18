package ru.fizteh.fivt.students.SergeyAksenov.shell;

public class ErrorHandler {

    protected static final void countArguments(final String command)
            throws ShellException {
        throw new ShellException(
                command + ": invalid number of arguments");
    }

    protected static final void noFile(String command, String file)
            throws ShellException {
        throw new ShellException(
                command + ": " + file + ": No such file or directory");
    }

    protected static final void fileExist(String command, String file)
            throws ShellException {
        throw new ShellException(
                command + ": " + file + ": File already exists");
    }

    protected static final void canNotCreate(String command)
            throws ShellException {
        throw new ShellException(
                command + ": cannot create file or directory");
    }

    protected static final void isDirectory(String command, String file)
            throws ShellException {
        throw new ShellException(
                command + ": " + file + ": is a directory");
    }

    protected static final void invalidArgument(String command, String arg)
            throws ShellException {
        throw new ShellException(
                command + ": " + arg + ": Invalid argument");
    }

    protected static final void canNotPerform(String command)
            throws ShellException {
        throw new ShellException(
                command + ": cannot perform this operation");
    }

    protected static final void unknownCommand(String command)
            throws ShellException {
        throw new ShellException(
                command + ": unknown command");
    }
}
