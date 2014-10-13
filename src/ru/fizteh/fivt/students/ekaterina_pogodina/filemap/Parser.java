package ru.fizteh.fivt.students.ekaterina_pogodina.filemap;

public class Parser {
    private Parser() {
        //
    }

    public static void parse(final String[] args, DataBase dBase) throws Exception {
        try {
            switch (args[0]) {
                case "exit":
                    dBase.close();
                    System.exit(0);
                    break;
                case "put":
                    dBase.put(args);
                    break;
                case "get":
                    dBase.get(args);
                    break;
                case "remove":
                    dBase.remove(args);
                    break;
                case "list":
                    dBase.list(args);
                    break;
                default:
                    System.err.println(args[0] + ": no such command");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}

