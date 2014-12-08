package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.table_commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.InterpreterStateDatabase;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.DatabaseCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.utils.Utils;

public class SizeCommand implements DatabaseCommand {
    public void execute(InterpreterStateDatabase state, String[] args) throws Exception {
        if (!Utils.checkTableChosen(state)) {
            return;
        }
        int size = state.table.size();
        state.out.println(size);
    }

    public String name() {
        return "size";
    }

    public int minArgs() {
        return 1;
    }

    public int maxArgs() {
        return 1;
    }
}
