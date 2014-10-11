package ru.fizteh.fivt.students.dnovikov.filemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

public class FileMap {
    private DataBaseConnector dbConnector = null;

    public static void main(String[] args) {
        FileMap fileMap = new FileMap();
        try {
            fileMap.run(args);
        } catch (IOException ioEx) {
            System.err.println(ioEx.getMessage());
        } catch (NullPointerException nulPtEx) {
            System.err.println(nulPtEx.getMessage());
            System.exit(1);
        }
    }

    public void run(String[] args) throws IOException, NullPointerException {
        dbConnector = new DataBaseConnector();
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
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
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
                System.out.print("$ ");
            } while (true);
        }
    }

    private void batchMode(String[] str) {
        String commands = new String();
        for (String it : str) {
            it += " ";
            commands += it;
        }
        try {
            runCommands(commands);
            exitCommand();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                dbConnector.saveTable();
            } catch (IOException saveErr) {
                System.out.println(saveErr.getMessage());
            }
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    private void runCommands(String str) throws IOException, IllegalArgumentException {
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
                        throw new IllegalArgumentException(cmd + ": too much arguments");
                    }
                    exitCommand();
                    break;
                default:
                    throw new IOException(cmd + ": no such command");
            }
        }
    }

    private void exitCommand() throws IOException {
        dbConnector.saveTable();
        System.exit(0);
    }

    private void putCommand(ArrayList<String> cmds) throws IllegalArgumentException {
        if (cmds.size() != 3) {
            throw new IllegalArgumentException(cmds.get(0) + ": wrong number of arguments");
        }
        String oldValue = dbConnector.getState().put(cmds.get(1), cmds.get(2));
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }

    private void getCommand(ArrayList<String> cmds) throws IllegalArgumentException {
        if (cmds.size() != 2) {
            throw new IllegalArgumentException(cmds.get(0) + ": wrong nubmer of arguments");
        }
        String value = dbConnector.getState().get(cmds.get(1));
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }

    private void listCommand(ArrayList<String> cmds) throws IllegalArgumentException {
        if (cmds.size() != 1) {
            throw new IllegalArgumentException(cmds.get(0) + ": wrong number of arguments");
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

    private void removeCommand(ArrayList<String> cmds) throws IllegalArgumentException {
        if (cmds.size() != 2) {
            throw new IllegalArgumentException(cmds.get(0) + ": wrong number of arguments");
        }
        String deleted = dbConnector.getState().remove(cmds.get(1));

        if (deleted == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
