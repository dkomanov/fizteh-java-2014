package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider.*;

import java.io.PrintStream;
import java.util.HashMap;

public class CommandExecutor {
    private final HashMap<String, CommandTableProvider> SHELL_COMMANDS;

    public CommandExecutor() {
        SHELL_COMMANDS = new HashMap<>();
        SHELL_COMMANDS.put("commit", new CommandCommit());
        SHELL_COMMANDS.put("create", new CommandCreate());
        SHELL_COMMANDS.put("drop", new CommandDrop());
        SHELL_COMMANDS.put("get", new CommandGetDistribute());
        SHELL_COMMANDS.put("list", new CommandListDistribute());
        SHELL_COMMANDS.put("put", new CommandPutDistribute());
        SHELL_COMMANDS.put("remove", new CommandRemoveDistribute());
        SHELL_COMMANDS.put("rollback", new CommandRollback());
        SHELL_COMMANDS.put("show", new CommandShowTables());
        SHELL_COMMANDS.put("size", new CommandSize());
        SHELL_COMMANDS.put("use", new CommandUse());
    }

    public void run(String message, PrintStream output, TableProvider dataBase) {
        String[] parsedCommands;
        String[] parsedArguments;
        boolean errorOccuried = false;

        parsedCommands = message.split(";|\n");
        for (String oneCommand : parsedCommands) {
            parsedArguments = oneCommand.trim().split("\\s+");
            if (parsedArguments[0].equals("put")) {
                if (oneCommand.contains("<")) {
                    String valueForPut = oneCommand.trim().substring(oneCommand.indexOf('<'));
                    parsedArguments[2] = valueForPut;
                }
            }
            if (parsedArguments.length == 0 || parsedArguments[0].equals("")) {
                continue;
            }
            if (parsedArguments[0].equals("exit")) {
                //do smth
            }
            CommandTableProvider commandToExecute = SHELL_COMMANDS.get(parsedArguments[0]);
            if (commandToExecute != null) {
                try {
                    if (commandToExecute.getNumberOfArguments() != parsedArguments.length
                            & commandToExecute.getNumberOfArguments() != -1) {
                        output.println(commandToExecute.getName() + ": wrong number of arguments");
                        errorOccuried = true;
                    } else if (!commandToExecute.run((MFileHashMap) dataBase, parsedArguments, output)) {
                        errorOccuried = true;
                    }
                } catch (IllegalStateException e) {
                    output.println(e.getMessage());
                    errorOccuried = true;
                }
            } else {
                output.println(parsedArguments[0] + ": command not found");
                errorOccuried = true;
            }
            if (errorOccuried) {
                throw new IllegalStateException();
            }
        }
    }
}
