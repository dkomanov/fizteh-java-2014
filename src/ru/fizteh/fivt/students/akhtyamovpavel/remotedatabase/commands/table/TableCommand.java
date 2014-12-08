package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

/**
 * Created by user1 on 07.10.2014.
 */
public abstract class TableCommand implements Command {
    public static final boolean EXIST = true;
    public static final boolean NON_EXIST = false;
    protected RemoteDataBaseTableProvider shell;

    public TableCommand(RemoteDataBaseTableProvider shell) {
        this.shell = shell;
    }


    public RemoteDataBaseTableProvider getShell() {
        return shell;
    }
}
