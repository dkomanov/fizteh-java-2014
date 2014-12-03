package ru.fizteh.fivt.students.irina_karatsapova.parallel.commands;

import ru.fizteh.fivt.students.irina_karatsapova.parallel.InterpreterState;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.exceptions.ThreadInterruptException;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.utils.Utils;

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
