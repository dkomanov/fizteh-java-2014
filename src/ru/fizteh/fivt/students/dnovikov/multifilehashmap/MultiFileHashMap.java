package ru.fizteh.fivt.students.dnovikov.multifilehashmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MultiFileHashMap {
    private DataBaseConnector dbConnector = null;

    public static void main(String[] args) {
        MultiFileHashMap fileMap = new MultiFileHashMap();
        try {
            fileMap.run(args);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (LoadOrSaveException e) {
            System.err.println(e.getMessage());
        }
    }

    public void run(String[] args) throws LoadOrSaveException, NullPointerException {
        dbConnector = new DataBaseConnector();
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }

    private void interactiveMode() {
        System.out.print("$ ");
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String str = scanner.nextLine();
                try {
                    runCommands(str);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } catch (WrongNumberOfArgumentsException e) {
                    System.err.println(e.getMessage());
                } catch (LoadOrSaveException e) {
                    System.err.println(e.getMessage());
                }
                System.out.print("$ ");
            }
        }
    }

    private void batchMode(String[] str) {
        String commands = new String();
        for (String it : str) {
            commands += it + " ";
        }
        try {
            runCommands(commands);
            exitCommand();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                dbConnector.saveTable();
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
            } catch (LoadOrSaveException exception) {
                System.err.println(exception.getMessage());
            }
            System.exit(1);
        } catch (WrongNumberOfArgumentsException e) {
            System.err.println(e.getMessage());
            try {
                dbConnector.saveTable();
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
            } catch (LoadOrSaveException exception) {
                System.err.println(exception.getMessage());
            }
            System.exit(1);
        } catch (LoadOrSaveException e) {
            e.getMessage();
        }
    }

    private void runCommands(String str) throws IOException, WrongNumberOfArgumentsException, LoadOrSaveException {
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
                    exitCommand(it);
                    break;
                case "create":
                    createCommand(it);
                    break;
                case "drop":
                    dropCommand(it);
                    break;
                case "use":
                    useCommand(it);
                    break;
                case "show":
                    showCommand(it);
                    break;
                default:
                    throw new IOException(cmd + ": no such command");
            }
        }
    }

    private void showCommand(ArrayList<String> cmds) throws IOException {
        if (cmds.size() != 2) {
            throw new WrongNumberOfArgumentsException("show tables");
        }
        if (!cmds.get(1).equals("tables")) {
            throw new IOException(cmds.get(0) + ' ' + cmds.get(1) + ": no such command");
        }
        dbConnector.showTable();
    }

    private void useCommand(ArrayList<String> cmds) throws IOException, LoadOrSaveException {
        if (cmds.size() != 2) {
            throw new WrongNumberOfArgumentsException(cmds.get(0));
        }
        dbConnector.useTable(cmds.get(1));
    }

    private void dropCommand(ArrayList<String> cmds) throws IOException, LoadOrSaveException {
        if (cmds.size() != 2) {
            throw new WrongNumberOfArgumentsException(cmds.get(0));
        }
        dbConnector.removeTable(cmds.get(1));
    }

    private void createCommand(ArrayList<String> cmds) {
        if (cmds.size() != 2) {
            throw new WrongNumberOfArgumentsException(cmds.get(0));
        }
        try {
            dbConnector.createTable(cmds.get(1));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (LoadOrSaveException e) {
            System.err.println(e.getMessage());
        }
    }

    private void exitCommand(ArrayList<String> cmds) throws IOException, WrongNumberOfArgumentsException, LoadOrSaveException {
        if (cmds.size() > 1) {
            throw new WrongNumberOfArgumentsException(cmds.get(0));
        }
        dbConnector.saveTable();
        System.exit(0);
    }

    private void exitCommand() throws IOException, LoadOrSaveException {
        dbConnector.saveTable();
        System.exit(0);
    }

    private void putCommand(ArrayList<String> cmds) throws WrongNumberOfArgumentsException, IOException {
        if (cmds.size() != 3) {
            throw new WrongNumberOfArgumentsException(cmds.get(0));
        }
        String key = new String(cmds.get(1));
        String value = new String(cmds.get(2));

        if (dbConnector.getCurrentTable() == null) {
            System.out.println("no table");
        } else {
            dbConnector.getCurrentTable().put(key, value);
        }
    }

    private void getCommand(ArrayList<String> cmds) throws WrongNumberOfArgumentsException {
        if (cmds.size() != 2) {
            throw new WrongNumberOfArgumentsException(cmds.get(0));
        }
        String key = new String(cmds.get(1));
        if (dbConnector.getCurrentTable() == null) {
            System.out.println("no table");
        } else {
            dbConnector.getCurrentTable().get(key);
        }
    }

    private void listCommand(ArrayList<String> cmds) throws WrongNumberOfArgumentsException {
        if (cmds.size() != 1) {
            throw new WrongNumberOfArgumentsException(cmds.get(0));
        }
        if (dbConnector.getCurrentTable() == null) {
            System.out.println("no table");
        } else {
            dbConnector.getCurrentTable().list();
        }
    }

    private void removeCommand(ArrayList<String> cmds) throws WrongNumberOfArgumentsException {
        if (cmds.size() != 2) {
            throw new WrongNumberOfArgumentsException(cmds.get(0));
        }
        String key = new String(cmds.get(1));
        if (dbConnector.getCurrentTable() == null) {
            System.out.println("no table");
        } else {
            dbConnector.getCurrentTable().remove(key);
        }
    }
}
