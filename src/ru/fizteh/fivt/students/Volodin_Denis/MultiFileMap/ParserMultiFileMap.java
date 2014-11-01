package ru.fizteh.fivt.students.Volodin_Denis.MultiFileMap;

import java.util.HashMap;

public class ParserMultiFileMap {
    ParserMultiFileMap() {
    }
    
    public static void parser(final String[] args, Table table, HashMap<String, Integer> dbInformation)
            throws Exception {
        switch (args[0]) {
            case "create":
                Command.create(args, table, dbInformation);
                break;
            case "drop":
                Command.drop(args, table, dbInformation);
                break;
            case "use":
                Command.use(args, table, dbInformation);
                break;
            case "show":
                Command.showTables(args, table, dbInformation);
                break;
            case "exit":
                Command.exit(args, table);
                break;               
            case "put":
                Command.put(args, table);
                break;
            case "get":
                Command.get(args, table);
                break;
            case "remove":
                Command.remove(args, table);
                break;
            case "list":
                Command.list(args, table);
                break;
            default:
                System.err.println("Command does not exist: [" + args[0] + "]");
        }
    }
}
