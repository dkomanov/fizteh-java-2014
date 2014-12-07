package ru.fizteh.fivt.students.irina_karatsapova.proxy.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.InterpreterState;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;

public class CommitCommand implements Command {
    public void execute(InterpreterState state, String[] args) throws Exception {
        if (!Utils.checkTableChosen(state)) {
            return;
        }
        int changesNumber = state.table.commit();
        state.out.println(changesNumber);
    }

    public String name() {
        return "commit";
    }

    public int minArgs() {
        return 1;
    }

    public int maxArgs() {
        return 1;
    }
}
