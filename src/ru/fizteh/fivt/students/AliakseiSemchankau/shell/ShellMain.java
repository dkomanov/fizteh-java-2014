package ru.fizteh.fivt.students.AliakseiSemchankau.shell;//package ru.fizteh.fivt.students.test.shell;

/**
 * @author test
 */
public class ShellMain {

    public static void main(String[] args) {


        CommandLine commandLine = new CommandLine();

        if (args.length > 0) {
            commandLine.readCommands(args);
            commandLine.doCommands();
        } else {
            while (!commandLine.isExit()) {
                System.out.print(commandLine.currentDirectory);
                System.out.print("$ ");
                try {
                    commandLine.readCommands(null);
                    commandLine.doCommands();
                } catch (ShellException se) {
                    System.out.println(se.getMessage());
                }
            }
        }


    }
}




