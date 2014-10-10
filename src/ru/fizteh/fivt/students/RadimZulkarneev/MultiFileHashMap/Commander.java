package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

import java.io.IOException;
import java.util.ArrayList;

public class Commander {
    public static void command(final String[] com, DataBase dBase) 
            throws MapExcept, IOException, TableConnectionError, DataBaseCorrupt {
        if (com.length == 0) {
            return;
        }
        switch (com[0]) {
        case "create":
            dBase.create(com[1]);
            System.out.println("created");
            break;
        case "show":
            if (com[1].equals("tables")) {
                dBase.showTables();
            } else {
                throw new MapExcept(com[0] + " " + com[1] + ": No such command");
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
            throw new MapExcept(com[0] + ": No such command");
        }
    }
    
    public static void commandExec(final ArrayList<String> com, final DataBase dBase) 
            throws MapExcept, IOException, DataBaseCorrupt, TableConnectionError  {
        if (com.size() == 0) {
            return;
        }
        switch (com.get(0)) {
        case "create":
            dBase.create(com.get(1));
            System.out.println("created");
            break;
        case "show":
            if (com.get(1).equals("tables")) {
                dBase.showTables();
            } else {
                throw new MapExcept(com.get(0) + " " + com.get(1) + ": No such command");
            }
            break;
        case "get":
            dBase.get(com.get(1));
            break;
        case "remove":
            dBase.remove(com.get(1));
            break;
        case "drop":
            dBase.drop(com.get(1));
            break;
        case "use":
            dBase.use(com.get(1));
            break;
        case "put":
            dBase.put(com.get(1), com.get(1));
            break;
        case "list":
            dBase.list();
            break;
        case "exit":
            dBase.commit();
            throw new IllegalStateException();
        default:
            throw new MapExcept(com.get(1) + ": No such command");
        }
    }

}
