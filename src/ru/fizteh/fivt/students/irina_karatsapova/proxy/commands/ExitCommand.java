package ru.fizteh.fivt.students.irina_karatsapova.proxy.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.InterpreterState;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.exceptions.ThreadInterruptException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;

public class ExitCommand implements Command {
    public void execute(InterpreterState state, String[] args) throws Exception {
        if (!Utils.checkNoChanges(state)) {
            return;
        }
        throw new ThreadInterruptException();
    }
    
    public String name() {
        return "exit";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 1;
    }
}
