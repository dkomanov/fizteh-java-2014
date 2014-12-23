package ru.fizteh.fivt.students.irina_karatsapova.proxy.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.InterpreterState;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;

public class RemoveCommand implements Command {
    public void execute(InterpreterState state, String[] args) {
        if (!Utils.checkTableChosen(state)) {
            return;
        }
        String key = args[1];
        Storeable value = state.table.remove(key);
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
