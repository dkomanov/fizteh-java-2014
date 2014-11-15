package ru.fizteh.fivt.students.dmitry_persiyanov.interpreter;

import java.io.PrintStream;

public interface InterpreterCommand {
    /**
     * @return InterpreterCommand Name in String
     */
    String getName();

    /**
     * Executes command. It can throw TerminateInterpreterException where it is need to stop interpreter
     *
     * @param out Output stream.
     * @param err Error stream.
     */
    void exec(final PrintStream out, final PrintStream err) throws TerminateInterpeterException;
}
