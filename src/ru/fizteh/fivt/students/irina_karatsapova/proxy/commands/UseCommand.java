package ru.fizteh.fivt.students.irina_karatsapova.proxy.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.InterpreterState;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;

public class UseCommand implements Command {
    public void execute(InterpreterState state, String[] args) throws Exception {
        if (!Utils.checkNoChanges(state)) {
            return;
        }
        String tableName = args[1];
        state.table = null;
        state.table = state.tableProvider.getTable(tableName);
        if (state.table == null) {
            state.out.println(tableName + " not exists");
        } else {
            state.out.println("using " + tableName);
        }
    }

    public String name() {
        return "use";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
