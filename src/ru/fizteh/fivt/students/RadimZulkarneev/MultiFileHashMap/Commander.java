package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

import java.io.IOException;
import java.util.ArrayList;


public class Commander {
    public static void command(final ArrayList<String> com, DataBase dBase) 
            throws MapException, IOException, TableConnectionException, DataBaseCorrupt {
        String[] it = (String[]) com.toArray(new String[0]);
        command(it, dBase);
    }
    
    public static void command(final String[] com, DataBase dBase) 
            throws MapException, IOException, TableConnectionException, DataBaseCorrupt {
        if (com.length == 0) {
            return;
        }
        argumentsOverflowAssertion(com[0], com.length);
        switch (com[0]) {
        case "create":
            dBase.create(com[1]);
            System.out.println("created");
            break;
        case "show":
            if (com[1].equals("tables")) {
                if (com.length > 2) {
                    throw new MapException("show tables: too much arguments");
                    }
                dBase.showTables();
            } else {
                throw new MapException(com[0] + " " + com[1] + ": No such command");
            }
            break;
        case "get":
            dBase.get(com[1]);
            break;
        case "remove":
            dBase.remove(com[1]);
            break;
        case "drop":
            dBase.drop(com[1]);
            break;
        case "use":
            dBase.use(com[1]);
            break;
        case "put":
            dBase.put(com[1], com[2]);
            break;
        case "list":
            dBase.list();
            break;
        case "exit":
            dBase.commit();
            throw new IllegalStateException();
        default:
            throw new MapException(com[0] + ": No such command");
        }
    }
    

    
    private static void argumentsOverflowAssertion(String command, int argCount) throws MapException {
        switch (command) {
        case "create":
            if (argCount > 2) {
                throw new MapException(command + ": too much arguments");
            }
            break;
        case "drop":
            if (argCount > 2) {
                throw new MapException(command + ": too much arguments");
            }
            break;
        case "use":
            if (argCount > 2) {
                throw new MapException(command + ": too much arguments");
            }
            break;
        case "put":
            if (argCount > 3) {
                throw new MapException(command + ": too much arguments");
            }
            break;
        case "list":
            if (argCount > 1) {
                throw new MapException(command + ": too much arguments");
            }
            break;
        case "remove":
            if (argCount > 2) {
                throw new MapException(command + ": too much arguments");
            }
            break;
        default: return;
        }
    }
}
