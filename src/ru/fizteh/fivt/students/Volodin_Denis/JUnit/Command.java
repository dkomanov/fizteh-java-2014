package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class Command {

    public static void commit(final String[] args, Table table) throws Exception {
        if (args.length != 1) {
            ErrorFunctions.wrongQuantity("commit");
        }
        
        table.commit();
    }

    public static void create(final String[] args, TableProvider tables) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantity("create");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("create");
        }
        
        tables.createTable(args[1]);
    }

    public static void drop(final String[] args, TableProvider tables) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantity("drop");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("drop");
        }
        tables.removeTable(args[1]);
    }

    public static void exit(final String[] args) throws Exception {
        if (args.length != 1) {
            ErrorFunctions.wrongQuantity("exit");
        }
        System.exit(ReturnCodes.SUCCESS);
    }

    public static void get(final String[] args, Table table) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantity("get");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("get");
        }
        if (table == null) {
            System.out.println("no table");
            return;
        }
        
        table.get(args[1]);
    }

    public static void list(final String[] args, Table table) throws Exception {
        if (args.length != 1) {
            ErrorFunctions.wrongQuantity("list");
        }
        if (table == null) {
            System.out.println("no table");
            return;
        }
        
        table.list();
    }

    public static void put(final String[] args, Table table) throws Exception {
        if (args.length != 3) {
            ErrorFunctions.wrongQuantity("put");
        }
        if ((args[1].isEmpty()) || (args[2].isEmpty())) {
            ErrorFunctions.wrongInput("put");
        }
        if (table == null) {
            System.out.println("no table");
            return;
        }
        
        table.put(args[1], args[2]);
    }

    public static void remove(final String[] args, Table table) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantity("remove");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("remove");
        }
        if (table == null) {
            System.out.println("no table");
            return;
        }
        table.remove(args[1]);
    }

    public static void rollback(final String[] args, Table table) throws Exception {
        if (args.length != 1) {
            ErrorFunctions.wrongQuantity("rollback");
        }
        
        table.rollback();
    }

    public static void size(final String[] args, Table table) throws Exception {
        if (args.length != 1) {
            ErrorFunctions.wrongInput("size");
        }
        
        table.size();
    }
    
    public static void showTables(final String[] args, TableProvider table)throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongInput("show tables");
        }
        if (!args[1].equals("tables")) {
            ErrorFunctions.wrongInput("show tables");
        }
        
        table.showTables();
    }
    
    public static void use(final String[] args, TableProvider tables, Table table) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantity("use");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("use");
        }
        
        table = tables.getTable(args[1]);
    }
}
