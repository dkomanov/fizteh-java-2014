package ru.fizteh.fivt.students.lukina.MultiFileMap;

import ru.fizteh.fivt.students.lukina.shell.ShellMain;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public/* abstract */class MultiFileMap {
    private static String dbDir = "";
    private static String currentTable = "";
    private static HashMap<String, HashMap<String, String>> fileMap = new HashMap<>();
    private static boolean isPacket = false;

    private static void printError(String strError) {
        if (isPacket) {
            System.err.println(strError);
            System.exit(1);
        } else {
            System.out.println(strError);
        }
    }

    private static boolean checkArgs(int num, String[] args) {
        return args.length == num;
    }

    private static void put(String key, String value) {
        if (currentTable.isEmpty()) {
            System.out.println("no table");
        } else {
            if (fileMap.get(currentTable).containsKey(key)) {
                System.out.println("overwrite "
                        + fileMap.get(currentTable).get(key));
                fileMap.get(currentTable).remove(key);
            } else {
                System.out.println("new");
            }
            fileMap.get(currentTable).put(key, value);
        }
    }

    private static void get(String key) {
        if (currentTable.isEmpty()) {
            System.out.println("no table");
        } else {
            if (fileMap.get(currentTable).containsKey(key)) {
                System.out.println("found");
                System.out.println(fileMap.get(currentTable).get(key));
            } else {
                System.out.println("not found");
            }
        }
    }

    private static void remove(String key) {
        if (currentTable.isEmpty()) {
            System.out.println("no table");
        } else {
            if (fileMap.get(currentTable).containsKey(key)) {
                fileMap.get(currentTable).remove(key);
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }

    private static void list() {
        Set<String> keySet = fileMap.get(currentTable).keySet();
        for (String key : keySet) {
            System.out.print(key + " ");
        }
        System.out.println("");
    }

    private static void show() {
        Set<String> fileKeySet = fileMap.keySet();
        for (String key : fileKeySet) {
            System.out.println(key + " " + fileMap.get(key).size());
        }
    }

    private static void use(String tableName) {
        if (fileMap.containsKey(tableName)) {
            currentTable = tableName;
            System.out.println("use " + tableName);
        } else {
            System.out.println(tableName + " not exists");
        }
    }

    private static void drop(String tableName) {
        if (fileMap.containsKey(tableName)) {
            fileMap.remove(tableName);
            System.out.println("dropped");
            if (currentTable.equals(tableName)) {
                currentTable = "";
            }
        } else {
            System.out.println(tableName + " not exists");
        }
    }

    private static void create(String tableName) {
        if (!fileMap.containsKey(tableName)) {
            HashMap<String, String> map = new HashMap<>();
            fileMap.put(tableName, map);
            System.out.println("created");
        } else {
            System.out.println(tableName + "exists");
        }
    }

    private static void createWithoutPrinting(String tableName) {
        if (!fileMap.containsKey(tableName)) {
            HashMap<String, String> map = new HashMap<>();
            fileMap.put(tableName, map);
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

    protected static void execProc(String[] args) {
        if (args.length != 0) {
            switch (args[0]) {
                case "put":
                    if (checkArgs(3, args)) {
                        put(args[1], args[2]);
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
                    if (checkArgs(2, args)) {
                        create(args[1]);
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
                case "exit":
                    try {
                        writeDirectory(dbDir);
                    } catch (IOException e) {
                        System.out.println("can't write to file");
                    }
                    System.exit(0);
                    break;
                default:
                    printError("unknown_command");
            }
        }
    }

    /*
     * сначала аргументы делятся по пробелу.а мы их склеим и поделим по ";"потом
     * вызовем для каждой группы аргументовфункцию разбивающую аргументы снова
     * как нужнои выполняющую программки
     */
    private static void readFile(String fileName, String tableName) throws IOException {
        File f = new File(fileName);
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(fileName));
        } catch (FileNotFoundException e1) {
            f.createNewFile();
            System.out.println(fileName + "not found but created");
            readFile(fileName, tableName);
            return;
        }
        int length;
        while (true) {
            try {
                length = input.readInt();
                byte[] key = new byte[length];
                input.read(key, 0, length);

                length = input.readInt();
                byte[] value = new byte[length];
                input.read(value, 0, length);

                String stringKey = new String(key);
                String stringValue = new String(value);

                //проверка правильности расположения данных
                int fileNumber = Integer.parseInt(f.getName());
                int dirNumber = Integer.parseInt(f.getParentFile().getName());
                if (fileNumber != key.toString().hashCode() / 16 % 16
                        || dirNumber != key.toString().hashCode() % 16) {
                    System.out.println(" incorrect format of file " + fileName);
                }
                fileMap.get(tableName).put(stringKey, stringValue);
                
            } catch (EOFException e) {
                return;
            }
        }
    }

    private static void readTables(String startDirectory) {
        File start = new File(startDirectory);
        if (!start.exists()) {
            ShellMain.mkdir(startDirectory);
            ;
            System.out.println(start.getName() + " not found but created");
        }
        if (start.exists() && start.isDirectory()) {
            for (File table : start.listFiles()) {
                if (table.isDirectory()) {
                    for (File directory : table.listFiles()) {
                        if (directory.isDirectory()) {
                            for (File file : directory.listFiles()) {
                                if (file.isFile()) {
                                    createWithoutPrinting(table.getName());
                                    try {
                                        readFile(file.getAbsolutePath(), table.getName());
                                    } catch (IOException e) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void writeDirectory(String start) throws IOException {
        File startDirectory = new File(start);
        System.out.println(startDirectory.exists() + "aaaaaa\n\n\n");
        for (File f : startDirectory.listFiles()) {
            if (f.isDirectory()) {
                ShellMain.rm(f.getAbsolutePath(), true);
            }
        }
        Set<String> fileKeySet = fileMap.keySet();
        for (String key : fileKeySet) {
            ShellMain.mkdir(dbDir + File.separator + key);
            for (int i = 0; i < 256; i++) {
                for (Entry<String, String> entry : fileMap.get(key).entrySet()) {
                    if (entry.getKey().hashCode() % 16 == i % 16
                            && entry.getKey().hashCode() / 16 % 16 == i / 16 % 16) {
                        File directory = new File(dbDir + File.separator
                                + key + File.separator + i % 16);
                        File file = new File(dbDir + File.separator + key
                                + File.separator + i % 16 + File.separator + i / 16 % 16);
                        if (!directory.exists() || !directory.isDirectory()) {
                            ShellMain.mkdir(directory.getAbsolutePath());
                        }
                        if (!file.exists() || file.isDirectory()) {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                System.out.println("can't create " + file.getAbsolutePath());
                            }
                        }
                        DataOutputStream output = null;
                        try {
                            output = new DataOutputStream(new FileOutputStream(file.getAbsolutePath()));
                        } catch (FileNotFoundException e) {
                            printError(file.getAbsolutePath() + " file not found");
                            System.exit(1);
                        }
                        output.writeInt(entry.getKey().getBytes("UTF-8").length);
                        output.write(entry.getKey().getBytes("UTF-8"));
                        output.writeInt(entry.getValue().getBytes("UTF-8").length);
                        output.write(entry.getValue().getBytes("UTF-8"));
                    }
                }
            }
        }
    }

    public static void exec(String[] args) {
        if (System.getProperty("fizteh.db.dir") == null) {
            printError("empty param");
            System.exit(1);
        }
        File dir = new File(System.getProperty("fizteh.db.dir"));
        if (dir.isAbsolute()) {
            dbDir = dir.getAbsolutePath();
        } else {
            dbDir = System.getProperty("user.dir")
                    + File.separator + System.getProperty("fizteh.db.dir");
        }
        readTables(dbDir);
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
            try {
                writeDirectory(dbDir);
            } catch (IOException e) {
                System.out.println("can't write to file");
            }
        } else {
            isPacket = false;
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter(System.lineSeparator());
            while (scanner.hasNextLine()) {
                Scanner scannerParse = new Scanner(scanner.nextLine());
                scannerParse.useDelimiter("[ ]*;[ ]*");
                while (scannerParse.hasNext()) {
                    String string = "";
                    string = scannerParse.next();
                    if (!string.isEmpty()) {
                        execProc(getArgsFromString(string));
                    }
                }
                System.out.print("$ ");
            }
            scanner.close();
        }
    }

    public static void main(String[] args) {
        MultiFileMap.exec(args);
        System.exit(0);
    }
}
