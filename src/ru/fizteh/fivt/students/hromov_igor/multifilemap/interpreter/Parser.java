package ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.BaseTable;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.TableManager;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.commands.*;

public class Parser {

    public static void parse(final String[] args, Object object) throws Exception {
        try {
                TableManager obj = (TableManager) object;
                switch (args[0]) {
                    case "exit":
                        if (obj.currentTable != null) {
                            BaseTable curTable = obj.tables.get(obj.currentTable);
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
                        Drop.run(args, obj);
                        break;
                    case "create":
                        Create.run(args, obj);
                        break;
                    case "use":
                        Use.run(args, obj);
                        break;
                    case "show":
                        ShowTables.run(args, obj);
                        break;
                    case "put":
                        Put.run(args, obj);
                        break;
                    case "get":
                        Get.run(args, obj);
                        break;
                    case "remove":
                        Remove.run(args, obj);
                        break;
                    case "list":
                        List.run(args, obj);
                        break;
                    case "size":
                        Size.run(args, obj);
                        break;
                    case "commit":
                        Commit.run(args, obj);
                        break;
                    case "rollback":
                        Rollback.run(args, obj);
                        break;
                    default:
                        System.err.println(args[0] + ": command not found");
                }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}