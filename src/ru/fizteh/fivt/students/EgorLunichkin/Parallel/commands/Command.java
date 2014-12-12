package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

import java.io.IOException;

public interface Command {
    void run() throws ParallelException, ExitException, IOException;
    void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException;
    int minArguments();
    int maxArguments();
}
