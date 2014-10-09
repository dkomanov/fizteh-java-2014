package ru.fizteh.fivt.students.ilivanov.FileMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ShellDb {
    private int exitCode = 0;

    private ArrayList<CommandDb> parseCommands(final StringBuilder source) {
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == ';') {
                source.insert(i, ' ');
                i++;
            }
        }
        String src = source.toString();
        src = src.replaceAll("\\s+$", "");
        if (!src.isEmpty() && src.charAt(src.length() - 1) != ';') {
            src = src + " ;";
        }

        String[] subs;
        subs = src.split(" +");

        ArrayList<CommandDb> commands = new ArrayList<>();
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
                        case "put":
                            commands.add(new CommandDbPut(command));
                            break;
                        case "get":
                            commands.add(new CommandDbGet(command));
                            break;
                        case "remove":
                            commands.add(new CommandDbRemove(command));
                            break;
                        case "list":
                            commands.add(new CommandDbList(command));
                            break;
                        case "exit":
                            commands.add(new CommandDbExit());
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
            } else {
                command.add(sub);
            }
        }
        return commands;
    }

    private int executeCommands(final ArrayList<CommandDb> commands) {
        int code;
        for (CommandDb command : commands) {
            code = command.execute();
            if (code == 7) {
                return 7;
            }
            try {
                FileUsing.writeDb();
            } catch (Exception e) {
                System.err.println("Can't write to file");
                exitCode = -100;
                return 7;
            }
            exitCode += code;
        }
        return 0;
    }

    public final int runPackage(final String[] args) {
        StringBuilder summarize = new StringBuilder("");
        for (String arg : args) {
            summarize.append(arg).append(" ");
        }
        executeCommands(parseCommands(summarize));
        return exitCode;
    }

    public final void runInteractive() {
        int isExit;
        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while (true) {
                System.out.print("db$ ");
                line = br.readLine();
                if (line != null) {
                    isExit =
                            executeCommands(parseCommands(new StringBuilder(line)));
                    if (isExit == 7) {
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
