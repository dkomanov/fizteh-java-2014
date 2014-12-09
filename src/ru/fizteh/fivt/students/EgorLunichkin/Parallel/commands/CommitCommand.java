package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

import java.io.IOException;

public class CommitCommand implements Command {
    public CommitCommand(ParallelTableProvider ptp) {
        base = ptp;
    }

    private ParallelTableProvider base;

    @Override
    public void run() throws ParallelException {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            try {
                System.out.println(base.getUsing().commit());
            } catch (IOException ex) {
                throw new ParallelException(ex.getMessage());
            }
        }
    }
}
