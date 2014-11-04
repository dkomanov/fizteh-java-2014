package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by user1 on 07.10.2014.
 */
public abstract class TableCommand implements Command {
    protected DataBaseTableProvider shell;
    public static final boolean EXIST = true;
    public static final boolean NON_EXIST = false;

    public TableCommand(DataBaseTableProvider shell) {
        this.shell = shell;
    }



    public DataBaseTableProvider getShell() {
        return shell;
    }
}
