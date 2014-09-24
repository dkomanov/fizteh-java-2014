package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

/**
 * @author test
 */

import java.util.HashMap;
import java.util.Scanner;

public class Shell{

    private HashMap<String, Command> shellCommands;

    public Shell() {
        shellCommands = new HashMap();
    }

    public void addCommand(Command newCommand){
        shellCommands.put(newCommand.toString(), newCommand);
    }
    public boolean interactiveMode(){
        //System.out.println("In interactive mode now");

        System.out.print("$ ");
        boolean ended = false;
        boolean errorOccuried = false;

        Scanner inStream = new Scanner(System.in);
        String[] parsedCommands;
        String[] parsedArguments;
        while(!ended){
            if (inStream.hasNextLine())
                parsedCommands = inStream.nextLine().split(";|\n");
            else
                continue;
            for (String oneCommand: parsedCommands){
                parsedArguments = oneCommand.split("\\s+");
                if (parsedArguments[0].equals("exit")){
                    ended = true;
                    break;
                }
                Command commandToExecute = shellCommands.get(parsedArguments[0]);
                if (commandToExecute != null){
                    if (!commandToExecute.run(parsedArguments)){
                        errorOccuried = true;
                    }
                }
                else{
                    System.out.println(parsedArguments[0] + ": command not found");
                }
            }
            if (!ended)
                System.out.print("$ ");
        }
        return  !errorOccuried;
    }

    public boolean packetMode(String[] arguments) {

        String[] parsedCommands;
        String[] parsedArguments;
        String commandLine = arguments[0];
        boolean errorOccuried = false;

        for (int i = 1; i < arguments.length; ++i) {
            commandLine = commandLine + " " + arguments[i];
        }

       //System.out.println(commandLine);
        parsedCommands = commandLine.split(";|\n");
        for (String oneCommand : parsedCommands) {
            parsedArguments = oneCommand.trim().split("\\s+");
            if (parsedArguments[0].equals("exit")) {
                return true;
            }
            Command commandToExecute = shellCommands.get(parsedArguments[0]);
            if (commandToExecute != null) {
                if (!commandToExecute.run(parsedArguments)) {
                    errorOccuried = true;
                }
            } else {
                System.out.println(parsedArguments[0] + ": command not found");
            }
        }
        return !errorOccuried;
    }
}