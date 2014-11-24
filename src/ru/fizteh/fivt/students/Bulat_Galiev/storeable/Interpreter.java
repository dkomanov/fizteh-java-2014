package ru.fizteh.fivt.students.Bulat_Galiev.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.storeable.commands.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Interpreter {
    private static final int SLEEPNUMBER = 5;
    private static Map<String, Command> commandMap = new HashMap<String, Command>();;

    public Interpreter(final TableProvider provider, final String[] args)
            throws IOException {
        commandMap.put("put", new CommandPut());
        commandMap.put("get", new CommandGet());
        commandMap.put("remove", new CommandRemove());
        commandMap.put("show", new CommandShowtables());
        commandMap.put("exit", new CommandExit());
        commandMap.put("create", new CommandCreate());
        commandMap.put("drop", new CommandDrop());
        commandMap.put("use", new CommandUse());
        commandMap.put("size", new CommandSize());
        commandMap.put("commit", new CommandCommit());
        commandMap.put("rollback", new CommandRollback());
        commandMap.put("list", new CommandList());

        if (args.length == 0) {
            interactiveMode(provider);
        } else {
            batchMode(provider, args);
        }
    }

    public static void interactiveMode(final TableProvider provider)
            throws IOException {
        try {
            String input = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            do {
                try {
                    Thread.sleep(SLEEPNUMBER);
                } catch (InterruptedException e1) {
                    System.err.print(e1.getMessage());
                }
                System.out.print("$ ");
                try {
                    input = in.readLine();
                } catch (IOException e) {
                    System.err.print(e.getMessage());
                    System.exit(-1);
                }
                parser(provider, input, false);
            } while (true);
        } catch (NullPointerException e) {
            System.exit(-1);
        }
    }

    public static void batchMode(final TableProvider provider,
            final String[] input) throws IOException {
        try {
            StringBuilder cmd = new StringBuilder();
            for (String argument : input) {
                if (cmd.length() != 0) {
                    cmd.append(' ');
                }
                cmd.append(argument);
            }
            String arg = cmd.toString();
            parser(provider, arg, true);
        } catch (NullPointerException e) {
            System.exit(-1);
        }
    }

    public static void parser(final TableProvider provider, final String input,
            final boolean mode) throws IOException {
        final StringTokenizer tok = new StringTokenizer(input, ";", false);
        while (tok.hasMoreTokens()) {
            int i = 0;
            StringTokenizer argtok = new StringTokenizer(tok.nextToken(), " ",
                    false);
            if (argtok.countTokens() == 0) {
                System.err.println("null command");
                continue;
            }
            String[] arg = new String[argtok.countTokens()];
            while (argtok.hasMoreTokens()) {
                arg[i++] = argtok.nextToken();
            }
            Command command = commandMap.get(arg[0]);

            if (command == null) {
                System.err.println(arg[0] + " is incorrect command");
                if (mode) {
                    System.exit(-1);
                }
                continue;
            }
            if (arg[0].equals("create")
                    && arg.length >= command.getArgumentsCount() + 1) {
                try {
                    command.execute(provider, arg);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    if (mode) {
                        System.exit(-1);
                    }
                }
            } else if (arg.length != command.getArgumentsCount() + 1) {
                System.err.println(command.getName() + ": wrong number of arguments");
                if (mode) {
                    System.exit(-1);
                }
            } else if (arg[0].equals("show")) {
                if (arg[1].equals("tables")) {
                    try {
                        command.execute(provider, arg);
                    } catch (IllegalArgumentException e) {
                        System.err.println(e.getMessage());
                        if (mode) {
                            System.exit(-1);
                        }
                    }
                } else {
                    System.err.println(arg[0] + " " + arg[1]
                            + " is incorrect command");
                    if (mode) {
                        System.exit(-1);
                    }
                }
            } else {
                try {
                    command.execute(provider, arg);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    if (mode) {
                        System.exit(-1);
                    }
                }
            }
        }
        if (mode) {
            System.exit(0);
        }
    }

}
