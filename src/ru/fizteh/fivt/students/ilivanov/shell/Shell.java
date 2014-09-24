package ru.fizteh.fivt.students.ilivanov.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author ilivanov
 */
class Shell {
    public static File currentDirectory = new File(System.getProperty("user.home"));
    private int exitCode = 0;

    private ArrayList<Command> parseCommands(StringBuilder source) {
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == ';') {
                source.insert(i, ' ');
                i++;
            }
        }
        String src = source.toString();
        src = src.replaceAll("\\s+$", "");
        if (!src.isEmpty() && src.charAt(src.length() - 1) != ';')
            src = src + " ;";

        String[] subs;
        subs = src.split(" +");

        ArrayList<Command> commands = new ArrayList<>();
        ArrayList<String> command = new ArrayList<>();
        String name;
        for (String sub : subs) {
            if (sub.equals(";")) {
                try {
                    name = command.get(0);
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Empty command");
                    exitCode = -1;
                    continue;
                }
                try {
                    switch (name) {
                        case "cd":
                            commands.add(new CommandCd(command));
                            break;
                        case "mkdir":
                            commands.add(new CommandMkdir(command));
                            break;
                        case "pwd":
                            commands.add(new CommandPwd(command));
                            break;
                        case "rm":
                            commands.add(new CommandRm(command));
                            break;
                        case "cp":
                            commands.add(new CommandCp(command));
                            break;
                        case "mv":
                            commands.add(new CommandMv(command));
                            break;
                        case "ls":
                            commands.add(new CommandLs(command));
                            break;
                        case "exit":
                            commands.add(new CommandExit());
                            break;
                        case "cat":
                            commands.add(new CommandCat(command));
                            break;
                        default:
                            System.err.println(name + ": unknown command");
                            exitCode = -1;
                            break;
                    }
                } catch (Exception e) {
                    System.err.println(name + ": " + e.getMessage());
                    exitCode = -1;
                }
                command.clear();
            } else
                command.add(sub);
        }
        return commands;
    }

    private void executeCommands(ArrayList<Command> commands) {
        for (Command command : commands)
            exitCode = command.execute();
    }

    public void runPackage(String[] args) {
        StringBuilder summarize = new StringBuilder("");
        for (String arg : args)
            summarize.append(arg).append(" ");
        executeCommands(parseCommands(summarize));
        System.exit(exitCode);
    }

    public void runInteractive() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while (true) {
                System.out.print("shell$ ");
                line = br.readLine();
                if (line != null)
                    executeCommands(parseCommands(new StringBuilder(line)));
                else
                    break;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void exit() {
        System.exit(0);
    }
}
