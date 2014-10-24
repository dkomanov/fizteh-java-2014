package ru.fizteh.fivt.students.kolmakov_sergey.multi_file_map;

import java.util.Set;
import java.io.IOException;

// Interpreter and Commands aren't separated, because I can't find any reason to use Interpreter anywhere else.
// Without reusing of the code separating seems unnecessary.

public final class CommandInterpreter {
    private static TableManager manager;

    protected static void setManager(TableManager manager) {
        CommandInterpreter.manager = manager;
    }

    private static void checkArguments(int from, int value, int to, String commandName)
            throws  WrongNumberOfArgumentsException {
        if (from > value || value > to) {
            throw new  WrongNumberOfArgumentsException(commandName + ": Incorrect number of arguments");
        }
    }

    protected static void showTables(String[] command) throws WrongNumberOfArgumentsException {
        checkArguments(2, command.length, 2, "show tables");
        System.out.println("table_name row_count");
        Set<String> names = manager.getNames();
        for (String currentName : names) {
            System.out.println(currentName + " " + manager.getTableSize(currentName));
        }
    }

    protected static void list(String[] command) throws IOException {
        checkArguments(1, command.length, 1, "list");
        Table currentTable = manager.getCurrentTable();
        if (currentTable != null) {
            System.out.println(String.join(", ", currentTable.list()));
        } else {
            System.out.println("no table");
        }
    }

    protected static void create(String[] command) throws WrongNumberOfArgumentsException {
        checkArguments(2, command.length, 2, "create");
        try {
            if (manager.createTable(command[1]) != null) {
                System.out.println("created");
            } else {
                System.out.println(command[1] + " exists");
            }
        } catch (DatabaseExitException e){
            if (e.getMessage().isEmpty()) {
                System.out.println("Can't create table");
            } else {
                System.out.println(e.getMessage());
            }
        }
    }

    protected static void useTable(String[] command) throws IOException {
        checkArguments(2, command.length, 2, "use");
        if (manager.useTable(command[1])) {
            System.out.println("using " + command[1]);
        } else {
            System.out.println(command[1] + " not exists");
        }
    }

    protected static void put(String[] command) throws IOException {
        checkArguments(3, command.length, 3, "put");
        Table currentTable = manager.getCurrentTable();
        if (currentTable != null) {
            String oldValue = currentTable.put(command[1], command[2]);
            if (oldValue != null) {
                System.out.println("overwrite");
                System.out.println(oldValue);
            } else {
                System.out.println("new");
            }
        } else {
            System.out.println("no table");
        }
    }

    protected static void get(String[] command) throws IOException {
        checkArguments(2, command.length, 2, "get");
        Table currentTable = manager.getCurrentTable();
        if (currentTable != null) {
            String value = currentTable.get(command[1]);
            if (value != null) {
                System.out.println("found");
                System.out.println(value);
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("no table");
        }
    }

    protected static void remove(String[] command) throws IOException {
        checkArguments(2, command.length, 2, "remove");
        Table currentTable = manager.getCurrentTable();
        if (currentTable != null) {
            String removedValue = currentTable.removeKey(command[1]);
            if (removedValue != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("no table");
        }
    }

    protected static void drop(String[] command) throws WrongNumberOfArgumentsException {
        checkArguments(2, command.length, 2, "drop");
        try {
            manager.dropTable(command[1]);
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    protected static void exit(String[] command) throws DatabaseExitException {
        checkArguments(1, command.length, 1, "exit");
        if (manager.getCurrentTable() != null) {
            try {
                manager.getCurrentTable().saveData();
            } catch (IOException e) {
                System.out.println("Error writing table to file");
                throw new DatabaseExitException(-1);
            }
        }
        throw new DatabaseExitException(0);
    }
}
