package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.server;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akhtyamovpavel on 08.12.14.
 */
public class ListUsersCommand implements Command{
    public ListUsersCommand(RemoteDataBaseTableProvider provider) {
        this.provider = provider;
    }

    RemoteDataBaseTableProvider provider;

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (!arguments.isEmpty()) {
            throw new Exception("usage: listusers");
        }

        List<String> listOfUsers = provider.getUsersList();
        return String.join("\n", listOfUsers);
    }

    @Override
    public String getName() {
        return "listusers";
    }
}
