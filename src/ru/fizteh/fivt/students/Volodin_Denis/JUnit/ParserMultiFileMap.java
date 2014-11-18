package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class ParserMultiFileMap {
    ParserMultiFileMap() {
    }
    
    public static void parser(final String[] args, TableProvider tableProvider, Table table)
            throws Exception {
        switch (args[0]) {
            case "commit":
                Command.commit(args, table);
                break;
            case "create":
                Command.create(args, tableProvider);
                break;
            case "drop":
                Command.drop(args, tableProvider);
                break;
            case "exit":
                Command.exit(args);
                break;
            case "get":
                Command.get(args, table);
                break;
            case "list":
                Command.list(args, table);
                break;
            case "put":
                Command.put(args, table);
                break;
            case "remove":
                Command.remove(args, table);
                break;
            case "rollback":
                Command.rollback(args, table);
                break;
            case "size":
                Command.size(args, table);
                break;
            case "show":
                Command.showTables(args, tableProvider);
                break;
            case "use":
                Command.use(args, tableProvider, table);
                break;
            default:
                System.err.println("Command does not exist: [" + args[0] + "]");
        }
    }
}
