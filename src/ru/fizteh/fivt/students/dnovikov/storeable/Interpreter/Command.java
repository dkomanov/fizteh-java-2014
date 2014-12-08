package ru.fizteh.fivt.students.dnovikov.storeable.Interpreter;

import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.WrongNumberOfArgumentsException;

public interface Command {
    String getName();

    void invoke(InterpreterState state, String[] args) throws WrongNumberOfArgumentsException;
}
