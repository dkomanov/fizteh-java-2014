package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.table_commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.InterpreterStateDatabase;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.DatabaseCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.utils.Utils;

import java.util.List;

public class ListCommand implements DatabaseCommand {
    public void execute(InterpreterStateDatabase state, String[] args) throws Exception {
        if (!Utils.checkTableChosen(state)) {
            return;
        }
        List<String> keys = state.table.list();
        String allKeys = "";
        for (String key: keys) {
            if (allKeys.length() > 0) {
                allKeys += ", ";
            }
            allKeys += key;
        }
        state.out.println(allKeys);
    }

    public String name() {
        return "list";
    }

    public int minArgs() {
        return 1;
    }

    public int maxArgs() {
        return 1;
    }
}
