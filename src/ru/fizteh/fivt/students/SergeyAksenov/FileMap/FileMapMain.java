package ru.fizteh.fivt.students.SergeyAksenov.FileMap;

import java.util.HashMap;

public class FileMapMain {
    private static HashMap<String, Command> initHashMap() {
        HashMap<String, Command> commands = new HashMap<>();
        commands.put("get", new GetCommand());
        commands.put("put", new PutCommand());
        commands.put("remove", new RemoveCommand());
        commands.put("list", new ListCommand());
        commands.put("exit", new ExitCommand());
        return commands;
    }

    public static void main(String[] args) {
       try {
            HashMap<String, Command> commandMap = initHashMap();
            Environment env = new Environment(args);
            if (env.packageMode) {
                Executor.packageAppender(args, commandMap, env);
            } else {
                Executor.interactiveMode(commandMap, env);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }//
    }
}

