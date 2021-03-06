package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.commands.Command;

/**
 * Created by user1 on 07.10.2014.
 */
public abstract class TableCommand implements Command {
    public static final boolean EXIST = true;
    public static final boolean NON_EXIST = false;
    protected DataBaseTableProvider shell;

    public TableCommand(DataBaseTableProvider shell) {
        this.shell = shell;
    }


    public DataBaseTableProvider getShell() {
        return shell;
    }
}
