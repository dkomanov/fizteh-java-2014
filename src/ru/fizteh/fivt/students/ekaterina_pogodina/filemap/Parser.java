package ru.fizteh.fivt.students.ekaterina_pogodina.filemap;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.Table;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands.*;

public class Parser {
    private Parser() {
        //
    }

    public static void parse(final String[] args, Object object) throws Exception {
        try {
            if (object.getClass() == DataBase.class) {
                DataBase obj = (DataBase) object;
                switch (args[0]) {
                    case "exit":
                        System.exit(0);
                        break;
                    case "put":
                        obj.put(args);
                        break;
                    case "get":
                        obj.get(args);
                        break;
                    case "remove":
                        obj.remove(args);
                        break;
                    case "list":
                        obj.list(args);
                        break;
                    default:
                        System.err.println(args[0] + ": no such command");
                }
            } else {
                if (object.getClass() == TableManager.class) {
                    TableManager obj = (TableManager) object;
                    switch (args[0]) {
                        case "exit":
                            if (obj.currentTable != null) {
                                Table curTable = obj.tables.get(obj.currentTable);
                                for (int i = 0; i < 16; i++) {
                                    for (int j = 0; j < 16; j++) {
                                        if (curTable.tableDateBase[i][j] != null) {
                                            curTable.tableDateBase[i][j].close();
                                        }
                                    }
                                }
                            }
                            System.exit(0);
                            break;
                        case "drop":
                            Command dropCommand = new Drop();
                            dropCommand.execute(args, obj);
                            break;
                        case "create":
                            Command createCommand = new Create();
                            createCommand.execute(args, obj);
                            break;
                        case "use":
                            Command command = new Use();
                            command.execute(args, obj);
                            break;
                        case "show":
                            if (!args[1].equals("tables")) {
                                System.err.println(args[0] + ": no such command");
                            }
                            Command showTables = new ShowTables();
                            showTables.execute(args, obj);
                            break;
                        case "put":
                            Command put = new Put();
                            put.execute(args, obj);
                            break;
                        case "get":
                            Command get = new Get();
                            get.execute(args, obj);
                            break;
                        case "remove":
                            Command remove = new Remove();
                            remove.execute(args, obj);
                            break;
                        case "list":
                            Command list = new List();
                            list.execute(args, obj);
                            break;
                        default:
                            System.err.println(args[0] + ": no such command");
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}

