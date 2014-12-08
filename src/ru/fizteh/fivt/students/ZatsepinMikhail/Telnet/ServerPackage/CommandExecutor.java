package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider.*;

import java.io.PrintStream;
import java.util.HashMap;

public class CommandExecutor {
    private final HashMap<String, CommandTableProvider> shellCommands;

    public CommandExecutor() {
        shellCommands = new HashMap<>();
        shellCommands.put("commit", new CommandCommit());
        shellCommands.put("create", new CommandCreate());
        shellCommands.put("drop", new CommandDrop());
        shellCommands.put("get", new CommandGetDistribute());
        shellCommands.put("list", new CommandListDistribute());
        shellCommands.put("put", new CommandPutDistribute());
        shellCommands.put("remove", new CommandRemoveDistribute());
        shellCommands.put("rollback", new CommandRollback());
        shellCommands.put("show", new CommandShowTables());
        shellCommands.put("size", new CommandSize());
        shellCommands.put("use", new CommandUse());
        shellCommands.put("describe", new CommandDescribe());
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
            CommandTableProvider commandToExecute = shellCommands.get(parsedArguments[0]);
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
                //throw new IllegalStateException();
            }
        }
    }
}
