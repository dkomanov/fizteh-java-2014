package ru.fizteh.fivt.students.SibgatullinDamir.storeable;

import java.nio.file.Files;

/**
 * Created by Lenovo on 20.10.2014.
 */
public class UseCommand implements CommandsForTables {
    public void execute(String[] args, MyTableProvider provider) throws MyException {

        if (args.length == 1) {
            throw new MyException("use: not enough arguments");
        }
        if (args.length > 2) {
            throw new MyException("use: too many arguments");
        }

        if (!Files.exists(StoreableMain.rootPath.resolve(args[1]))) {
            throw new MyException(args[1] + " not exists");
        }

        if (provider.usingTable != null && !provider.usingTable.changedKeys.isEmpty()) {
            throw new MyException(provider.usingTable.changedKeys.size() + " unsaved changes");
        }

        provider.usingTable = provider.tables.get(args[1]);
        provider.usingTable.committedTable.clear();
        provider.usingTable.committedTable.putAll(provider.usingTable.currentTable);
        System.out.println("using " + args[1]);
    }

    public String getName() {
        return "use";
    }
}
