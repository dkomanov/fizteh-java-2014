package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import java.util.List;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class Command {

    public static void commit(final String[] args, Table table) throws Exception {
        if (args.length != 1) {
            ErrorFunctions.wrongQuantityOfArguments("commit");
        }
        
        table.commit();
    }

    public static void create(final String[] args, TableProvider tableProvider) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantityOfArguments("create");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("create");
        }
        
        tableProvider.createTable(args[1]);
    }

    public static void drop(final String[] args, TableProvider tableProvider) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantityOfArguments("drop");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("drop");
        }
        tableProvider.removeTable(args[1]);
    }

    public static void exit(final String[] args) throws Exception {
        if (args.length != 1) {
            ErrorFunctions.wrongQuantityOfArguments("exit");
        }
        System.exit(ReturnCodes.SUCCESS);
    }

    public static void get(final String[] args, Table table) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantityOfArguments("get");
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
            ErrorFunctions.wrongQuantityOfArguments("list");
        }
        if (table == null) {
            System.out.println("no table");
            return;
        }
        
        List<String> list = table.list();
        int i = list.size();
        for (String key : list) {
            System.out.print(key);
            --i;
            if (i > 0) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    public static void put(final String[] args, Table table) throws Exception {
        if (args.length != 3) {
            ErrorFunctions.wrongQuantityOfArguments("put");
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
            ErrorFunctions.wrongQuantityOfArguments("remove");
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
            ErrorFunctions.wrongQuantityOfArguments("rollback");
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
            ErrorFunctions.wrongInput("show tableProvider");
        }
        if (!args[1].equals("tableProvider")) {
            ErrorFunctions.wrongInput("show tableProvider");
        }
        
        table.showTables();
    }
    
    public static void use(final String[] args, TableProvider tableProvider, Table table) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantityOfArguments("use");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("use");
        }
        
        table = tableProvider.getTable(args[1]);
    }
}
