package ru.fizteh.fivt.students.irina_karatsapova.proxy.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.InterpreterState;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;

import java.util.List;

public class ListCommand implements Command {
    public void execute(InterpreterState state, String[] args) throws Exception {
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
