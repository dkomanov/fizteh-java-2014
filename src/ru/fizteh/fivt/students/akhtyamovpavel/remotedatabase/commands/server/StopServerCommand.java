package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.server;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 08.12.14.
 */
public class StopServerCommand implements Command {
    public StopServerCommand(RemoteDataBaseTableProvider provider) {
        this.provider = provider;
    }

    RemoteDataBaseTableProvider provider;


    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (!arguments.isEmpty()) {
            throw new Exception("usage: stop");
        }
        if (!provider.isServerStarted()) {
            return "not started";
        }
        return "stopped at " + provider.stopServer();
    }

    @Override
    public String getName() {
        return "stop";
    }
}
