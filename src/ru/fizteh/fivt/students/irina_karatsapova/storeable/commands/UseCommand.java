package ru.fizteh.fivt.students.irina_karatsapova.storeable.commands;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.Utils;

public class UseCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) throws Exception {
        if (!Utils.checkNoChanges(tableProvider)) {
            return;
        }
        String tableName = args[1];
        Table usingTable = tableProvider.useTable(tableName);
        if (usingTable == null) {
            System.out.println(tableName + " not exists");
        } else {
            System.out.println("using " + tableName);
        }
    }

    public String name() {
        return "use";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
