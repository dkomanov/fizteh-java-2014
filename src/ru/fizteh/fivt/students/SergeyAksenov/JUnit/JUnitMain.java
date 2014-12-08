package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

import java.util.HashMap;

public class JUnitMain {
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
        commands.put("commit", new CommitCommand());
        commands.put("rollback", new RollbackCommand());
        return commands;
    }

    public static void main(String[] args) {
        HashMap<String, Command> commands = initHashMap();
        JUnitTableProviderFactory factory = new JUnitTableProviderFactory();
        String s = System.getProperty("fizteh.db.dir");
        JUnitTableProvider tableProvider = factory.create(System.getProperty("fizteh.db.dir"));
        try {
            Executor.interactiveMode(commands, tableProvider);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
}
