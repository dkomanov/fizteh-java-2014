package ru.fizteh.fivt.students.lukina.DataBase;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class DataBase {
    private static DBase currTable;
    private static boolean isPacket = false;
    private static DBaseProvider prov;
    DBaseProviderFactory factory;
    private String dbDir = "";

    public DataBase() {
        String property = System.getProperty("fizteh.db.dir");
        if (property == null) {
            throw new RuntimeException("root dir not selected");
        }
        File r = new File(property);
        if (!r.exists()) {
            if (!r.mkdirs()) {
                throw new RuntimeException("cannot create root dir " + property);
            }
        }
        if (property.endsWith(File.separator)) {
            dbDir = property;
        } else {
            dbDir = property + File.separatorChar;
        }
        factory = new DBaseProviderFactory();
        try {
            prov = (DBaseProvider) factory.create(dbDir);
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }


    private static void printError(String strError) {
        if (isPacket) {
            try {
                currTable.unloadData();
            } finally {
                System.err.println(strError);
                System.exit(1);
            }
        } else {
            System.out.println(strError);
        }
    }

    private static boolean checkArgs(int num, String[] args) {
        return args.length == num;
    }

    private static void put(String key, String value) {
        try {
            Storeable oldValue = currTable.put(key, prov.deserialize(currTable, value));
            if (oldValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(prov.serialize(currTable, oldValue));
            }
        } catch (RuntimeException e) {
            printError("Cannot parse arguments");
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

    }

    private static void get(String key) {
        try {
            Storeable value = currTable.get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(prov.serialize(currTable, value));
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void remove(String key) {
        try {
            Storeable value = currTable.remove(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void list() {
        if (currTable != null) {
            currTable.list();
        }
    }

    private static void show() {
        if (prov != null) {
            prov.getTableNames();
        }
    }

    private static void use(String tableName) {
        if (currTable != null) {
            int changes = currTable.getNumberOfUncommittedChanges();
            if (changes != 0) {
                System.out.println(changes + " unsaved changes");
                return;
            }
        }
        try {
            DBase tmp = (DBase) prov.getTable(tableName);
            if (tmp == null) {
                System.out.println(tableName + " not exists");
            } else {
                if (currTable != null) {
                    currTable.unloadData();
                }
                currTable = tmp;
                System.out.println("using " + tableName);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void drop(String tableName) {
        if (currTable == prov.getTable(tableName)) {
            currTable = null;
        }
        try {
            prov.removeTable(tableName);
            System.out.println("dropped");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(tableName + " not exists");
        }
    }

    private static void doCreateTable(String args) {
        args.trim();
        if (!args.endsWith(")")) {
            printError("Incorrect arguments. Need types");
            return;
        }
        args = args.replace(")", "");
        String[] strArray = args.split("[ ]+[/(]", 2);
        if (strArray.length < 2) {
            printError("Incorrect arguments. Need types");
            return;
        }
        String tableName = strArray[0];
        ArrayList<Class<?>> list = new ArrayList<>();
        Scanner sc = new Scanner(strArray[1]);
        while (sc.hasNext()) {
            try {
                String type = sc.next();
                Class<?> cl = prov.getClassFromString(type);
                list.add(cl);
            } catch (RuntimeException e) {
                printError("wrong type (" + e.getMessage() + ")");
                sc.close();
                return;
            }
        }
        sc.close();
        try {
            if (prov.createTable(tableName, list) == null) {
                System.out.println(tableName + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void doCommit() {
        if (currTable == null) {
            return;
        }
        try {
            System.out.println(currTable.commit());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static String[] getArgsFromString(String str) {
        String newStr = str.replaceAll("[ ]+", " ");
        int countWords = 1;
        for (int i = 0; i < newStr.length(); i++) {
            if (str.charAt(i) == ' ') {
                countWords++;
            }
        }
        String[] args = new String[countWords];
        Scanner scanner = new Scanner(newStr);
        scanner.useDelimiter(" ");
        for (int i = 0; scanner.hasNext(); i++) {
            args[i] = scanner.next();
        }
        scanner.close();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                countWords--;
            }
        }
        String[] arg = new String[countWords];
        for (int i = 0; i < countWords; i++) {
            arg[i] = args[i];
        }
        return arg;
    }

    public static void main(String[] args) {
        try {
            DataBase data = new DataBase();
            data.exec(args);
            data.doExit();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void doExit() {
        System.exit(0);
    }

    /*
     * сначала аргументы делятся по пробелу.а мы их склеим и поделим по ";"потом
     * вызовем для каждой группы аргументовфункцию разбивающую аргументы снова
     * как нужнои выполняющую программки
     */

    private void doRollBack() {
        if (currTable != null) {
            System.out.println(currTable.rollback());
        }
    }

    public String appendArgs(int num, String[] args) {
        StringBuffer str = new StringBuffer(args[num]);
        for (int i = num + 1; i < args.length; ++i) {
            str.append(" ");
            str.append(args[i]);
        }
        return str.toString();
    }

    protected void execProc(String[] args) {
        if (args != null && args.length != 0) {
            switch (args[0]) {
                case "put":
                    if (args.length > 2) {
                        put(args[1], appendArgs(2, args));
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "get":
                    if (checkArgs(2, args)) {
                        get(args[1]);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "remove":
                    if (checkArgs(2, args)) {
                        remove(args[1]);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "list":
                    if (checkArgs(1, args)) {
                        list();
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "create":
                    if (args.length > 1) {
                        doCreateTable(appendArgs(1, args));
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "drop":
                    if (checkArgs(2, args)) {
                        drop(args[1]);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "use":
                    if (checkArgs(2, args)) {
                        use(args[1]);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "show":
                    if (checkArgs(2, args) && args[1].equals("tables")) {
                        show();
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "size":
                    System.out.println(currTable.size());
                    break;
                case "commit":
                    if (checkArgs(1, args)) {
                        doCommit();
                    }
                    break;
                case "rollback":
                    if (checkArgs(1, args)) {
                        doRollBack();
                    }
                    break;
                case "exit":
                    if (checkArgs(1, args)) {
                        doExit();
                    }
                default:
                    printError("unknown_command");
            }
        }
    }

    public void exec(String[] args) {
        File dir = new File(System.getProperty("fizteh.db.dir"));
        if (dir.isAbsolute()) {
            dbDir = dir.getAbsolutePath();
        } else {
            dbDir = System.getProperty("user.dir")
                    + File.separator + System.getProperty("fizteh.db.dir");
        }
        if (args.length != 0) {
            isPacket = true;
            StringBuffer str = new StringBuffer(args[0]);
            for (int i = 1; i < args.length; i++) {
                str.append(" ");
                str.append(args[i]);
            }
            Scanner scanner = new Scanner(str.toString());
            scanner.useDelimiter("[ ]*;[ ]*");
            while (scanner.hasNext()) {
                String string = "";
                string = scanner.next();
                if (!string.isEmpty()) {
                    execProc(getArgsFromString(string));
                }
            }
            scanner.close();
        } else {
            isPacket = false;
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter(System.lineSeparator());
            while (scanner.hasNextLine()) {
                Scanner scannerParse = new Scanner(scanner.next());
                scannerParse.useDelimiter("[ ]*;[ ]*");
                while (scannerParse.hasNext()) {
                    String string = "";
                    string = scannerParse.next();
                    if (string.equals("exit")) {
                        scanner.close();
                        scannerParse.close();
                        return;
                    }
                    if (!string.isEmpty()) {
                        execProc(getArgsFromString(string));
                    }
                }
                scannerParse.close();
                System.out.print("$ ");
            }
            scanner.close();
        }
    }
}
