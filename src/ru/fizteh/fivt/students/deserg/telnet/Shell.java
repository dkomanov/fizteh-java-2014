package ru.fizteh.fivt.students.deserg.telnet;

import ru.fizteh.fivt.students.deserg.telnet.client.commands.ClientCommand;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;
import ru.fizteh.fivt.students.deserg.telnet.server.commands.*;

import java.util.*;

/**
 * Created by deserg on 11.12.14.
 */
public class Shell {


    private static Map<String, DbCommand> serverCommandMap;
    private static Map<String, ClientCommand> clientCommandMap;

    static {

        serverCommandMap = new HashMap<>();
        serverCommandMap.put("exit", new TableExit());
        serverCommandMap.put("get", new TableGet());
        serverCommandMap.put("list", new TableList());
        serverCommandMap.put("put", new TablePut());
        serverCommandMap.put("remove", new TableRemove());
        serverCommandMap.put("size", new TableSize());
        serverCommandMap.put("commit", new TableCommit());
        serverCommandMap.put("rollback", new TableRollback());
        serverCommandMap.put("create", new DbCreate());
        serverCommandMap.put("drop", new DbDrop());
        serverCommandMap.put("use", new DbUse());
        serverCommandMap.put("show", new DbShowTables());

        clientCommandMap = new HashMap<>();


    }

    public static String executeServerCommand(String commands, DbTableProvider db) {

        return execute(commands, db);

    }

    public static String executeClientCommand(String commands) {

        return execute(commands, null);
    }


    private static String execute(String commands, DbTableProvider db) {

        Queue<ArrayList<String>> commandQueue = new LinkedList<>();

        System.out.print("$ ");

        String lineStr = "";

        if (commands == null) {
            Scanner lineScan = new Scanner(System.in);
            if (lineScan.hasNext()) {
                lineStr = lineScan.nextLine();
            } else {
                System.exit(1);
            }
        } else {
            lineStr = commands;
        }

        String[] commandBlockAr = lineStr.split(";");

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



            if (db != null) {
                DbCommand command;
                command = serverCommandMap.get(arguments.get(0));
                if (command != null) {
                    result += command.execute(arguments, db);
                }
            } else {
                ClientCommand command;
                command = clientCommandMap.get(arguments.get(0));
                if (command != null) {
                    result += command.execute(arguments);
                }
            }

        }

        return result;
    }

}
