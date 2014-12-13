package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.table_commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.InterpreterStateDatabase;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.DatabaseCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.utils.Utils;

public class RemoveCommand implements DatabaseCommand {
    public void execute(InterpreterStateDatabase state, String[] args) {
        if (!Utils.checkTableChosen(state)) {
            return;
        }
        String key = args[1];
        Storeable value = state.getTable().remove(key);
        if (value == null) {
            state.out.println("not found");
        } else {
            state.out.println("removed");
        }
    }

    public String name() {
        return "remove";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
