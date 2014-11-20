package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Command;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.ShellState;

public class WrongArgsNumberException extends IllegalArgumentException {

    private static final long serialVersionUID = 3047879410299126653L;

    /**
     * Constructs a new instance of this.
     * @param command
     *         Command, attempted to be invoked.
     * @param name
     *         Alias of this command it is called by.
     * @param <S>
     *         Shell state that commands works with
     */
    public <S extends ShellState<S>> WrongArgsNumberException(Command<S> command, String name) {
        super(makeMessage(command, name));
    }

    private static <S extends ShellState<S>> String makeMessage(Command<S> command, String name) {
        StringBuilder sb = new StringBuilder("Wrong arguments number; invocation: ");
        sb.append(name);
        if (command.getInvocation() != null) {
            sb.append(' ').append(command.getInvocation());
        }
        return sb.toString();
    }
}
