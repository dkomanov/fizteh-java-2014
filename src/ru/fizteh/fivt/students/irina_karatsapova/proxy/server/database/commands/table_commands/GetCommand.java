package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.table_commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.InterpreterStateDatabase;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.DatabaseCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.utils.Utils;

public class GetCommand implements DatabaseCommand {
    public void execute(InterpreterStateDatabase state, String[] args) {
        if (!Utils.checkTableChosen(state)) {
            return;
        }
        String key = args[1];
        Storeable tableRawValue = state.getTable().get(key);
        if (tableRawValue != null) {
            state.out.println("found");
            String stringValue = state.getTableProvider().serialize(state.getTable(), tableRawValue);
            state.out.println(stringValue);
        } else {
            state.out.println("not found");
        }
    }

    public String name() {
        return "get";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
