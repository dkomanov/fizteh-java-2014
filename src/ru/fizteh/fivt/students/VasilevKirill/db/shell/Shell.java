package ru.fizteh.fivt.students.VasilevKirill.db.shell;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vasilev Kirill on 22.09.2014.
 */
public class Shell {
    static String currentPath;
    private Map<String, Command> commandMap = new HashMap<>();
    private Status status;

    static {
        currentPath = new File("").getAbsolutePath();
    }

    public static void main(String[] args) {
        Status newStatus = null;
        int returnValue = 0;
        Map<String, Command> commands = new HashMap<>();
        commands.put(new CdCommand().toString(), new CdCommand());
        commands.put(new MkdirCommand().toString(), new MkdirCommand());
        commands.put(new PwdCommand().toString(), new PwdCommand());
        commands.put(new RmCommand().toString(), new RmCommand());
        commands.put(new CpCommand().toString(), new CpCommand());
        commands.put(new MvCommand().toString(), new MvCommand());
        commands.put(new LsCommand().toString(), new LsCommand());
        commands.put(new CatCommand().toString(), new CatCommand());
        if (args.length == 0) {
            new Shell(commands, newStatus).handle(System.in);
        } else {
            returnValue = new Shell(commands, newStatus).handle(args);
        }
        System.exit(returnValue);
    }

    public Shell(Map<String, Command> commandMap, Status status) {
        this.commandMap = commandMap;
        this.status = status;
    }

    public void handle(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String command = "";
            while (!command.equals("exit")) {
                System.out.print("$ ");
                command = reader.readLine();
                String[] cmds = command.split("\\s+");
                Command currentCommand;
                if ((currentCommand = commandMap.get(cmds[0])) != null) {
                    try {
                        currentCommand.execute(cmds, status);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public int handle(String[] args) {
        if (args.length == 0) {
            return 0;
        }
        String[] currentArgs = new String[4];
        int argIterator = 0;
        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            //commandBuilder.append(args[i]);
            for (int j = 0; j < args[i].length(); ++j) {
                if (args[i].charAt(j) != '\"' && args[i].charAt(j) != '\'') {
                    commandBuilder.append(args[i].charAt(j));
                }
            }
            if (i != args.length - 1) {
                commandBuilder.append(" ");
            }
        }
        String[] cmdsBySemicolon = commandBuilder.toString().split("\\s*;\\s*");
        String[][] newArgs = new String[cmdsBySemicolon.length][5];
        int arrayIterator = 0;
        for (int i = 0; i < cmdsBySemicolon.length; ++i) {
            if (!cmdsBySemicolon[i].equals("")) {
                newArgs[arrayIterator++] = cmdsBySemicolon[i].split("\\s+");
            }
        }
        try {
            Command currentCommand;
            for (String[] it : newArgs) {
                if (it[0] == null || it[0].equals("exit")) {
                    continue;
                }
                if ((currentCommand = commandMap.get(it[0])) != null) {
                    if (currentCommand.execute(it, status) == 1) {
                        return 1;
                    }
                } else {
                    return 1;
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }
}
