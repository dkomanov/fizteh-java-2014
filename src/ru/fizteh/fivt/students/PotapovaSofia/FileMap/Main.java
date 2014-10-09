package ru.fizteh.fivt.students.PoatpovaSofia.FileMap;

import java.util.Scanner;

public class Main {
    private static String dbPath;
    private static DataBase db;

    public static void main(String[] args) throws Exception {
        try {
            dbPath = System.getProperty("db.file");
            db = new DataBase(dbPath);
            if (args.length == 0) {
                interMode();
            } else {
                packMode(args);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
    public static void interMode() throws Exception {
        try (Scanner in = new Scanner(System.in)) {
            System.out.print("$ ");
            while (in.hasNextLine()) {
                String str = in.nextLine();
                String[] cmds = str.trim().split(";");
                for (String cmd : cmds) {
                    try {
                        commandParse(cmd);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                System.out.print("$ ");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println();
    }
    public static void packMode(String[] args) throws Exception {
        StringBuilder cmd = new StringBuilder();
        for (String a : args) {
            cmd.append(a);
            cmd.append(' ');
        }
        String[] cmds = cmd.toString().trim().split(";");
        for (String c : cmds) {
            try {
                commandParse(c);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
    public static void commandParse(String cmd) throws Exception {
        String[] runningCmd = cmd.trim().split("\\s+");
        if (runningCmd[0].equals("put")) {
            try {
                if (runningCmd.length > 3) {
                    throw new Exception("put: too much arguments");
                } else if (runningCmd.length < 3) {
                    throw new Exception("put: few arguements");
                } else {
                    Command.put(db, runningCmd[1], runningCmd[2]);
                }
            } catch (IndexOutOfBoundsException e) {
                throw new Exception("usage: put <key> <value>");
            }
        } else if (runningCmd[0].equals("get")) {
            try {
                if (runningCmd.length > 2) {
                    throw new Exception("get: too much arguments");
                } else if (runningCmd.length < 2) {
                    throw new Exception("get: few arguements");
                } else {
                    Command.get(db, runningCmd[1]);
                }
            } catch (IndexOutOfBoundsException e) {
                throw new Exception("usage: get <key>");
            }
        } else if (runningCmd[0].equals("remove")) {
            try {
                if (runningCmd.length > 2) {
                    throw new Exception("remove: too much arguments");
                } else if (runningCmd.length < 2) {
                    throw new Exception("remove: few arguements");
                } else {
                    Command.remove(db, runningCmd[1]);
                }
            } catch (IndexOutOfBoundsException e) {
                throw new Exception("usage: remove <key>");
            }
        } else if (runningCmd[0].equals("list")) {
            if (runningCmd.length > 1) {
                throw new Exception("list: too much arguments");
            } else {
                Command.list(db);
            }
        } else if (runningCmd[0].equals("exit")) {
            if (runningCmd.length > 1) {
                throw new Exception("exit: too much arguments");
            }
            System.out.println("exit");
            System.exit(0);
        } else {
            throw new Exception(runningCmd[0] + ": unknown command");
        }
    }
}
