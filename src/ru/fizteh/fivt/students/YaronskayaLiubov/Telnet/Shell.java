package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by luba_yaronskaya on 21.12.14.
 */
public class Shell {
    public String dbDir;
    protected RemoteDataTableProvider provider;
    private Map<String, TelnetCommand> shellCommands;

    boolean isInteractive = false;

    public Shell() {
        shellCommands = new HashMap<>();
        shellCommands.put("commit", new CommitCommand());
        shellCommands.put("connect", new ConnectCommand());
        shellCommands.put("create", new CreateCommand());
        shellCommands.put("disconnect", new DisconnectCommand());
        shellCommands.put("drop", new DropCommand());
        shellCommands.put("exit", new ExitCommand());
        shellCommands.put("get", new GetCommand());
        shellCommands.put("list", new ListCommand());
        shellCommands.put("listusers", new ListusersCommand());
        shellCommands.put("put", new PutCommand());
        shellCommands.put("remove", new RemoveCommand());
        shellCommands.put("rollback", new RollbackCommand());
        shellCommands.put("show", new ShowCommand());
        shellCommands.put("start", new StartCommand());
        shellCommands.put("stop", new StopCommand());
        shellCommands.put("use", new UseCommand());
        shellCommands.put("whereami", new WhereamiCommand());

    }

    public void interactiveMode() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    provider.close();
                } catch (IOException e) {
//
                }
            }
        });
        isInteractive = true;
        System.out.print(System.getProperty("user.dir") + "$ ");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {

            for (String s : scanner.nextLine().split(";")) {
                try {
                    executeCommand(s, false);
                } catch (CommandRuntimeException | ClientRuntimeException | ServerRuntimeException e) {
                    printMessage(e.getMessage());
                } finally {
                    save();
                }
            }
            System.out.print(System.getProperty("user.dir") + "$ ");
        }
    }

    public void packetMode(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    provider.close();
                } catch (IOException e) {
//
                }
            }
        });
        isInteractive = false;
        String[] argsPool = String.join(" ", args).split(";");
        for (String cmd: argsPool) {
            try {
                executeCommand(cmd, false);
            } catch (CommandRuntimeException | ClientRuntimeException | ServerRuntimeException e) {
                printMessage(e.getMessage());
            } finally {
                save();
            }
        }
    }

    public void printMessage(String exceptionMessage) {
        if (isInteractive) {
            System.out.println(exceptionMessage);
        } else {
            System.err.println(exceptionMessage);
        }
    }

    public void setProvider(RemoteDataTableProvider provider) {
        this.provider = provider;
    }

    public String executeCommand(String s, boolean isServer) {

            String[] argv = s.trim().split("\\s+");
            String curCommand = argv[0];
            if (curCommand.equals("")) {
                return "";
            }
            if (shellCommands.get(curCommand) == null) {
                printMessage(curCommand + ": command not found");
                return null;
            }
            String result = null;
            try {
                result = shellCommands.get(curCommand).execute(provider, argv);
            } catch (CommandRuntimeException | ClientRuntimeException | ServerRuntimeException e) {
                if (isServer) {
                    return e.getMessage();
                } else {
                    printMessage(e.getMessage());
                    return null;
                }
            }

            if (!isServer) {
                System.out.println(result);
            }

        return result;
    }

    public void save() {
        for (String tableName : provider.getTables().keySet()) {
            StoreableDataTable table = provider.getTables().get(tableName);
            table.save();
        }

    }
}
