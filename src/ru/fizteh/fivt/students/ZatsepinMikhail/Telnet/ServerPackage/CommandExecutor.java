package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.Command;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap;

import java.io.PrintStream;
import java.util.HashMap;

public class CommandExecutor {
    private HashMap<String, Command<MFileHashMap>> shellCommands;

    public CommandExecutor() {
        shellCommands = new HashMap<>();
        shellCommands.put()
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
            Command<MFileHashMap> commandToExecute = shellCommands.get(parsedArguments[0]);
            if (commandToExecute != null) {
                try {
                    if (commandToExecute.getNumberOfArguments() != parsedArguments.length
                            & commandToExecute.getNumberOfArguments() != -1) {
                        System.out.println(commandToExecute.getName() + ": wrong number of arguments");
                        errorOccuried = true;
                    } else if (!commandToExecute.run((MFileHashMap) dataBase, parsedArguments)) {
                        errorOccuried = true;
                    }
                } catch (IllegalStateException e) {
                    output.println(e.getMessage());
                    errorOccuried = true;
                }
            } else {
                System.out.println(parsedArguments[0] + ": command not found");
                errorOccuried = true;
            }
            if (errorOccuried) {
                throw new IllegalStateException();
            }
        }
    }
}
