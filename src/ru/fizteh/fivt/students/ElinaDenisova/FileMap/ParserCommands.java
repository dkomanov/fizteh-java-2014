package ru.fizteh.fivt.students.ElinaDenisova.FileMap;

import java.util.ArrayList;

import static ru.fizteh.fivt.students.ElinaDenisova.FileMap.ReqType.*;

public class ParserCommands {

    public static void commandExec(String[] command,
            DataBase dataBase) throws DataBaseException {
        if (command.length == 0) {
            return;
        }
        ReqType request = getType(command[0]);
        switch (request) {
        case PUTCOMMAND:
            try {
                dataBase.put(command[1], command[2]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new DataBaseException("put: need two arguments");

            }
            break;
        case GETCOMMAND:
            try {
                dataBase.get(command[1]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new DataBaseException("get: need argument");
            }
            break;
        case REMOVECOMMAND:
            try {
                dataBase.remove(command[1]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new DataBaseException("remove: need argument");
            }
            break;
        case LISTCOMMAND:
                dataBase.listCommand();
            break;
        case EXITCOMMAND:
            dataBase.writeInFile();
            System.exit(0);
            break;
        default:
            throw new DataBaseException(command[0] + ": unknown command");
        }
    }

    public static void commandExec(ArrayList<String> command,
            DataBase dataBase) throws DataBaseException {
        if (command.size() == 0) {
            return;
        }
        String[] com = new String[command.size()];
        com = command.toArray(com);
        commandExec(com, dataBase);
    }
}
