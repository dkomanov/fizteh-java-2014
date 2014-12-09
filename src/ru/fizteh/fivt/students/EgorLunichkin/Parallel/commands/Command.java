package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.ParallelException;

public interface Command {
    void run() throws ParallelException;
}
