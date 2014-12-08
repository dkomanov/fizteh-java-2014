package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.RemoteCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class ListCommand extends RemoteCommand {
    private RemoteDataBaseTableProvider shell;

    public ListCommand(RemoteDataBaseTableProvider shell) {
        super(shell);
        this.shell = shell;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (shell.isGuested()) {
            return sendCommand(arguments);
        }
        if (!arguments.isEmpty()) {
            throw new Exception("usage: list");
        }
        if (shell.getOpenedTable() == null) {
            return "no table";
        }
        return String.join(",", shell.getOpenedTable().list());
    }

    @Override
    public String getName() {
        return "list";
    }
}
