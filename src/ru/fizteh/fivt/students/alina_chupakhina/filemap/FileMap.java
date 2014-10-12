package ru.fizteh.fivt.students.alina_chupakhina.filemap;

import java.io.*;
import java.util.*;

public class FileMap {

    private static String path;
    private static Map<String, String> fm;
    private static boolean out;
    private static RandomAccessFile file;
    private static String message = "invalid number of arguments";

    public static void main(final String[] args) {
        try {
            fm = new TreeMap<String, String>();
            path = System.getProperty("db.file");
            try {
                file = new RandomAccessFile(path, "r");
                getFile();
            } catch (FileNotFoundException e) {
                File f = new File(path);
                f.createNewFile();
            }
            if (args.length > 0) {
                batch(args);
            } else {
                interactive();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void getFile() throws Exception {
        int n = 0;
        int i = 0;
        String key;
        String value;

        boolean end = false;
        while (!end) {
            try {
                int length = file.readInt();
                byte[] bytes = new byte[length];
                file.readFully(bytes);
                key = new String(bytes, "UTF-8");
                length = file.readInt();
                bytes = new byte[length];
                file.readFully(bytes);
                value = new String(bytes, "UTF-8");
                fm.put(key, value);
            } catch (IOException e) {
                end = true;
            }
        }
    }

    public static void putFile() throws Exception {
        String key;
        String value;
        DataOutputStream outStream  = new DataOutputStream(
                    new FileOutputStream(path));
        for (Map.Entry<String, String> i : fm.entrySet()) {
            key = i.getKey();
            value = i.getValue();
            try {
                byte[] byteWord = key.getBytes("UTF-8");
                outStream.writeInt(byteWord.length);
                outStream.write(byteWord);
                outStream.flush();
                byteWord = value.getBytes("UTF-8");
                outStream.writeInt(byteWord.length);
                outStream.write(byteWord);
                outStream.flush();
            } catch (Exception e) {
                throw new Exception("Error with writing");
            }
        }
    }

    public static void interactive() {
        out = false;
        Scanner sc = new Scanner(System.in);
        try {
            while (!out) {
                System.out.print("$ ");
                String s = sc.nextLine();
                doCommand(s, false);
            }
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void batch(final String[] args) {
        String arg;
        arg = args[0];
        for (int i = 1; i != args.length; i++) {
            arg = arg + ' ' + args[i];
        }
        String[] commands = arg.trim().split(";");
        try {
            for (int i = 0; i != commands.length; i++) {
                doCommand(commands[i], true);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        interactive();
    }

    public static void doCommand(final String command, boolean isBatch)
            throws Exception {
        String[] args = command.trim().split("\\s+");
        try {
            if (args[0].equals("put")) {
                put(args);
            } else if (args[0].equals("get")) {
                get(args);
            } else if (args[0].equals("remove")) {
                remove(args);
            } else if (args[0].equals("list")) {
                list(args);
            } else if (args[0].equals("exit")) {
                exit(args);
            } else if (args[0].equals("")) {
                out = false;
            } else {
                throw new Exception(args[0] + "Invalid command");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (isBatch) {
                System.exit(-1);
            }
        }
    }

    public static void put(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("put: " + message);
        }
        String key = args[1];
        String value = args[2];
        String s = fm.put(key, value);
        if (s != null) {
            System.out.println("overwrite");
            System.out.println(s);
        } else {
            System.out.println("new");
        }
    }

    public static void get(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("get: " + message);
        }
        String key = args[1];
        String s = fm.get(key);
        if (s != null) {
            System.out.println("found");
            System.out.println(s);
        } else {
            System.out.println("not found");
        }
    }

    public static void remove(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("remove: " + message);
        }
        String key = args[1];
        String s = fm.remove(key);
        if (s != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

    public static void list(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("list: " + message);
        }
        Set<String> keySet = fm.keySet();
        int counter = 0;
        for (String current : keySet) {
            ++counter;
            System.out.print(current);
            if (counter != keySet.size()) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    public static void exit(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("exit: " + message);
        }
        putFile();
        System.exit(0);
    }
}

