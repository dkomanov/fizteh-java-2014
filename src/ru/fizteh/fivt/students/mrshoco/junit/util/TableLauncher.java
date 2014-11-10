package util;

import strings.*;

public class TableLauncher {
    TableProvider tableProvider;

    Table table;

    public String getCurrentName() {
        if (table == null) {
            return "";
        } else {
            return table.getName();
        }
    }

    public TableLauncher(TableProvider tableProvider) {
        this.tableProvider = tableProvider;
    }

    public void run(String[] cmd) throws Exception {
        switch (cmd[0]) {
            case "create":
                if (cmd.length != 2) {
                    throw new Exception("Wrong number of arguments");
                }
                table = tableProvider.createTable(cmd[1]);
                break;
            case "drop":
                if (cmd.length != 2) {
                    throw new Exception("Wrong number of arguments");
                }
                tableProvider.removeTable(cmd[1]);
                break;
            case "use":
                if (cmd.length != 2) {
                    throw new Exception("Wrong number of arguments");
                }
                table = tableProvider.getTable(cmd[1]);
                break;
            case "put":
                if (cmd.length != 3) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                table.put(cmd[1], cmd[2]);
                break;
            case "get":
                if (cmd.length != 2) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                table.get(cmd[1]);
                break;
            case "remove":
                if (cmd.length != 2) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                table.remove(cmd[1]);
                break;
            case "list":
                if (cmd.length != 1) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                table.list();
                break;
            case "size":
                if (cmd.length != 1) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                table.size();
                break;
            case "commit":
                if (cmd.length != 1) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                table.commit();
                break;
            case "rollback":
                if (cmd.length != 1) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                table.rollback();
                break;
            case "exit":
                if (cmd.length != 1) {
                    throw new Exception("Wrong number of arguments");
                }
                throw new ExitException("Exit");
            default:
                throw new Exception("Bad command");
        }
    }
}
