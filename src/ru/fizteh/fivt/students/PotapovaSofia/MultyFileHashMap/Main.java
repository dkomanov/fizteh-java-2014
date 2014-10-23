package ru.fizteh.fivt.students.PoatpovaSofia.MultyFileHashMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static String fmPath;
    private static MultyFileHashMap multyFileHashMap;
    private static String currentTable = null;
    private static Map<String, Integer> tableNames = null;
    private static Map<String, String>[][] db = null;

    public static void main(String[] args) throws IOException {
        fmPath = System.getProperty("fizteh.db.dir");
        if (fmPath == null) {
            System.err.println("Your directory is null");
            System.exit(1);
        }
        MultyFileHashMap multyFileHashMap = new MultyFileHashMap(fmPath);
        tableNames = multyFileHashMap.tableNames;
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }

    public static void interactiveMode() {
        System.out.print("$ ");
        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String str = in.nextLine();
            String[] cmds = str.trim().split(";");
            for (String cmd : cmds) {
                try {
                    commandParse(cmd);
                } catch (IOException | IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
            }
            System.out.print("$ ");
        }
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
            } catch (IOException | IllegalArgumentException e) {
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
                put(runningCmd[1], runningCmd[2]);
            }
        } else if (runningCmd[0].equals("get")) {
            if (runningCmd.length > 2) {
                tooMuchArgs("get");
            } else if (runningCmd.length < 2) {
                fewArgs("get");
            } else {
                get(runningCmd[1]);
            }
        } else if (runningCmd[0].equals("remove")) {
            if (runningCmd.length > 2) {
                tooMuchArgs("remove");
            } else if (runningCmd.length < 2) {
                fewArgs("remove");
            } else {
                remove(runningCmd[1]);
            }
        } else if (runningCmd[0].equals("list")) {
            if (runningCmd.length > 1) {
                tooMuchArgs("list");
            } else {
                list();
            }
        } else if (runningCmd[0].equals("exit")) {
            if (runningCmd.length > 1) {
                tooMuchArgs("exit");
            }
            System.out.println("exit");
            System.exit(0);
        } else if (runningCmd[0].equals("create")) {
            if (runningCmd.length > 2) {
                tooMuchArgs("create");
            } else if (runningCmd.length < 2) {
                fewArgs("create");
            } else {
                create(runningCmd[1]);
            }
        } else if (runningCmd[0].equals("drop")) {
            if (runningCmd.length > 2) {
                tooMuchArgs("drop");
            } else if (runningCmd.length < 2) {
                fewArgs("drop");
            } else {
                drop(runningCmd[1]);
            }
        } else if (runningCmd[0].equals("use")) {
            if (runningCmd.length > 2) {
                tooMuchArgs("use");
            } else if (runningCmd.length < 2) {
                fewArgs("use");
            } else {
                use(runningCmd[1]);
            }
        } else if (runningCmd[0].equals("show")) {
            if (runningCmd.length > 2) {
                tooMuchArgs("show tables");
            } else if (runningCmd.length < 2) {
                fewArgs("show tables");
            } else {
                if(runningCmd[1].equals("tables")) {
                    show();
                } else {
                    throw new IllegalArgumentException(runningCmd[1] + ": unknown command");
                }
            }
        } else {
            throw new IllegalArgumentException(runningCmd[0] + ": unknown command");
        }
        writeData();
    }

    private static boolean checkTable(String name) {
        File dircheck = new File(fmPath + File.separator + name);
        if (!dircheck.exists() || !dircheck.isDirectory()) {
            return false;
        }
        for (int i = 0; i < 16; i++) {
            File subdircheck = new File(dircheck.getAbsolutePath() + File.separator + Integer.toString(i) + ".dir");
            if (!subdircheck.exists() || !subdircheck.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    private static boolean writeData() throws IOException {
        if (currentTable == null) {
            return false;
        }
        if (db == null) {
            return true;
        }
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                MultyFileHashMap.writeDataToFile(db[i][j], fmPath + File.separator + currentTable + File.separator
                        + Integer.toString(i) + ".dir" + File.separator + Integer.toString(j) + ".dat");
            }
        }
        return true;
    }

    private static boolean readData() throws IOException {
        db = new HashMap[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                db[i][j] = MultyFileHashMap.readDataFromFile(fmPath + File.separator + currentTable + File.separator
                        + Integer.toString(i) + ".dir" + File.separator + Integer.toString(j) + ".dat");
            }
        }
        return true;
    }

    public static void put(String key, String value) throws IOException {
        if (currentTable == null) {
            throw new IOException("table not exists");
        }
        int hashCode = key.hashCode();
        int nDirectory = hashCode % 16;
        int nFile = hashCode / 16 % 16;


        if (!db[nDirectory][nFile].containsKey(key)) {
            System.out.println("new");
            int num = tableNames.get(currentTable);
            tableNames.remove(currentTable);
            tableNames.put(currentTable, num + 1);
        } else {
            System.out.println("overwrite" );
            System.out.println(db[nDirectory][nFile].get(key));
            db[nDirectory][nFile].remove(key);
        }
        db[nDirectory][nFile].put(key, value);
    }

    public static void get(String key) throws IOException {
        if (currentTable == null) {
            throw new IOException("table not exists");
        }
        int hashCode = key.hashCode();
        int nDirectory = hashCode % 16;
        int nFile = hashCode / 16 % 16;
        if (db[nDirectory][nFile].containsKey(key)) {
            System.out.println("found");
            System.out.println(db[nDirectory][nFile].get(key));
        } else {
            System.out.println("not found");
        }
    }

    public static void remove(String key) throws IOException {
        if (currentTable == null) {
            throw new IOException("table not exists");
        }
        int hashCode = key.hashCode();
        int nDirectory = hashCode % 16;
        int nFile = hashCode / 16 % 16;
        if (db[nDirectory][nFile].containsKey(key)) {
            db[nDirectory][nFile].remove(key);
            int num = tableNames.get(currentTable);
            tableNames.remove(currentTable);
            tableNames.put(currentTable, num - 1);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

    public static void list() throws IOException {
        if (currentTable == null || db == null) {
            throw new IOException("table not exists");
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Set keySet = db[i][j].keySet();
                String joined = String.join(", ", keySet);
                System.out.print(joined);
            }
        }
        System.out.println();
    }

    private static void create(String tablename) throws IOException {
        if (tableNames != null && tableNames.containsKey(tablename)) {
            System.out.println(tablename + " exists");
        }
        File table = new File(fmPath + File.separator + tablename);
        if (!table.mkdir()) {
            throw new IOException("not a directory");
        }
        for (int i = 0; i < 16; i++) {
            File subdir = new File(table.getAbsolutePath() + File.separator + Integer.toString(i) + ".dir");
            if (!subdir.mkdir()) {
                throw new IOException("not a directory");
            }
            for (int j = 0; j < 16; j++) {
                File data = new File(subdir.getAbsolutePath() + File.separator + Integer.toString(j) + ".dat");
                if (!data.createNewFile()) {
                    throw new IOException("can't create");
                }
            }
        }
        tableNames.put(tablename, 0);
        System.out.println("created");
    }

    private static void drop(String tablename) throws IOException {
        if (tableNames == null || !tableNames.containsKey(tablename)) {
            System.out.println(tablename + " not exists");
        } else if (!recursiveRemove(new File(fmPath + File.separator + tablename))) {
            throw new IOException("can't delete");
        } else {
            tableNames.remove(tablename);
            System.out.println("dropped");
        }
    }

    private static boolean recursiveRemove(File toRemove) {
        if (toRemove.isDirectory()) {
            File[] files = toRemove.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (!recursiveRemove(file)) {
                        return false;
                    }
                }
            }
        }
        try {
            if (!toRemove.delete()) {
                return false;
            }
            return true;
        } catch (SecurityException e) {
            System.err.println("Security exeption");
        }
        return false;
    }

    private static void use(String tablename) throws IOException {
        if (tableNames == null || !tableNames.containsKey(tablename)) {
            System.out.println(tablename + " not exists");
        } else {
            if (currentTable != null) {
                writeData();
            }
            currentTable = tablename;
            checkTable(tablename);
            readData();
            System.out.println("using " + tablename);
        }
    }

    private static void show() throws IOException {
        System.out.println("table_name row_count");
        if (tableNames == null) {
            return;
        }
        for (Map.Entry<String, Integer> name : tableNames.entrySet()) {
            System.out.println(name.getKey() + " " + name.getValue());
        }
    }

    private static void tooMuchArgs(String cmd) throws IllegalArgumentException {
        throw new IllegalArgumentException(cmd + ": too much arguments");
    }

    private static void fewArgs(String cmd) throws IllegalArgumentException {
        throw new IllegalArgumentException(cmd + ": few arguments");
    }
}
