package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vasilev Kirill on 22.09.2014.
 */
public class Shell {
    static String currentPath = "C:";
    private static Map<String, Command> commandMap = new HashMap<>();

    static {
        commandMap.put(new CdCommand().toString(), new CdCommand());
        commandMap.put(new MkdirCommand().toString(), new MkdirCommand());
        commandMap.put(new PwdCommand().toString(), new PwdCommand());
        commandMap.put(new RmCommand().toString(), new RmCommand());
        commandMap.put(new CpCommand().toString(), new CpCommand());
        commandMap.put(new MvCommand().toString(), new MvCommand());
        commandMap.put(new LsCommand().toString(), new LsCommand());
        commandMap.put(new CatCommand().toString(), new CatCommand());
    }

    public static void main(String[] args) {
        int status = 0;
        if (args.length == 0) {
            new Shell().handle(System.in);
        } else {
            status = new Shell().handle(args);
        }
        System.exit(status);
    }

    private void handle(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String command = "";
            while (!command.equals("exit")) {
                System.out.print("$ ");
                command = reader.readLine();
                String[] cmds = command.split("\\s+");
                Command currentCommand;
                if ((currentCommand = commandMap.get(cmds[0])) != null) {
                    currentCommand.execute(cmds);
                }
            }
        } catch (IOException e) {
            System.err.println("IOException caught");
        } catch (Exception e) {
            System.err.println("Exception caught");
        }
    }

    private int handle(String[] args) {
        int status = 0;
        if (args.length == 0) return 0;
        String[] currentArgs = new String[4];
        int argIterator = 0;
        try {
            Command currentCommand;
            for (int i = 0; i < args.length; ++i) {
                if (!args[i].equals(";")) {
                    currentArgs[argIterator++] = args[i];
                } else {
                    if ((currentCommand = commandMap.get(currentArgs[0])) != null) {
                        if (currentCommand.execute(currentArgs) == 1) status = 1;
                    }
                    currentArgs = new String[4];
                    argIterator = 0;
                }
            }
            if ((currentCommand = commandMap.get(currentArgs[0])) != null) {
                currentCommand.execute(currentArgs);
            }
        } catch (IOException e) {
            System.err.println("IOException caught");
        } catch (Exception e) {
            System.err.println("Exception caught");
        }
        return status;
    }
}
