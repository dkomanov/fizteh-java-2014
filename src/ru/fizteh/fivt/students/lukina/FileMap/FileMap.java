package ru.fizteh.fivt.students.lukina.FileMap;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public/* abstract */class FileMap {
    private static HashMap<String, String> map = new HashMap<>();
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
        if (map.containsKey(key)) {
            System.out.println("overwrite " + map.get(key));
            map.remove(key);
        } else {
            System.out.println("new");
        }
        map.put(key, value);
    }

    private static void get(String key) {
        if (map.containsKey(key)) {
            System.out.println("found");
            System.out.println(map.get(key));
        } else {
            System.out.println("not found");
        }
    }

    private static void remove(String key) {
        if (map.containsKey(key)) {
            map.remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

    private static void list() {
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            System.out.print(key + " ");
        }
        System.out.println("");
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

    protected/* abstract */ static void execProc(String[] args) {
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
//
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
    private static void readFile(String fileName) throws IOException {

        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(fileName));
        } catch (FileNotFoundException e1) {
            File f = new File(fileName);
            f.createNewFile();
            System.out.println(fileName + "not found but created");
            readFile(fileName);
           return;
        }
        int lenght;
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        while (true) {
            try {
                lenght = input.readInt();
                for (int i = 0; i < lenght; i++) {
                    key.append(input.readChar());
                }
                lenght = input.readInt();
                for (int i = 0; i < lenght; i++) {
                    value.append(input.readChar());
                }
                map.put(key.toString(), value.toString());
                key.delete(0, key.length());
                value.delete(0, value.length());
            } catch (EOFException e) {
                return;
            }
        }
    }

    private static void writeFile(String fileName) throws IOException {
        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            printError(fileName + " file not found");
            System.exit(1);
        }
        for (Entry<String, String> entry : map.entrySet()) {
            output.writeInt(entry.getKey().length());
            output.writeChars(entry.getKey());
            output.writeInt(entry.getValue().length());
            output.writeChars(entry.getValue());
        }
    }

    public static void exec(String[] args) {
        if (System.getProperty("db.file") == null) {
            printError("empty param");
            System.exit(1);
        }
        try {
            readFile(System.getProperty("db.file"));
        } catch (IOException e) {
            printError("cannot read the file");
            return;
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
                if (string.equals("exit")) {
                    break;
                }
                if (!string.isEmpty()) {
                    execProc(getArgsFromString(string));
                }
            }
            scanner.close();
            try {
                writeFile(System.getProperty("db.file"));
            } catch (IOException e) {
                printError("cannot write to file");
                return;
            }
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
                        try {
                            writeFile(System.getProperty("db.file"));
                        } catch (IOException e) {
                            printError("cannot write to file");
                            return;
                        }
                        return;
                    }
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
        FileMap.exec(args);
        System.exit(0);
    }
}
