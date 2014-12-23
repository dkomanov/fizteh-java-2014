package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.server;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 08.12.14.
 */
public class StartServerCommand implements Command{
    RemoteDataBaseTableProvider provider;

    public StartServerCommand(RemoteDataBaseTableProvider provider) {
        this.provider = provider;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() >= 2) {
            throw new Exception("usage start [port]");
        }
        if (arguments.isEmpty()) {
            return "started at " + provider.startServer();
        } else {
            try {
                return "started at " + provider.startServer(Integer.parseInt(arguments.get(0)));
            } catch (NumberFormatException e) {
                throw new Exception("not started: wrong port number");
            }
        }
    }

    @Override
    public String getName() {
        return "start";
    }
}
