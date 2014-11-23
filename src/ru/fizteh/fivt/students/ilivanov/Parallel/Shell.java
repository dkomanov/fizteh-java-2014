package ru.fizteh.fivt.students.ilivanov.Parallel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Shell {

    private HashMap<String, ShellCommand> commands;
    private ArrayList<ShellCommand> exitFunction;
    private boolean exit;
    public PrintStream writer;
    private String greeting;

    public Shell(PrintStream writer) {
        this.writer = writer;
        greeting = "$ ";
        commands = new HashMap<>();
        exitFunction = new ArrayList<>();
        commands.put("exit", new ShellCommand("exit", (shell, args) -> {
            stop();
            return 0;
        }));
    }

    public void stop() {
        for (ShellCommand command : exitFunction) {
            command.exec.execute(this, null);
        }
        exit = true;
    }

    private int parseString(String s) {
        String[] comm = s.split(";");
        for (String aComm : comm) {
            String[] tokens = aComm.split("\\s+");
            ArrayList<String> args = new ArrayList<>();
            ArrayList<String> selfParseArgs = new ArrayList<>();
            String name = "";
            for (String token : tokens) {
                if (!token.equals("")) {
                    if (name.equals("")) {
                        name = token;
                    } else {
                        args.add(token);
                    }
                }
            }
            for (int j = 1; j < aComm.length(); j++) {
                if (Character.isWhitespace(aComm.charAt(j)) && !Character.isWhitespace(aComm.charAt(j - 1))) {
                    selfParseArgs.add(aComm.substring(j, aComm.length()).trim());
                    break;
                }
            }
            if (selfParseArgs.size() == 0) {
                selfParseArgs.add("");
            }
            ShellCommand command = commands.get(name);
            if (command != null) {
                if (command.parsingRequired) {
                    if (command.exec.execute(this, args) != 0) {
                        return -1;
                    }
                } else {
                    if (command.exec.execute(this, selfParseArgs) != 0) {
                        return -1;
                    }
                }
            }
            if (command == null && !name.equals("")) {
                writer.printf("No such command %s%s", name, System.lineSeparator());
                return -1;
            }
        }
        return 0;
    }

    public void addCommand(ShellCommand command) {
        commands.put(command.name, command);
    }

    public void addExitFunction(ShellCommand command) {
        exitFunction.add(command);
    }

    public int runArgs(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s).append(" ");
        }
        String argString = sb.toString();
        return parseString(argString);
    }

    public int run(BufferedReader br) {
        exit = false;
        while (!exit) {
            writer.print(greeting);
            try {
                String str = br.readLine();
                if (str == null) {
                    stop();
                    return 0;
                }
                parseString(str);
            } catch (IOException e) {
                writer.println(e.getMessage());
            }
        }
        return 0;
    }

    public interface ShellExecutable {
        int execute(Shell shell, ArrayList<String> args);
    }

    public static class ShellCommand {
        String name;
        ShellExecutable exec;
        boolean parsingRequired;

        public ShellCommand(String name, ShellExecutable exec) {
            this.name = name;
            this.exec = exec;
            this.parsingRequired = true;
        }

        public ShellCommand(String name, boolean parsingRequired, ShellExecutable exec) {
            this.name = name;
            this.exec = exec;
            this.parsingRequired = parsingRequired;
        }
    }
}