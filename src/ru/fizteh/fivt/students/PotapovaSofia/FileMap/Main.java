package ru.fizteh.fivt.students.PoatpovaSofia.FileMap;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static String dbPath;
    private static DataBase db;

    public static void main(String[] args) throws Exception {
        dbPath = System.getProperty("db.file");
        db = new DataBase(dbPath);
        if (args.length == 0) {
            interMode();
        } else {
            batchMode(args);
        }
    }

    public static void interMode() {
        System.out.print("$ ");
        try (Scanner in = new Scanner(System.in)) {
            while (in.hasNextLine()) {
                String str = in.nextLine();
                String[] cmds = str.trim().split(";");
                for (String cmd : cmds) {
                    try {
                        commandParse(cmd);
                    } catch (IOException e) {
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

    public static void batchMode(String[] args) {
        StringBuilder cmd = new StringBuilder();
        for (String a : args) {
            cmd.append(a);
            cmd.append(' ');
        }
        String[] cmds = cmd.toString().trim().split(";");
        for (String c : cmds) {
            try {
                commandParse(c);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public static void commandParse(String cmd) throws IOException {
        String[] runningCmd = cmd.trim().split("\\s+");
        if (runningCmd[0].equals("put")) {
            if (runningCmd.length > 3) {
                tooMuchArgs("put");
            } else if (runningCmd.length < 3) {
                fewArgs("put");
            } else {
                put(db, runningCmd[1], runningCmd[2]);
            }
        } else if (runningCmd[0].equals("get")) {
            if (runningCmd.length > 2) {
                tooMuchArgs("get");
            } else if (runningCmd.length < 2) {
                fewArgs("get");
            } else {
                get(db, runningCmd[1]);
            }
        } else if (runningCmd[0].equals("remove")) {
            if (runningCmd.length > 2) {
                tooMuchArgs("remove");
            } else if (runningCmd.length < 2) {
                fewArgs("remove");
            } else {
                remove(db, runningCmd[1]);
            }
        } else if (runningCmd[0].equals("list")) {
            if (runningCmd.length > 1) {
                tooMuchArgs("list");
            } else {
                list(db);
            }
        } else if (runningCmd[0].equals("exit")) {
            if (runningCmd.length > 1) {
                tooMuchArgs("exit");
            }
            System.out.println("exit");
            System.exit(0);
        } else {
            throw new IllegalArgumentException(runningCmd[0] + ": unknown command");
        }
    }

    public static void put(DataBase db, String key, String value) throws IOException{
        if (!db.getDataBase().containsKey(key)) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(db.getDataBase().get(key));
            db.getDataBase().remove(key);
        }
        db.getDataBase().put(key, value);
        db.writeDataToFile();
    }

    public static void get(DataBase db, String key) {
        if (db.getDataBase().containsKey(key)) {
            System.out.println("found");
            System.out.println(db.getDataBase().get(key));
        } else {
            System.out.println("not found");
        }
    }

    public static void remove(DataBase db, String key) throws IOException {
        if (db.getDataBase().containsKey(key)) {
            db.getDataBase().remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        db.writeDataToFile();
    }

    public static void list(DataBase db) {
        Set keySet = db.getDataBase().keySet();
        String joined = String.join(", ", keySet);
        System.out.println(joined);
    }

    private static void tooMuchArgs(String cmd) throws IllegalArgumentException {
        throw new IllegalArgumentException(cmd + ": too much arguments");
    }

    private static void fewArgs(String cmd) throws IllegalArgumentException {
        throw new IllegalArgumentException(cmd + ": few arguments");
    }
}
