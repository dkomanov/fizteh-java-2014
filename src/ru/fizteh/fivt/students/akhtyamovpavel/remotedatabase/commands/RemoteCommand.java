package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 08.12.14.
 */
public abstract class RemoteCommand implements Command {
    public RemoteCommand(RemoteDataBaseTableProvider provider) {
        this.provider = provider;
    }

    RemoteDataBaseTableProvider provider;

    protected String sendCommand(ArrayList<String> arguments) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append(getName() + " ");
        result.append(String.join(" ", arguments));
        return provider.sendCommand(result.toString());
    }
}
