package ru.fizteh.fivt.students.deserg.telnet;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;
import ru.fizteh.fivt.students.deserg.telnet.commands.*;

import java.util.*;

/**
 * Created by deserg on 11.12.14.
 */
public class DbCommandExecuter {


    private static Map<String, DbCommand> dbCommandMap;

    static {

        dbCommandMap = new HashMap<>();
        dbCommandMap.put("get", new TableGet());
        dbCommandMap.put("list", new TableList());
        dbCommandMap.put("put", new TablePut());
        dbCommandMap.put("remove", new TableRemove());
        dbCommandMap.put("size", new TableSize());
        dbCommandMap.put("commit", new TableCommit());
        dbCommandMap.put("rollback", new TableRollback());
        dbCommandMap.put("create", new DbCreate());
        dbCommandMap.put("drop", new DbDrop());
        dbCommandMap.put("use", new DbUse());
        dbCommandMap.put("show", new DbShowTables());

    }


    public static String executeDbCommand(String commands, DbTableProvider db) {

        Queue<ArrayList<String>> commandQueue = new LinkedList<>();

        System.out.print("$ ");

        String[] commandBlockAr = commands.split(";");

        for (String commandBlock: commandBlockAr) {
            String[] argsStr = commandBlock.trim().split("\\s+");
            ArrayList<String> argumentVector = new ArrayList<>();

            for (String arg: argsStr) {
                argumentVector.add(arg);
            }

            if (argumentVector.size() > 0) {
                commandQueue.add(argumentVector);
            }
        }

        return executeQueue(commandQueue, db);

    }



    private static String executeQueue(Queue<ArrayList<String>> commandQueue, DbTableProvider db) {

        if (commandQueue.size() == 0) {
            return null;
        }

        String result = "";

        while (commandQueue.size() > 0) {
            ArrayList<String> arguments = commandQueue.poll();

            DbCommand command;
            command = dbCommandMap.get(arguments.get(0));
            if (command != null) {
                result += command.execute(arguments, db);
            }

        }

        return result;
    }

}
