package storeable.util;

import java.util.Arrays;
import java.util.List;

import storeable.structured.*;

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
        String[] strings;
        switch (cmd[0]) {
            case "create":
                if (cmd.length < 3) {
                    throw new Exception("Wrong number of arguments");
                }
                if (cmd[2].charAt(0) != '(' || cmd[cmd.length - 1].charAt(
                                    cmd[cmd.length - 1].length() - 1) != ')') {
                    throw new Exception("Wrong types description");
                }
                cmd[2] = cmd[2].replaceAll("\\(", "");
                cmd[cmd.length - 1] = cmd[cmd.length - 1].replaceAll("\\)", "");
                strings = Arrays.copyOfRange(cmd, 2, cmd.length);

                table = tableProvider.createTable(cmd[1], TypesTransformer.toListTypes(strings));
                if (table == null) {
                    System.out.println("tablename exists");
                } else {
                    System.out.println("created");
                }
                break;
            case "drop":
                if (cmd.length != 2) {
                    throw new Exception("Wrong number of arguments");
                }
                tableProvider.removeTable(cmd[1]);
                System.out.println("dropped");
                break;
            case "use":
                if (cmd.length != 2) {
                    throw new Exception("Wrong number of arguments");
                }
                table = tableProvider.getTable(cmd[1]);
                if (table == null) {
                    System.out.println("table not exists");
                } else {
                    System.out.println("using " + table.getName());
                }
                break;
            case "show":
                if (cmd.length != 2 || !cmd[1].equals("tables")) {
                    throw new Exception("Wrong number of arguments");
                }
                List<String> tables = tableProvider.getTableNames();
                for (int i = 0; i < tables.size(); i++) {
                    System.out.println(tables.get(i));
                }
                break;
            case "put":
                if (cmd.length < 3) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                StringBuilder builder = new StringBuilder();
                for (int i = 2; i < cmd.length; i++) {
                    builder.append(cmd[i]);
                }

                Storeable old = table.put(cmd[1], tableProvider.deserialize(table,
                                                                builder.toString()));
                if (old == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(tableProvider.serialize(table, old));
                }
                break;
            case "get":
                if (cmd.length != 2) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                Storeable value = table.get(cmd[1]);
                if (value == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found\n" + tableProvider.serialize(table, value));
                }
                break;
            case "remove":
                if (cmd.length != 2) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                value = table.remove(cmd[1]);
                if (value == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("removed");
                }
                break;
            case "list":
                if (cmd.length != 1) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                for (String el : table.list()) {
                    System.out.println(el);
                }
                break;
            case "size":
                if (cmd.length != 1) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                System.out.println(table.size());
                break;
            case "commit":
                if (cmd.length != 1) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                System.out.println(table.commit());
                break;
            case "rollback":
                if (cmd.length != 1) {
                    throw new IndexOutOfBoundsException("Wrong number of arguments");
                }
                System.out.println(table.rollback());
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
