package ru.fizteh.fivt.students.irina_karatsapova.junit.commands;

import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.Utils;

public class RollbackCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) throws Exception {
        if (!Utils.checkTableChosen(tableProvider)) {
            return;
        }
        int changesNumber = tableProvider.currentTable().rollback();
        System.out.println(changesNumber);
    }

    public String name() {
        return "rollback";
    }

    public int minArgs() {
        return 1;
    }

    public int maxArgs() {
        return 1;
    }
}
