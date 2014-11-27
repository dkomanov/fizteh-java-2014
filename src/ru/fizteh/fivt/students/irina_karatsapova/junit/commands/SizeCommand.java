package ru.fizteh.fivt.students.irina_karatsapova.junit.commands;

import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.TableException;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.Utils;

public class SizeCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) throws Exception {
        if (!Utils.checkTableChosen(tableProvider)) {
            return;
        }
        int size = tableProvider.currentTable().size();
        System.out.println(size);
    }

    public String name() {
        return "size";
    }

    public int minArgs() {
        return 1;
    }

    public int maxArgs() {
        return 1;
    }
}
