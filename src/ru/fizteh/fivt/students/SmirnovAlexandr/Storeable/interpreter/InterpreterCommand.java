package ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.interpreter;

import java.io.PrintStream;

public interface InterpreterCommand {

    String getName();

    void exec(final PrintStream out, final PrintStream err) throws TerminateInterpeterException;
}
