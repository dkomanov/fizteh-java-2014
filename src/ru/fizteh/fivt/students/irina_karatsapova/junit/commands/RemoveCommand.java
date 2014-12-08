package ru.fizteh.fivt.students.irina_karatsapova.junit.commands;

import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.Utils;

public class RemoveCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) {
        if (!Utils.checkTableChosen(tableProvider)) {
            return;
        }
        String key = args[1];
        String value = tableProvider.currentTable().remove(key);
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
