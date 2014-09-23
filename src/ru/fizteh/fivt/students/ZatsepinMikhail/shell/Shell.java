package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

/**
 * @author test
 */

import java.util.ArrayList;
import java.util.Scanner;

public class Shell{
    ArrayList<Command> arrayOfCommands;
    public Shell(){
        arrayOfCommands = new ArrayList<Command>();
    }
    void addCommand(Command newCommand){
        arrayOfCommands.add(newCommand);
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

                boolean rightCommand = false;
                for (Command shellCommand: arrayOfCommands){
                    if (shellCommand.getName().equals(parsedArguments[0])) {
                        rightCommand = true;
                        if (!shellCommand.run(parsedArguments))
                            errorOccuried = true;
                        break;
                    }
                }
                if (!rightCommand){
                    System.out.println(parsedArguments[0] + ": command not found");
                }
            }
            if (!ended)
                System.out.print("$ ");
        }
        if (errorOccuried)
            return true;
        else
            return false;
    }
    public void packetMode(String[] arguments){
        System.out.println("In packet mode now");
    }
}