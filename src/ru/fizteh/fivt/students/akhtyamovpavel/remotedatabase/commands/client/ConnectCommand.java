package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.client;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 08.12.14.
 */
public class ConnectCommand implements Command {
    public ConnectCommand(RemoteDataBaseTableProvider provider) {
        this.provider = provider;
    }

    RemoteDataBaseTableProvider provider;

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 2) {
            throw new Exception("usage: connect host port");
        }
        try {
            return provider.connect(arguments.get(0), Integer.parseInt(arguments.get(1)));
        } catch (NumberFormatException e) {
            throw new Exception("wrong port number");
        }
    }

    @Override
    public String getName() {
        return "connect";
    }
}
