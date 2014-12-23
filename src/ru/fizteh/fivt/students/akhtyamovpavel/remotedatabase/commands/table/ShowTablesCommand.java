package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by user1 on 17.10.2014.
 */
public class ShowTablesCommand extends TableCommand implements Command {
    public ShowTablesCommand(RemoteDataBaseTableProvider shell) {
        super(shell);
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (shell.isGuested()) {
            return sendCommand(arguments);
        }
        if (arguments.size() != 1) {
            throw new Exception("usage: show tables");
        }
        if (!"tables".equals(arguments.get(0))) {
            throw new Exception("usage: show tables");
        }
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : shell.getTableList().entrySet()) {
            result.append(entry.getKey() + " " + entry.getValue() + "; ");
        }
        return result.toString();
    }

    @Override
    public String getName() {
        return "show";
    }

    String sendCommand(ArrayList<String> arguments) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append(getName() + " ");
        result.append(String.join(" ", arguments));
        return shell.sendCommand(result.toString());
    }
}
