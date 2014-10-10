package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

import java.util.ArrayList;

public final class Commander {
    private Commander() {
        //
    }
    public static void commandExec(final String[] command,
            final DataBase dataBase) throws MapExcept {
        if (command.length == 0) {
            return;
        }

        switch (command[0]) {
        case "put":
            try {
                dataBase.put(command[1], command[2]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new MapExcept("put: missing operand");
            }
            break;
        case "get":
            try {
                dataBase.get(command[1]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new MapExcept("get: missing operand");
            }
            break;
        case "remove":
            try {
                dataBase.remove(command[1]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new MapExcept("remove: missing operand");
            }
            break;
        case "list":
                dataBase.listCommand();
            break;
        case "exit":
            dataBase.writeInFile();
            System.exit(0);
            break;
        default:
            throw new MapExcept(command[0] + ": No such command");
        }
    }
    public static void commandExec(final ArrayList<String> command,
            final DataBase dataBase) throws MapExcept {
        if (command.size() == 0) {
            return;
        }

        switch (command.get(0)) {
        case "put":
            try {
                dataBase.put(command.get(1), command.get(2));
            } catch (IndexOutOfBoundsException ex) {
                throw new MapExcept("put: missing operand");
            }
            break;
        case "get":
            try {
                dataBase.get(command.get(1));
            } catch (IndexOutOfBoundsException ex) {
                throw new MapExcept("get: missing operand");
            }
            break;
        case "remove":
            try {
                dataBase.remove(command.get(1));
            } catch (IndexOutOfBoundsException ex) {
                throw new MapExcept("remove: missing operand");
            }
            break;
        case "list":
                dataBase.listCommand();
            break;
        case "exit":
            dataBase.writeInFile();
            System.exit(0);
        default:
            throw new MapExcept(command.get(0)
                    + ": No such command");
        }
    }
}
