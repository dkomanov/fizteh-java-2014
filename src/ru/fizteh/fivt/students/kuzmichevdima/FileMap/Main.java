package ru.fizteh.fivt.students.kuzmichevdima.FileMap;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
public class Main {
    private static String dbPath;
    private static DB db;
    public static void main(String[] args) throws Exception {
        dbPath = System.getProperty("db.file");
        db = new DB(dbPath);
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }
    public static void interactiveMode() {
        System.out.print("$ ");
        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String str = in.nextLine();
            String[] commands = str.trim().split(";");
            for (String command : commands) {
                try {
                    commandParse(command);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            System.out.print("$ ");
        }
    }
    public static void packageMode(String[] args) {
        StringBuilder command = new StringBuilder();
        for (String a : args) {
            command.append(a);
            command.append(' ');
        }
        String[] commands = command.toString().trim().split(";");
        for (String cmd : commands) {
            try {
                commandParse(cmd);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
    public static void commandParse(String command) throws IOException {
        String[] runningCmd = command.trim().split("\\s+");
        if (runningCmd[0].equals("put")) {
            if (runningCmd.length > 3) {
                throwTooManyArgs("put");
            } else if (runningCmd.length < 3) {
                throwTooFewArgs("put");
            } else {
                put(db, runningCmd[1], runningCmd[2]);
            }
        } else if (runningCmd[0].equals("get")) {
            if (runningCmd.length > 2) {
                throwTooManyArgs("get");
            } else if (runningCmd.length < 2) {
                throwTooFewArgs("get");
            } else {
                get(db, runningCmd[1]);
            }
        } else if (runningCmd[0].equals("remove")) {
            if (runningCmd.length > 2) {
                throwTooManyArgs("remove");
            } else if (runningCmd.length < 2) {
                throwTooFewArgs("remove");
            } else {
                remove(db, runningCmd[1]);
            }
        } else if (runningCmd[0].equals("list")) {
            if (runningCmd.length > 1) {
                throwTooManyArgs("list");
            } else {
                list(db);
            }
        } else if (runningCmd[0].equals("exit")) {
            if (runningCmd.length > 1) {
                throwTooManyArgs("exit");
            }
            System.out.println("exit");
            System.exit(0);
        } else {
            throw new IllegalArgumentException(runningCmd[0] + ": unknown command");
        }
    }
    public static void put(DB db, String key, String value) throws IOException {
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
    public static void get(DB db, String key) {
        if (db.getDataBase().containsKey(key)) {
            System.out.println("found");
            System.out.println(db.getDataBase().get(key));
        } else {
            System.out.println("not found");
        }
    }
    public static void remove(DB db, String key) throws IOException {
        if (db.getDataBase().containsKey(key)) {
            db.getDataBase().remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        db.writeDataToFile();
    }
    public static void list(DB db) {
        Set keySet = db.getDataBase().keySet();
        Object [] array = keySet.toArray();
        String list = "";
        for (int j = 0; j < array.length; j++) {
            list += array[j].toString();
            if (j != array.length - 1) {
                list += ", ";
            }
        }
        System.out.println(list);
    }
    private static void throwTooManyArgs(String command) throws IllegalArgumentException {
        throw new IllegalArgumentException(command + ": too many arguments");
    }
    private static void throwTooFewArgs(String command) throws IllegalArgumentException {
        throw new IllegalArgumentException(command + ": too few arguments");
    }
}
