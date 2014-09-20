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
    public void interactiveMode(){
        System.out.println("In interactive mode now");
        System.out.print("$ ");
        boolean ended = false;
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
                            System.out.println("ERROR occuried");
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
    }
    public void packetMode(String[] arguments){
        System.out.println("In packet mode now");
    }
}

/*public class Shell {
    private static int commandInterpreter(String[] parsingArguments){
        String command = parsingArguments[0];
        switch (command) {
            case "cd":
                break;
            case "mkdir":
                break;
            case "rm":
                break;
            case "cp":
                break;
            case "mv":
                break;
            case "ls":
                break;
            case "exit":
                break;
            case "cat":

                break;
            default:
                return 1;
        }
        return 0;
    }


    public static void main(String[] args) {
        if (args.length == 0)
        {
            String command;
            String[] parsingCommands;
            String[] parsingArguments;
            Scanner istream = new Scanner(System.in);
           // istream.useDelimiter(";|/n| ");
           // Pattern myPathern = Pattern.compile("");
            while(istream.hasNext()) {
                command = istream.next();
                parsingCommands = command.split(";");
                for (String commandInFor: parsingCommands)
                {
                    parsingArguments = commandInFor.split(" ");
                    commandInterpreter(parsingArguments);
                }

            }
            /*while(istream.hasNext())
            {
                System.out.println(istream.next());
            }
            System.out.print("$ ");
            istream.close();

        }
        else
        {

        }
        System.exit(0);
    }
}
*/