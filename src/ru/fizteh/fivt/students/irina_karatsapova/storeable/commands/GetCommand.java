package ru.fizteh.fivt.students.irina_karatsapova.storeable.commands;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.Utils;

public class GetCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) {
        if (!Utils.checkTableChosen(tableProvider)) {
            return;
        }
        String key = args[1];
        Table table = tableProvider.currentTable();
        Storeable tableRawValue = table.get(key);
        if (tableRawValue != null) {
            System.out.println("found");
            String stringValue = tableProvider.serialize(table, tableRawValue);
            System.out.println(stringValue);
        } else {
            System.out.println("not found");
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
