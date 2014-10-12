package ru.fizteh.fivt.students.SergeyAksenov.shell;

import java.util.HashMap;


public class ShellMain {

    private static HashMap<String, Command> initHashMap() {
        HashMap<String, Command> commands = new HashMap<>();
        LsCommand ls = new LsCommand();
        commands.put("ls", ls);
        CatCommand cat = new CatCommand();
        commands.put("cat", cat);
        CdCommand cd = new CdCommand();
        commands.put("cd", cd);
        CpCommand cp = new CpCommand();
        commands.put("cp", cp);
        MkDirCommand mkDir = new MkDirCommand();
        commands.put("mkdir", mkDir);
        MvCommand mv = new MvCommand();
        commands.put("mv", mv);
        PwdCommand pwd = new PwdCommand();
        commands.put("pwd", pwd);
        RmCommand rm = new RmCommand();
        commands.put("rm", rm);
        ExitCommand exit = new ExitCommand();
        commands.put("exit", exit);
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
        }
    }
}
