package ru.fizteh.fivt.students.elina_denisova.file_map;

import java.util.ArrayList;

public class ParserCommands {

    public static void commandsExecution(String[] command, DataBase dataBase) throws IllegalMonitorStateException{
        if (command.length == 0) {
            return;
        }
        try {
            Commands request = Commands.getCommand(command[0]);
            switch (request) {
                case PUTCOMMAND:
                    try {
                        dataBase.dbPut(command[1], command[2]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("put: need two arguments" + e);

                    }
                    break;
                case GETCOMMAND:
                    try {
                        dataBase.dbGet(command[1]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("get: need argument" + e);
                    }
                    break;
                case REMOVECOMMAND:
                    try {
                        dataBase.dbRemove(command[1]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("remove: need argument" + e);
                    }
                    break;
                case LISTCOMMAND:
                    dataBase.dbList();
                    break;
                case EXITCOMMAND:
                    throw new IllegalMonitorStateException("Exit");
                default:
                    System.err.println(command[0] + " - unknown command");
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.toString());
        }
    }



    public static void commandsExecution(ArrayList<String> commands, DataBase dataBase) {
        if (commands.size() == 0) {
            return;
        }
        String[] com = new String[commands.size()];
        com = commands.toArray(com);
        commandsExecution(com, dataBase);
    }
}
