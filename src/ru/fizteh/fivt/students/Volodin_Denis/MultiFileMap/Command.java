package ru.fizteh.fivt.students.Volodin_Denis.MultiFileMap;

import java.util.HashMap;

public class Command {
    
    public static void create(String[] args, Table table, HashMap<String, Integer> dbInformation) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantity("create");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("create");
        }
        
        table.create(args[1], dbInformation);
    }

    public static void drop(final String[] args, Table table, HashMap<String, Integer> dbInformation) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantity("drop");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("drop");
        }
        table.drop(args[1], dbInformation);
    }

    public static void use(final String[] args, Table table, HashMap<String, Integer> dbInformation) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantity("use");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("use");
        }
        
        table.use(args[1], dbInformation);
    }
    
    public static void showTables(final String[] args, Table table, HashMap<String, Integer> dbInformation)
            throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongInput("show tables");
        }
        if (!args[1].equals("tables")) {
            ErrorFunctions.wrongInput("show tables");
        }
        
        table.showTables(args[1], dbInformation);
    }
    
    public static void exit(final String[] args, Table table) throws Exception {
        if (args.length != 1) {
            ErrorFunctions.wrongQuantity("exit");
        }
        table.exit();
    }
    
    public static void put(final String[] args, Table table) throws Exception {
        if (args.length != 3) {
            ErrorFunctions.wrongQuantity("put");
        }
        if ((args[1].isEmpty()) || (args[2].isEmpty())) {
            ErrorFunctions.wrongInput("put");
        }
        if (table.getTable() == null) {
            System.out.println("no table");
            return;
        }
        
        table.put(args[1], args[2]);
    }
    
    public static void get(final String[] args, Table table) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantity("get");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("get");
        }
        if (table.getTable() == null) {
            System.out.println("no table");
            return;
        }
        
        table.get(args[1]);
    }
    
    public static void remove(final String[] args, Table table) throws Exception {
        if (args.length != 2) {
            ErrorFunctions.wrongQuantity("remove");
        }
        if (args[1].isEmpty()) {
            ErrorFunctions.wrongInput("remove");
        }
        if (table.getTable() == null) {
            System.out.println("no table");
            return;
        }
        table.remove(args[1]);
    }
    
    public static void list(final String[] args, Table table) throws Exception {
        if (args.length != 1) {
            ErrorFunctions.wrongQuantity("list");
        }
        if (table.getTable() == null) {
            System.out.println("no table");
            return;
        }
        
        table.list();
    }
}
