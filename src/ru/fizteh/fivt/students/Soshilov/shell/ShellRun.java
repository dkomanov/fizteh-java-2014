package ru.fizteh.fivt.students.Soshilov.shell;

import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 02 October 2014
 * Time: 19:33
 */
public class ShellRun {
    /**
     * Check whether the command is correct
     * @param name The command that was entered
     * @return Bool: false if command is not from the list of needed ones
     */
    public Boolean checkName(final String name) {
        String[] s = {"ls", "pwd", "cd", "mkdir", "rm", "cp", "mv", "cat", "exit"};
        for (int i = 0; i < s.length; ++i) {
            if (name.equals(s[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param currentArgs Arguments, entered with command (operators, files)
     * @param currentDirectory Current directory where we are now
     */
    public void run(final String[] currentArgs, final CurrentDirectory currentDirectory) {
        switch (currentArgs[0]){
            case "cd":
                ChangeDirectory.changeDirectoryRun(currentArgs, currentDirectory);
                //ChangeDirectory;
                break;
            case "mkdir":
                Mkdir.mkdirRun(currentArgs, currentDirectory);
                break;
            case "rm":
                Remove.removeRun(currentArgs, currentDirectory);
                break;
            case "ls":
                ListDirectoryContents.listDirectoryContentsRun(currentDirectory);
                break;
            case "exit":
                Exit.exitRun();
                break;
            case "pwd":
                PrintName.printNameRun(currentDirectory);
                break;
            case "cat":
                Cat.catRun(currentArgs, currentDirectory);
                break;
            case "mv":
                Move.moveRun(currentArgs, currentDirectory);
                break;
            case "cp":
                Copy.copyRun(currentArgs, currentDirectory);
                break;
            default:
                System.err.println(currentArgs[0] + ": command not found");
                System.exit(1);
                break;
        }
    }

    public void interactiveMode() {
        CurrentDirectory currentDirectory = new CurrentDirectory();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(currentDirectory.getCurrentDirectory() + "$");
            String currentString = sc.nextLine();
            String[] functions = currentString.split(";");
            for (int i = 0; i < functions.length; ++i) {
                functions[i] = functions[i].trim(); //Deleting spaces
                run(functions[i].split("\\s+"), currentDirectory); //Division on space-kind elements
            }
        }
    }

    public void packageMode(final String[] args) {
        CurrentDirectory currentDirectory = new CurrentDirectory();
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s).append(" ");
        }
        String string = new String(builder);
        String[] commands = string.split(";|(\\s+)");
        int i = 0;
        while (i < commands.length) {
            int first = i;
            ++i;
            while (i < commands.length && !checkName(commands[i])) {
                ++i;
            }
            int size = 0;
            for (int j = 0; j < i - first; ++j) {
                if (commands[j + first].length() != 0) {
                    ++size;
                }
            }

            String[] s = new String[size];
            int tmpSize = 0;
            for (int j = 0; j < s.length; ++j) {
                if (commands[j + first].length() != 0) {
                    s[tmpSize] = commands[j + first];
                    ++tmpSize;
                }
            }
            run(s, currentDirectory);
        }
    }

    public ShellRun(final String[] args) {
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }
}
