package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.database_commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.InterpreterStateDatabase;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.DatabaseCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.utils.Utils;

public class DropCommand implements DatabaseCommand {
    public void execute(InterpreterStateDatabase state, String[] args) throws Exception {
        if (!Utils.checkNoChanges(state)) {
            return;
        }
        String tableName = args[1];
        try {
            state.getTableProvider().removeTable(tableName);
            state.out.println("dropped");
        } catch (IllegalStateException e) {
            state.out.println(tableName + "not exists");
        }
        state.stopUsingTable();
    }

    public String name() {
        return "drop";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
