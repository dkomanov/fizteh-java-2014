package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by user1 on 21.10.2014.
 */
public class CommitCommand implements Command {
    private RemoteDataBaseTableProvider table;

    public CommitCommand(RemoteDataBaseTableProvider table) {
        this.table = table;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (!table.isGuested()) {
            if (!arguments.isEmpty()) {
                throw new Exception("usage: commit");
            }

            if (table.getOpenedTable() == null) {
                return "no table";
            }

            try {
                return Integer.toString(table.getOpenedTable().commit());
            } catch (Exception e) {
                throw new Exception("commit error");
            }
        } else {
            return table.sendCommand(String.join(" ", arguments));
        }
//        TODO допилить штуку, которая заставлять сервера обращаться к парсеру
    }

    @Override
    public String getName() {
        return "commit";
    }
}
