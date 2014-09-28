package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by mikhail on 28.09.14.
 */
public class FileMap {
    private HashMap<String, String> dataBase;
    private HashMap<String, Command> fileMapCommands;
    public FileMap() {
        dataBase = new HashMap<>();
        fileMapCommands = new HashMap<>();
    }
    public void addCommand(Command newCommand) {
        fileMapCommands.put(newCommand.toString(), newCommand);
    }

    public void init(FileOutputStream outputStream) {
        System.out.println("Init here");
    }

    public boolean interactiveMode(FileOutputStream outStream) {
        boolean ended = false;
        boolean errorOccuried = false;

        try (Scanner inStream = new Scanner(System.in)) {
            String[] parsedCommands;
            String[] parsedArguments;
            while (!ended) {
                if (inStream.hasNextLine()) {
                    parsedCommands = inStream.nextLine().split(";|\n");
                } else {
                    continue;
                }
                for (String oneCommand : parsedCommands) {
                    parsedArguments = oneCommand.split("\\s+");
                    if (parsedArguments[0].equals("exit")) {
                        ended = true;
                        break;
                    }
                    Command commandToExecute = fileMapCommands.get(parsedArguments[0]);
                    if (commandToExecute != null) {
                        if (!commandToExecute.run(dataBase, parsedArguments)) {
                            errorOccuried = true;
                        }
                    } else {
                        System.out.println(parsedArguments[0] + ": command not found");
                        errorOccuried = true;
                    }
                }
                if (!ended) {
                    System.out.print("$ ");
                }
            }
        }
        return !errorOccuried;
    }
}
