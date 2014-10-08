package ru.fizteh.fivt.students.dnovikov.filemap;

import java.util.*;

public class FileMap {
    private DataBaseConnector dbConnector = null;

    public static void main(String[] args) {
        FileMap fileMap = new FileMap();
        try {
            fileMap.run(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void run(String[] args) throws Exception {
        dbConnector = new DataBaseConnector();
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }

    private void interactiveMode() {
        String str;
        System.out.print("$ ");
        try (Scanner scanner = new Scanner(System.in)) {
            do {
                str = scanner.nextLine();
                try {
                    runCommands(str);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                System.out.print("$ ");
            } while (true);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());

        }
    }

    private void packageMode(String[] str) {
        String commands = new String();
        for (String it : str) {
            it += " ";
            commands += it;
        }
        try {
            runCommands(commands);
            exitCommand();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            try {
                dbConnector.saveTable();
            } catch (Exception saveErr) {
                System.out.println(saveErr.getMessage());
            }
            System.exit(1);
        }
    }

    private void runCommands(String str) throws Exception {
        ArrayList<ArrayList<String>> commands = new ArrayList<ArrayList<String>>();
        ArrayList<String> tempStr = new ArrayList<String>(Arrays.asList(str.trim().split(";")));

        for (String it : tempStr) {
            ArrayList<String> t = new ArrayList<String>(Arrays.asList(it.trim().split("\\s+")));
            commands.add(t);
        }

        for (ArrayList<String> it : commands) {
            String cmd = it.get(0);
            switch (cmd) {
                case "put":
                    putCommand(it);
                    break;
                case "get":
                    getCommand(it);
                    break;
                case "remove":
                    removeCommand(it);
                    break;
                case "list":
                    listCommand(it);
                    break;
                case "exit":
                    if (it.size() > 1) {
                        throw new Exception(cmd + ": too much arguments");
                    }
                    exitCommand();
                    break;
                default:
                    throw new Exception(cmd + ": no such command");
            }
        }
    }

    private void exitCommand() throws Exception {
        dbConnector.saveTable();
        System.exit(0);
    }

    private void putCommand(ArrayList<String> cmds) throws Exception {
        if (cmds.size() != 3) {
            throw new Exception(cmds.get(0) + ": wrong number of arguments");
        }
        String oldValue = dbConnector.getState().put(cmds.get(1), cmds.get(2));
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }

    private void getCommand(ArrayList<String> cmds) throws Exception {
        if (cmds.size() != 2) {
            throw new Exception(cmds.get(0) + ": wrong nubmer of arguments");
        }
        String value = dbConnector.getState().get(cmds.get(1));
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println(value);
            System.out.println("found");
        }
    }

    private void listCommand(ArrayList<String> cmds) throws Exception {
        if (cmds.size() != 1) {
            throw new Exception(cmds.get(0) + ": wrong number of arguments");
        }
        Set<String> keys = dbConnector.getState().list();
        int num = 0;
        for (String key : keys) {
            if (num != 0) {
                System.out.print(", ");
            }
            System.out.print(key);
            ++num;
        }
        System.out.println();
    }

    private void removeCommand(ArrayList<String> cmds) throws Exception {
        if (cmds.size() != 2) {
            throw new Exception(cmds.get(0) + ": wrong number of arguments");
        }
        String deleted = dbConnector.getState().remove(cmds.get(1));

        if (deleted == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
