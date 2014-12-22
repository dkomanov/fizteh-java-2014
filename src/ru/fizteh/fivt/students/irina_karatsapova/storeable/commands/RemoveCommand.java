package ru.fizteh.fivt.students.irina_karatsapova.storeable.commands;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.Utils;

public class RemoveCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) {
        if (!Utils.checkTableChosen(tableProvider)) {
            return;
        }
        String key = args[1];
        Storeable value = tableProvider.currentTable().remove(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
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
