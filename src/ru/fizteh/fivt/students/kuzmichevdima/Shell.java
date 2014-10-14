/**
 * Created by kuzmi_000 on 14.10.14.
 */

package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

import java.util.Scanner;

public class Shell {
    private static final int WRONG_COMMAND_EXIT_CODE = 2;

    private void runCommand(final String[] args, final CurrentDir dir) {
        if (args[0].equals("cd")) {
            new ChangeDir(args, dir);
        } else if (args[0].equals("mkdir")) {
            new MakeDir(args, dir);
        } else if (args[0].equals("pwd")) {
            new Pwd(args, dir);
        } else if (args[0].equals("rm")) {
            new Remove(args, dir);
        } else if (args[0].equals("cp")) {
            new Copy(args, dir);
        } else if (args[0].equals("mv")) {
            new Move(args, dir);
        } else if (args[0].equals("ls")) {
            new List(args, dir);
        } else if (args[0].equals("exit")) {
            new Exit(args);
        } else if (args[0].equals("cat")) {
            new Cat(args, dir);
        } else {
            System.err.println("there is no such command:" + args[0]);
            System.exit(WRONG_COMMAND_EXIT_CODE);
        }
    }

    private void packageMode(final String[] args) {
        CurrentDir currentDir = new CurrentDir();
        String str = "";
        for (String argsStr : args) {
            str = str + " " + argsStr;
        }
        str = str.trim();
        //System.err.println("package mode str = " + str);
        String[] commands = str.split(";");
        for (int i = 0; i < commands.length; i++) {
            commands[i] = commands[i].trim();
            //System.err.println("package mode i = " + i + " (" + commands[i]);
            String [] tmp = commands[i].split("\\s+");
            int size = 0;
            for (int j = 0; j < tmp.length; j++) {
                if (!tmp[j].equals("")) {
                    size++;
                }
            }
            //System.err.println("size = " + size);
            String [] commandParts = new String [size];
            int cnt = 0;
            for (int j = 0; j < tmp.length; j++) {
                if (!tmp[j].equals("")) {
                    commandParts[cnt] = tmp[j];
                    cnt++;
                }
            }
            runCommand(commandParts, currentDir);
        }

    }

    private void interactiveMode() {
        CurrentDir currentDir = new CurrentDir();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(currentDir.getCurrentDir() + "$");
            String str;
            if (scanner.hasNextLine()) {
                str = scanner.nextLine();
            } else {
                break;
            }
            String[] commands = str.split(";");
            for (int i = 0; i < commands.length; i++) {
                if (!commands[i].isEmpty()) {
                    commands[i] = commands[i].trim();
                    String [] commandParts = commands[i].split("\\s+");
                    runCommand(commandParts, currentDir);
                }
            }
        }
    }

    public Shell(final String[] args) {
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }
}
