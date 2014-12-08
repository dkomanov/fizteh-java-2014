package ru.fizteh.fivt.students.SibgatullinDamir.junit;

import java.nio.file.Files;

/**
 * Created by Lenovo on 20.10.2014.
 */
public class UseCommand implements CommandsForTables {
    public void execute(String[] args, MultiFileHashMap multiFileHashMap) throws MyException {

        if (args.length == 1) {
            throw new MyException("use: not enough arguments");
        }
        if (args.length > 2) {
            throw new MyException("use: too many arguments");
        }

        if (!Files.exists(MultiFileHashMapMain.rootPath.resolve(args[1]))) {
            throw new MyException(args[1] + " not exists");
        }

        if (MultiFileHashMapMain.currentTable != null && !MultiFileHashMapMain.currentTable.changedKeys.isEmpty()) {
            throw new MyException(MultiFileHashMapMain.currentTable.changedKeys.size() + " unsaved changes");
        }

        MultiFileHashMapMain.currentTable = multiFileHashMap.get(args[1]);
        MultiFileHashMapMain.committedTable.clear();
        MultiFileHashMapMain.committedTable.putAll(MultiFileHashMapMain.currentTable);
        System.out.println("using " + args[1]);

    }

    public String getName() {
        return "use";
    }
}
