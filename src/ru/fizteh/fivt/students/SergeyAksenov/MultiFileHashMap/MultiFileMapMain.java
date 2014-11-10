package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

import java.util.HashMap;

public class MultiFileMapMain {
    private static HashMap<String, Command> initHashMap() {
        HashMap<String, Command> commands = new HashMap<>();
        commands.put("get", new GetCommand());
        commands.put("put", new PutCommand());
        commands.put("remove", new RemoveCommand());
        commands.put("list", new ListCommand());
        commands.put("exit", new ExitCommand());
        commands.put("drop", new DropCommand());
        commands.put("create", new CreateCommand());
        commands.put("show", new ShowCommand());
        commands.put("use", new UseCommand());
        return commands;
    }

    public static void main(String[] args) {
        try {//
            HashMap<String, Command> commandMap = initHashMap();
            DataBase dataBase = new DataBase();
               Executor.interactiveMode(commandMap, dataBase);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
}
