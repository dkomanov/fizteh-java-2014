package ru.fizteh.fivt.students.irina_karatsapova.storeable.commands;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.Utils;

import java.text.ParseException;

public class PutCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) throws ParseException {
        if (!Utils.checkTableChosen(tableProvider)) {
            return;
        }
        String key = args[1];
        String stringValue = "";
        for (int argsIndex = 2; argsIndex < args.length; ++argsIndex) {
            stringValue += (args[argsIndex]);
        }
        Table table = tableProvider.currentTable();
        Storeable tableRawValue = tableProvider.deserialize(table, stringValue);
        Storeable tableRawPrevValue = table.put(key, tableRawValue);
        if (tableRawPrevValue == null) {
            System.out.println("new");
        } else {
            System.out.println("owerwrite");
            String stringPrevValue = tableProvider.serialize(table, tableRawPrevValue);
            System.out.println(stringPrevValue);
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
