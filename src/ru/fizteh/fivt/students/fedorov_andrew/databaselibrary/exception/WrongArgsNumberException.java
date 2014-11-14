package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Command;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.ShellState;

public class WrongArgsNumberException extends InvocationException {

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
        super(command, name, "Wrong arguments number");
    }

}
