package ru.fizteh.fivt.students.irina_karatsapova.parallel.commands;

import ru.fizteh.fivt.students.irina_karatsapova.parallel.InterpreterState;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.utils.Utils;

public class GetCommand implements Command {
    public void execute(InterpreterState state, String[] args) {
        if (!Utils.checkTableChosen(state)) {
            return;
        }
        String key = args[1];
        Storeable tableRawValue = state.table.get(key);
        if (tableRawValue != null) {
            state.out.println("found");
            String stringValue = state.tableProvider.serialize(state.table, tableRawValue);
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
