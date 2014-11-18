package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import java.util.List;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class Command {

    private static void checkQuantityOfArguments(final String[] args,
            final int expectation, final String commandName) throws Exception {
        if (args.length != expectation) {
            ErrorFunctions.wrongQuantityOfArguments(commandName);
        }
        if (args.length > 1) {
            for (int i = 1; i < args.length; ++i) {
                if (args[i].isEmpty()) {
                    ErrorFunctions.wrongInput(commandName);
                }
            }
        }
    }
    
    private static void tableIsNull(final Table table) throws Exception {
        if (table == null) {
            throw new Exception("no table");
        }
    }
    
    public static void commit(final String[] args, Table table) throws Exception {
        checkQuantityOfArguments(args, 1, "commit");
        tableIsNull(table);
        
        table.commit();
    }

    public static void create(final String[] args, TableProvider tableProvider) throws Exception {
        checkQuantityOfArguments(args, 2, "create");
        
        tableProvider.createTable(args[1]);
    }

    public static void drop(final String[] args, TableProvider tableProvider) throws Exception {
        checkQuantityOfArguments(args, 2, "drop");
        
        tableProvider.removeTable(args[1]);
    }

    public static void exit(final String[] args) throws Exception {
        checkQuantityOfArguments(args, 1, "exit");
        
        System.exit(ReturnCodes.SUCCESS);
    }

    public static void get(final String[] args, Table table) throws Exception {
        checkQuantityOfArguments(args, 2, "get");
        tableIsNull(table);
        
        table.get(args[1]);
    }

    public static void list(final String[] args, Table table) throws Exception {
        checkQuantityOfArguments(args, 1, "list");
        tableIsNull(table);
        
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
        checkQuantityOfArguments(args, 3, "put");
        tableIsNull(table);
        
        table.put(args[1], args[2]);
    }

    public static void remove(final String[] args, Table table) throws Exception {
        checkQuantityOfArguments(args, 2, "remove");
        tableIsNull(table);
        
        table.remove(args[1]);
    }

    public static void rollback(final String[] args, Table table) throws Exception {
        checkQuantityOfArguments(args, 1, "rollback");
        tableIsNull(table);
        
        table.rollback();
    }

    public static void size(final String[] args, Table table) throws Exception {
        checkQuantityOfArguments(args, 1, "size");
        tableIsNull(table);
        
        table.size();
    }
    
    public static void showTables(final String[] args, TableProvider table)throws Exception {
        checkQuantityOfArguments(args, 2, "show tables");
        if (!args[1].equals("tables")) {
            ErrorFunctions.wrongInput("show tables");
        }
        
        table.showTables();
    }
    
    public static void use(final String[] args, TableProvider tableProvider, Table table) throws Exception {
        checkQuantityOfArguments(args, 2, "use");
        
        table = tableProvider.getTable(args[1]);
    }
}
