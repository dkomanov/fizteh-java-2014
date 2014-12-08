package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.client;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 09.12.14.
 */
public class DisconnectCommand implements Command {
    public DisconnectCommand(RemoteDataBaseTableProvider provider) {
        this.provider = provider;
    }

    RemoteDataBaseTableProvider provider;

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (!arguments.isEmpty()) {
            throw new Exception("usage: disconnect");
        }
        if (!provider.isGuested()) {
            throw new Exception("Provider in stable mode");
        }
        return provider.disconnect();
    }

    @Override
    public String getName() {
        return "disconnect";
    }
}
