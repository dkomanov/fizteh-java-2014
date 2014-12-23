package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata;

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
    private Object monitor;

    static {
        currentPath = new File("").getAbsolutePath();
    }
    public Shell(Map<String, Command> commandMap, Status status) {
        this.commandMap = commandMap;
        this.status = status;
        //this.monitor = monitor;
    }

    public void handle(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String command = "";
            while (!command.equals("exit") && !command.equals("stop")) {
                System.out.print("$ ");
                command = reader.readLine();
                String[] cmds = command.split("\\s+");
                int result = new Shell(commandMap, status).handle(cmds);
                if (result == 2) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(e);
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
                if (args[i].charAt(j) != '\"') {
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
                if (it[0] == null) {
                    continue;
                }
                if (it[0].equals("exit") || it[0].equals("stop")) {
                    return 2;
                }
                if ((currentCommand = commandMap.get(it[0])) != null) {
                    return currentCommand.execute(it, status);
                    /*if (it[0].equals("start")) {
                        synchronized (monitor) {
                            monitor.notifyAll();
                        }
                    }*/
                } else {
                    return 1;
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(-1);
        }
        return 0;
    }
}
