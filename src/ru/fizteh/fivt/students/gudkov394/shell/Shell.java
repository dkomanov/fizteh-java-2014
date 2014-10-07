package ru.fizteh.fivt.students.gudkov394.shell;

import java.util.Scanner;

public class Shell {
    public Boolean checkName(final String name) {
        String[] s = {"ls", "pwd", "cd", "mkdir", "rm", "cp", "mv", "cat", "exit"};
        for (int i = 0; i < s.length; ++i) {
            if (name.equals(s[i])) {
                return true;
            }
        }
        return false;
    }

    public void run(final String[] currentArgs, final CurrentDirectory cd) {
        if (currentArgs[0].equals("pwd")) {
            Pwd pwd = new Pwd(currentArgs, cd);
        } else if ("mkdir".equals(currentArgs[0])) {
            Mkdir mkdir = new Mkdir(currentArgs, cd);
        } else if ("cd".equals(currentArgs[0])) {
            ChangeDirectory changeDirectory = new ChangeDirectory(currentArgs, cd);
        } else if ("rm".equals(currentArgs[0])) {
            RemoveDirectory removeDerictory = new RemoveDirectory(currentArgs, cd);
        } else if ("cp".equals(currentArgs[0])) {
            Copy copy = new Copy(currentArgs, cd);
        } else if ("mv".equals(currentArgs[0])) {
            MoveFile moveFile = new MoveFile(currentArgs, cd);
        } else if ("ls".equals(currentArgs[0])) {
            Ls ls = new Ls(currentArgs, cd);
        } else if ("exit".equals(currentArgs[0])) {
            Exit exit = new Exit(currentArgs);
        } else if ("cat".equals(currentArgs[0])) {
            Cat cat = new Cat(currentArgs, cd);

        } else {
            System.err.println("wrong command");
            System.exit(22);
        }
    }

    public void interactive() {
        CurrentDirectory currentDirectory = new CurrentDirectory();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(currentDirectory.getCurrentDirectory() + "$");
            String currentString = null;
            if (sc.hasNextLine()) {
                currentString = sc.nextLine();
            }
            currentString = currentString.replaceAll("\\s*;\\s*", ";");
            String[] functions = currentString.split(";");
            for (int i = 0; i < functions.length; ++i) {
                functions[i] = functions[i].trim();
                run(functions[i].split("\\s+"), currentDirectory);
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
        string = string.trim();
        string = string.replaceAll("\\s*;\\s*", ";");
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
        System.exit(0);
    }


    public Shell(final String[] args) {
        if (args.length == 0) {
            interactive();
        } else {
            packageMode(args);
        }
    }
}
