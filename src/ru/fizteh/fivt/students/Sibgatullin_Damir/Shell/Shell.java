package ru.fizteh.fivt.students.Sibgatullin_Damir.Shell;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Lenovo on 30.09.2014.
 */
public class Shell {

    public static void main(String[] args) {
        if (args.length == 0) {
            Shell.interfaceMode();
        } else {
            Shell.packageMode(args);
        }
    }

    static Path currentPath = Paths.get("").toAbsolutePath();

    private static void switchCommand(String[] command)throws MyException {

        Map<String, Commands> commands = new HashMap<String, Commands>();
        commands.put(new CdCommand().getName(), new CdCommand());
        commands.put(new MkdirCommand().getName(), new MkdirCommand());
        commands.put(new PwdCommand().getName(), new PwdCommand());
        commands.put(new RmCommand().getName(), new RmCommand());
        commands.put(new CpCommand().getName(), new CpCommand());
        commands.put(new MvCommand().getName(), new MvCommand());
        commands.put(new LsCommand().getName(), new LsCommand());
        commands.put(new CatCommand().getName(), new CatCommand());
        commands.put(new ExitCommand().getName(), new ExitCommand());

        try {
            commands.get(command[0]).execute(command);
        } catch (NullPointerException e) {
            throw new MyException("No such command");
        }
    }

    private static void interfaceMode() {
        Scanner input = new Scanner(System.in);
        System.out.print("$ ");
        while (true) {
            String com = input.nextLine();
            if (com.length() == 0) {
                System.out.print("$ ");
                continue;
            }
            String[] commands = com.split(";");
            for (String string: commands) {
                try {
                    String[] command = string.trim().split("\\s+");
                    switchCommand(command);
                } catch (MyException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.print("$ ");
        }
    }
    private static void packageMode(String[] args) {
        LinkedHashSet<String> com = new LinkedHashSet<String>();
        for (String string : args) {
            if (!string.equals(";")) {
                if (string.endsWith(";")) {
                    string = string.substring(0, string.length() - 1);
                    com.add(string);
                    try {
                        switchCommand(com.toArray(new String[com.size()]));
                    } catch (MyException e) {
                        System.exit(1);
                    }

                    com.clear();
                } else
                    com.add(string);
            } else {
                try {
                    switchCommand(com.toArray(new String[com.size()]));
                } catch (MyException e) {
                    System.exit(1);
                }

                com.clear();
            }
        }

        try {
            switchCommand(com.toArray(new String[com.size()]));
        } catch (MyException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        com.clear();
    }
}
