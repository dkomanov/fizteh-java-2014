package ru.fizteh.fivt.students.ryad0m.multifile;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Shell {

    private Table table;

    private void runCommand(String[] args) throws ShellException, IOException, BadFormatException {
        if (args[0].equals("get")) {
            get(args);
        } else if (args[0].equals("put")) {
            put(args);
        } else if (args[0].equals("list")) {
            list(args);
        } else if (args[0].equals("remove")) {
            remove(args);
        } else if (args[0].equals("show")) {
            show(args);
        } else if (args[0].equals("drop")) {
            dropTable(args);
        } else if (args[0].equals("create")) {
            createTable(args);
        } else if (args[0].equals("use")) {
            selectTable(args);
        } else if (args[0].equals("exit")) {
            exit(args);
        } else {
            throw new ShellException();
        }
        if (!args[0].equals("exit")) {
            Main.database.save();
        }
    }

    private void get(String[] args) throws ShellException {
        if (args.length != 2) {
            throw new ShellException();
        } else if (table == null) {
            System.out.println("No table");
        } else {
            if (table.containKey(args[1])) {
                System.out.println("found");
                System.out.println(table.get(args[1]));
            } else {
                System.out.println("not found");
            }
        }
    }

    private void put(String[] args) throws ShellException {
        if (args.length != 3) {
            throw new ShellException();
        } else if (table == null) {
            System.out.println("No table");
        } else {
            if (table.containKey(args[1])) {
                System.out.println("overwrite");
                System.out.println("old " + table.get(args[1]));
            } else {
                System.out.println("new");
            }
            table.put(args[1], args[2]);
        }
    }

    private void list(String[] args) throws ShellException {
        if (args.length != 1) {
            throw new ShellException();
        } else if (table == null) {
            System.out.println("No table");
        } else {
            String[] keys = table.getKeys();
            for (int i = 0; i + 1 < keys.length; ++i) {
                System.out.print(keys[i] + ", ");
            }
            if (keys.length > 0) {
                System.out.println(keys[keys.length - 1]);
            }

        }
    }

    private void remove(String[] args) throws ShellException {
        if (args.length != 2) {
            throw new ShellException();
        } else if (table == null) {
            System.out.println("No table");
        } else {
            if (table.containKey(args[1])) {
                System.out.println("removed");
                table.remove(args[1]);
            } else {
                System.out.println("not found");
            }
        }
    }

    private void selectTable(String[] args) throws ShellException {
        if (args.length != 2) {
            throw new ShellException();
        } else {
            if (Main.database.containTable(args[1])) {
                System.out.println("using " + args[1]);
                table = Main.database.getTable(args[1]);
            } else {
                System.out.println(args[1] + " not exist");
            }
        }
    }

    private void createTable(String[] args) throws ShellException, IOException, BadFormatException {
        if (args.length != 2) {
            throw new ShellException();
        } else {
            if (Main.database.containTable(args[1])) {
                System.out.println(args[1] + " exists");
            } else {
                System.out.println("created");
                Main.database.createTable(args[1]);
            }
        }
    }

    private void dropTable(String[] args) throws ShellException, IOException, BadFormatException {
        if (args.length != 2) {
            throw new ShellException();
        } else {
            if (Main.database.containTable(args[1])) {
                System.out.println("dropped");
                Main.database.dropTable(args[1]);
            } else {
                System.out.println(args[1] + " not exists");
            }
        }
    }

    private void show(String[] args) throws ShellException {
        if (args.length != 2) {
            throw new ShellException();
        } else if (args[1].equals("tables")) {
            for (Map.Entry<String, Table> entry : Main.database.getTables()) {
                System.out.print(entry.getKey() + " ");
                System.out.println(entry.getValue().getSize());
            }
        } else {
            System.out.println("unrecognized option");
        }
    }

    private void exit(String[] args) throws ShellException {
        if (args.length > 1) {
            throw new ShellException();
        }
        System.exit(0);
    }

    private void parseLine(String line) throws ShellException, IOException, BadFormatException {
        String[] commands = line.split(";");
        for (String someCommand : commands) {
            String command = someCommand.trim().replaceAll("\\s+", " ");
            if (!command.equals("")) {
                runCommand(command.split(" "));
            }
        }
    }

    private void interactiveMode() throws IOException, BadFormatException {
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

    private void notInteractiveMode(String[] args) throws ShellException, IOException, BadFormatException {
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
        } catch (BadFormatException e) {
            System.out.println("Format error");
            System.exit(1);
        }

    }
}
