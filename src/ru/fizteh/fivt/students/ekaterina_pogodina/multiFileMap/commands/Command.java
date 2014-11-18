package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public abstract class Command {
    public abstract void execute(String[] args, TableManager table) throws Exception;
    public void checkArgs(String[] args, TableManager table, int k) throws Exception {
        if (args.length > k) {
            table.manyArgs(args[1]);
        } else {
            if  (args.length < k) {
                table.missingOperand(args[1]);
            }
        }
        switch (args[0]) {
            case "drop":
                Command dropCommand = new Drop();
                dropCommand.execute(args, table);
                break;
            case "create":
                Command createCommand = new Create();
                createCommand.execute(args, table);
                break;
            case "use":
                Command command = new Use();
                command.execute(args, table);
                break;
            case "show":
                Command showTables = new ShowTables();
                showTables.execute(args, table);
                break;
            case "put":
                Command put = new Put();
                put.execute(args, table);
                break;
            case "get":
                Command get = new Get();
                get.execute(args, table);
                break;
            case "remove":
                Command remove = new Remove();
                remove.execute(args, table);
                break;
            case "list":
                Command list = new List();
                list.execute(args, table);
                break;
            case "size":
                Command size = new Size();
                size.execute(args, table);
                break;
            case "commit":
                Command commit = new Commit();
                commit.execute(args, table);
                break;
            case "rollback":
                Command rollback = new Rollback();
                rollback.execute(args, table);
                break;
            default:
                break;
        }
    }
}
