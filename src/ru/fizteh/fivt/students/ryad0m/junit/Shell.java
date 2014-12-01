package ru.fizteh.fivt.students.ryad0m.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Shell {

    private Table table;
    private TableProvider database;

    public Shell(TableProvider tableProvider) {
        database = tableProvider;
    }

    private void runCommand(String[] args) throws ShellException, IOException, BadFormatException {
        if (args[0].equals("get")) {
            get(args);
        } else if (args[0].equals("put")) {
            put(args);
        } else if (args[0].equals("list")) {
            list(args);
        } else if (args[0].equals("remove")) {
            remove(args);
        } else if (args[0].equals("drop")) {
            dropTable(args);
        } else if (args[0].equals("show")) {
            show(args);
        } else if (args[0].equals("create")) {
            createTable(args);
        } else if (args[0].equals("size")) {
            size(args);
        } else if (args[0].equals("commit")) {
            commit(args);
        } else if (args[0].equals("rollback")) {
            rollback(args);
        } else if (args[0].equals("use")) {
            selectTable(args);
        } else if (args[0].equals("exit")) {
            exit(args);
        } else {
            throw new ShellException();
        }
    }

    private void get(String[] args) throws ShellException {
        if (args.length != 2) {
            throw new ShellException();
        } else if (table == null) {
            System.out.println("No table");
        } else {
            String val = table.get(args[1]);
            if (val != null) {
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
            if (table.put(args[1], args[2]) != null) {
                System.out.println("overwrite");
                System.out.println("old " + table.get(args[1]));
            } else {
                System.out.println("new");
            }
        }
    }

    private void list(String[] args) throws ShellException {
        if (args.length != 1) {
            throw new ShellException();
        } else if (table == null) {
            System.out.println("No table");
        } else {
            List<String> keys = table.list();
            for (int i = 0; i + 1 < keys.size(); ++i) {
                System.out.print(keys.get(i) + ", ");
            }
            if (keys.size() > 0) {
                System.out.println(keys.get(keys.size() - 1));
            }

        }
    }

    private void remove(String[] args) throws ShellException {
        if (args.length != 2) {
            throw new ShellException();
        } else if (table == null) {
            System.out.println("No table");
        } else {
            if (table.remove(args[1]) != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }

    private void commit(String[] args) throws ShellException, IOException {
        if (args.length != 1) {
            throw new ShellException();
        } else if (table == null) {
            System.out.println("No table");
        } else {
            System.out.println(table.commit());
        }
    }

    private void size(String[] args) throws ShellException {
        if (args.length != 1) {
            throw new ShellException();
        } else if (table == null) {
            System.out.println("No table");
        } else {
            System.out.println(table.size());
        }
    }

    private void rollback(String[] args) throws ShellException {
        if (args.length != 1) {
            throw new ShellException();
        } else if (table == null) {
            System.out.println("No table");
        } else {
            System.out.println(table.rollback());
        }
    }

    private void selectTable(String[] args) throws ShellException {
        if (args.length != 2) {
            throw new ShellException();
        } else if (table != null && ((UserTable) table).unsavedSize() != 0) {
            System.out.print(((UserTable) table).unsavedSize());
            System.out.println(" unsaved changes");
        } else {
            table = database.getTable(args[1]);
            if (table != null) {
                System.out.println("using " + args[1]);
            } else {
                System.out.println(args[1] + " not exist");
            }
        }
    }

    private void createTable(String[] args) throws ShellException, IOException, BadFormatException {
        if (args.length != 2) {
            throw new ShellException();
        } else {
            if (database.createTable(args[1]) == null) {
                System.out.println(args[1] + " exists");
            } else {
                System.out.println("created");
                database.createTable(args[1]);
            }
        }
    }

    private void dropTable(String[] args) throws ShellException, IOException, BadFormatException {
        if (args.length != 2) {
            throw new ShellException();
        } else {
            try {
                database.removeTable(args[1]);
            } catch (IllegalStateException ex) {
                System.out.println(args[1] + " not exists");
                return;
            }
            System.out.println("dropped");
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

    private void show(String[] args) throws ShellException {
        if (args.length != 2) {
            throw new ShellException();
        } else if (args[1].equals("tables")) {
            for (Map.Entry<String, MyTable> entry : ((MyTableProvider) database).getTables()) {
                System.out.print(entry.getKey() + " ");
                System.out.println(entry.getValue().getSize());
            }
        } else {
            System.out.println("unrecognized option");
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
