package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.client;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 09.12.14.
 */
public class DestinationCommand implements Command {
    public DestinationCommand(RemoteDataBaseTableProvider provider) {
        this.provider = provider;
    }

    RemoteDataBaseTableProvider provider;

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (!arguments.isEmpty()) {
            throw new Exception("usage: whereiam");
        }

        return provider.getRoot();
    }

    @Override
    public String getName() {
        return "whereiam";
    }
}
