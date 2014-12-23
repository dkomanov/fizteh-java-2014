package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

import java.io.InputStream;
import java.io.PrintStream;

public interface InterpreterState {
    InputStream getInputStream();

    PrintStream getOutputStream();

    PrintStream getErrorStream();

    boolean isExitSafe();
}
