package ru.fizteh.fivt.students.ryad0m.filemap;

import java.io.IOException;
import java.util.Scanner;

public class Shell {

    private void runCommand(String[] args) throws ShellException, IOException {
        if (args[0].equals("get")) {
            get(args);
        } else if (args[0].equals("put")) {
            put(args);
        } else if (args[0].equals("list")) {
            list(args);
        } else if (args[0].equals("remove")) {
            remove(args);
        } else if (args[0].equals("exit")) {
            exit(args);
        } else {
            throw new ShellException();
        }
        if (!args[0].equals("exit")) {
            Main.tableNode.save();
        }
    }

    private void get(String[] args) throws ShellException {
        if (args.length != 2) {
            throw new ShellException();
        } else {
            if (Main.tableNode.containKey(args[1])) {
                System.out.println("found");
                System.out.println(Main.tableNode.get(args[1]));
            } else {
                System.out.println("not found");
            }
        }
    }

    private void put(String[] args) throws ShellException {
        if (args.length != 3) {
            throw new ShellException();
        } else {
            if (Main.tableNode.containKey(args[1])) {
                System.out.println("overwrite");
                System.out.println("old " + Main.tableNode.get(args[1]));
            } else {
                System.out.println("new");
            }
            Main.tableNode.put(args[1], args[2]);
        }
    }

    private void list(String[] args) throws ShellException {
        if (args.length != 1) {
            throw new ShellException();
        } else {
            String[] keys = Main.tableNode.getKeys();
            for (int i = 0; i + 1 < keys.length; ++i)
                System.out.print(keys[i] + ", ");
            if (keys.length > 0)
                System.out.println(keys[keys.length - 1]);

        }
    }

    private void remove(String[] args) throws ShellException {
        if (args.length != 2) {
            throw new ShellException();
        } else {
            if (Main.tableNode.containKey(args[1])) {
                System.out.println("removed");
                Main.tableNode.remove(args[1]);
            } else {
                System.out.println("not found");
            }
        }
    }

    private void exit(String[] args) throws ShellException {
        if (args.length > 1) {
            throw new ShellException();
        }
        System.exit(0);
    }

    private void parseLine(String line) throws ShellException, IOException {
        String[] commands = line.split(";");
        for (String someCommand : commands) {
            String command = someCommand.trim().replaceAll("\\s+", " ");
            if (!command.equals("")) {
                runCommand(command.split(" "));
            }
        }
    }

    private void interactiveMode() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        System.out.flush();
        while (scanner.hasNextLine()) {
            try {
                parseLine(scanner.nextLine());
            } catch (ShellException e) {
                System.out.println("Command error");
            }
            System.out.print("$ ");
            System.out.flush();
        }
    }

    private void notInteractiveMode(String[] args) throws ShellException, IOException {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg);
            sb.append(' ');
        }
        parseLine(sb.toString());
    }

    public void start(String[] args) {
        try {
            if (args.length == 0) {
                interactiveMode();
            } else {
                notInteractiveMode(args);
            }
        } catch (ShellException e) {
            System.out.println("Command parse error");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("IO error");
            System.exit(1);
        }
    }
}
