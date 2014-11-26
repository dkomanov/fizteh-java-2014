package ru.fizteh.fivt.students.kolmakov_sergey.shell;

public class ErrorHolder {

    protected static void errorCountArguments(String command)
            throws ShellException {
        throw new ShellException(
                command + ": invalid number of arguments");
    }

    protected static void errorNoFile(String command, String file)
            throws ShellException {
        throw new ShellException(
                command + ": " + file + ": No such file or directory");
    }

    protected static void errorFileExists(String command, String file)
            throws ShellException {
        throw new ShellException(
                command + ": " + file + ": File already exists");
    }

    protected static void errorCreatingFile(String command)
            throws ShellException {
        throw new ShellException(
                command + ": cannot create file or directory");
    }

    protected static void errorIsDirectory(String command, String file)
            throws ShellException {
        throw new ShellException(
                command + ": " + file + ": is a directory");
    }

    protected static void errorInvalidArgument(String command, String arg)
            throws ShellException {
        throw new ShellException(
                command + ": " + arg + ": Invalid argument");
    }

    protected static void errorCannotPerform(String command)
            throws ShellException {
        throw new ShellException(
                command + ": cannot perform this operation");
    }

    protected static void errorUnknownCommand(String command)
            throws ShellException {
        throw new ShellException(
                command + ": unknown command");
    }
}
