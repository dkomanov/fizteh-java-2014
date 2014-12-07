package ru.fizteh.fivt.students.irina_karatsapova.proxy.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.InterpreterState;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;

import java.text.ParseException;

public class PutCommand implements Command {
    public void execute(InterpreterState state, String[] args) throws ParseException {
        if (!Utils.checkTableChosen(state)) {
            return;
        }
        String key = args[1];
        String stringValue = "";
        for (int argsIndex = 2; argsIndex < args.length; ++argsIndex) {
            stringValue += (args[argsIndex]);
        }
        Storeable tableRawValue = state.tableProvider.deserialize(state.table, stringValue);
        Storeable tableRawPrevValue = state.table.put(key, tableRawValue);
        if (tableRawPrevValue == null) {
            state.out.println("new");
        } else {
            state.out.println("owerwrite");
            String stringPrevValue = state.tableProvider.serialize(state.table, tableRawPrevValue);
            state.out.println(stringPrevValue);
        }
    }
    
    public String name() {
        return "put";
    }
    
    public int minArgs() {
        return 3;
    }
    
    public int maxArgs() {
        return Integer.MAX_VALUE;
    }
}
