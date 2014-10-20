package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

import java.io.IOException;
import java.util.ArrayList;

public class Commander {
    public static void command(final String[] com, DataBase dBase) 
            throws MapException, IOException, TableConnectionException, DataBaseCorrupt {
        if (com.length == 0) {
            return;
        }
        if (argumentsOverflowAssertion(com[0], com.length)) {
        	throw new MapException(com[0] + ": too much arguments");
        }
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
    
    public static void commandExec(final ArrayList<String> com, final DataBase dBase) 
            throws MapException, IOException, DataBaseCorrupt, TableConnectionException  {
        if (com.size() == 0) {
            return;
        }
        if (argumentsOverflowAssertion(com.get(0), com.size())) {
        	throw new MapException(com.get(0) + "too much arguments");
        }
        switch (com.get(0)) {
        case "create":
            dBase.create(com.get(1));
            System.out.println("created");
            break;
        case "show":
        	
            if (com.get(1).equals("tables")) {
            	if (com.size() > 2) {
            		throw new MapException("show tables: too much arguments");
            	}
            	dBase.showTables();
            } else {
                throw new MapException(com.get(0) + " " + com.get(1) + ": No such command");
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
            throw new MapException(com.get(1) + ": No such command");
        }
    }
    
    private static boolean argumentsOverflowAssertion(String command, int argCount) {
    	switch (command) {
    	case "create":
    		return argCount > 2;
		case "drop":
    		return argCount > 2;
		case "use":
    		return argCount > 2;
		case "put":
    		return argCount > 3;
		case "list":
    		return argCount > 1;
		case "remove":
    		return argCount > 2;
		default: return false;
    	}
    }
}
