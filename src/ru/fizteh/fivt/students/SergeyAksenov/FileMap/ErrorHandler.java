package ru.fizteh.fivt.students.SergeyAksenov.FileMap;

public class ErrorHandler {

    protected static final void countArguments(final String command)
            throws FileMapException {
        throw new FileMapException(
                command + ": invalid number of arguments");
    }
//
    protected static final void canNotPerform(String command)
            throws FileMapException {
        throw new FileMapException(
                command + ": cannot perform this operation");
    }

    protected static final void unknownCommand(String command)
            throws FileMapException {
        throw new FileMapException(
                command + ": unknown command");
    }
}
