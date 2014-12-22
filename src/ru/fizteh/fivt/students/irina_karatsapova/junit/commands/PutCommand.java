package ru.fizteh.fivt.students.irina_karatsapova.junit.commands;

import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.Utils;

public class PutCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) {
        if (!Utils.checkTableChosen(tableProvider)) {
            return;
        }
        String key = args[1];
        String value = args[2];
        String prevValue = tableProvider.currentTable().put(key, value);
        if (prevValue == null) {
            System.out.println("new");
        } else {
            System.out.println("owerwrite");
            System.out.println(prevValue);
        }
    }
    
    public String name() {
        return "put";
    }
    
    public int minArgs() {
        return 3;
    }
    
    public int maxArgs() {
        return 3;
    }
}
