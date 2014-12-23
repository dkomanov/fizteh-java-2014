package ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableException;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProvider;

import java.io.IOException;

public class DropCommand implements Command {
    public DropCommand(StoreableTableProvider stp, String givenName) {
        tableName = givenName;
        sTableProvider = stp;
    }

    private StoreableTableProvider sTableProvider;
    private String tableName;

    @Override
    public void run() throws StoreableException {
        try {
            sTableProvider.removeTable(tableName);
            System.out.println("dropped");
        } catch (IllegalStateException ex) {
            System.out.println(tableName + " not found");
        } catch (IOException ex) {
            throw new StoreableException(ex.getMessage());
        }
    }
}
