package ru.fizteh.fivt.students.dnovikov.junit.Interpreter;

import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.WrongNumberOfArgumentsException;

public interface Command {
    String getName();
    void invoke(InterpreterState state, String[] args) throws WrongNumberOfArgumentsException;
}
