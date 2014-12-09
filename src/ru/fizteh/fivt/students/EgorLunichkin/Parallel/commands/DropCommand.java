package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

import java.io.IOException;

public class DropCommand implements Command {
    public DropCommand(ParallelTableProvider ptp, String givenName) {
        tableName = givenName;
        base = ptp;
    }

    private ParallelTableProvider base;
    private String tableName;

    @Override
    public void run() throws ParallelException {
        try {
            base.removeTable(tableName);
            System.out.println("dropped");
        } catch (IllegalStateException ex) {
            System.out.println(tableName + " not found");
        } catch (IOException ex) {
            throw new ParallelException(ex.getMessage());
        }
    }
}
