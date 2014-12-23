package ru.fizteh.fivt.students.irina_karatsapova.proxy.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.InterpreterState;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;

public class DropCommand implements Command {
    public void execute(InterpreterState state, String[] args) throws Exception {
        if (!Utils.checkNoChanges(state)) {
            return;
        }
        String tableName = args[1];
        try {
            state.tableProvider.removeTable(tableName);
            state.out.println("dropped");
        } catch (IllegalStateException e) {
            state.out.println(tableName + "not exists");
        }
        state.table = null;
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
