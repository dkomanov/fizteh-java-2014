package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Command;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.ShellState;

public class InvocationException extends Exception {
    /**
     * Constructs a new instance of this.
     * @param command
     *         Command, attempted to be invoked.
     * @param name
     *         Alias of this command it is called by.
     * @param <S>
     *         Shell state that commands works with
     */
    public <S extends ShellState<S>> InvocationException(Command<S> command, String name, String message) {
        super(makeMessage(command, name, message));
    }

    private static <S extends ShellState<S>> String makeMessage(Command<S> command,
                                                                String name,
                                                                String message) {
        StringBuilder sb = new StringBuilder(message).append("; invocation: ");
        sb.append(name);
        if (command.getInvocation() != null) {
            sb.append(' ').append(command.getInvocation());
        }
        return sb.toString();
    }
}
