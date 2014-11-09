package ru.fizteh.fivt.students.ilivanov.FileMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Shell {

    final private HashMap<String, ShellCommand> commands;
    private boolean exit;


    public Shell() {
        commands = new HashMap<>();
        commands.put("exit", new ShellCommand("exit", new ShellExecutable() {
            @Override
            public int execute(final Shell shell, final ArrayList<String> args) {
                stop();
                return 0;
            }
        }));
    }

    private int parseString(final String s) {
        ArrayList<String> command = new ArrayList<>();
        int start = 0;
        boolean quote = false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ';' && !quote) {
                command.add(s.substring(start, i));
                start = i + 1;
            }
            if (s.charAt(i) == '"') {
                quote ^= true;
            }
        }
        if (quote) {
            System.err.println("wrong quotation sequence");
            return -1;
        }
        if (start != s.length()) {
            command.add(s.substring(start, s.length()));
        }
        for (String word : command) {
            String name = "";
            ArrayList<String> args = new ArrayList<>();
            ArrayList<String> selfParseArgs = new ArrayList<>();
            boolean nameRead = false;
            start = 0;
            quote = false;
            for (int j = 0; j < word.length(); j++) {
                if (!quote && Character.isWhitespace(word.charAt(j))) {
                    if (start != j) {
                        if (!nameRead) {
                            name = word.substring(start, j);
                            nameRead = true;
                            selfParseArgs.add(word.substring(j + 1, word.length()));
                        } else {
                            args.add(word.substring(start, j));
                        }
                    }
                    start = j + 1;
                }
                if (word.charAt(j) == '"') {
                    if (!nameRead) {
                        System.err.println("arguments are specified, but no command was given");
                        return -1;
                    }
                    if (quote) {
                        args.add(word.substring(start, j));
                        if (j + 1 != word.length() && !Character.isWhitespace(word.charAt(j + 1))) {
                            System.err.println("wrong argument format (maybe space-character is forgotten)");
                            return -1;
                        }
                    } else if (!Character.isWhitespace(word.charAt(j - 1))) {
                        System.err.println("wrong argument format (maybe space-character is forgotten)");
                        return -1;
                    }
                    quote ^= true;
                    start = j + 1;
                }
            }
            if (start != word.length()) {
                if (!nameRead) {
                    name = word.substring(start, word.length());
                    selfParseArgs.add("");
                } else {
                    args.add(word.substring(start, word.length()));
                }
            }
            ShellCommand shellCommand = commands.get(name);
            if (shellCommand != null) {
                if (shellCommand.parsingRequired) {
                    if (shellCommand.exec.execute(this, args) != 0) {
                        return -1;
                    }
                } else {
                    if (shellCommand.exec.execute(this, selfParseArgs) != 0) {
                        return -1;
                    }
                }
            }
            if (shellCommand == null && !name.equals("")) {
                System.err.printf("unknown command; %s\n", name);
                return -1;
            }
        }
        return 0;
    }

    public void addCommand(final ShellCommand command) {
        commands.put(command.name, command);
    }

    public int runArgs(final String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s).append(" ");
        }
        String argString = sb.toString();
        return parseString(argString);
    }

    public int run(final BufferedReader br) {
        exit = false;
        int code;
        int exitCode = 0;
        while (!exit) {
            System.out.print("$ ");
            try {
                String str = br.readLine();
                if (str == null) {
                    return exitCode;
                }
                code = parseString(str);
                if (code != 0) {
                    exitCode = code;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return exitCode;
    }

    public interface ShellExecutable {
        int execute(Shell shell, ArrayList<String> args);
    }

    public static class ShellCommand {
        final private String name;
        final private ShellExecutable exec;
        final private boolean parsingRequired;

        public ShellCommand(final String name, final ShellExecutable exec) {
            this.name = name;
            this.exec = exec;
            this.parsingRequired = true;
        }
    }

    public void stop() {
        exit = true;
    }

}
