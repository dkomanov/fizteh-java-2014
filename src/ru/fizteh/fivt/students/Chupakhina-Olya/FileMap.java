package ru.fizteh.fivt.students.olga_chupakhina.filemap;

import java.util.*;
import java.io.*;

public class FileMap {
    private static boolean mode;
    private static String path;
    private static TreeMap<String, String> map;
    private static RandomAccessFile file;

    public static void main(final String[] args) {
        try {
            map = new TreeMap<String, String>();
            try {
                path = "D:\\ol.txt";
                try {
                    file = new RandomAccessFile(path, "r");
                    getFile();
                } catch (FileNotFoundException e) {
                    File f = new File(path);
                    f.createNewFile();
                }
            } catch (Exception e) {
                throw new Exception("Can't find or create data base file");
            }
            if (args.length == 0) {
                mode = true;
                interactiveMode();
            } else {
                mode = false;
                packageMode(args);
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
        boolean notEnd = true;
        while (notEnd) {
            try {
                int length = file.readInt();
                byte[] bytes = new byte[length];
                file.readFully(bytes);
                key = new String(bytes, "UTF-8");
                length = file.readInt();
                bytes = new byte[length];
                file.readFully(bytes);
                value = new String(bytes, "UTF-8");
                map.put(key, value);
            } catch (IOException e) {
                notEnd = false;
            }
        }
    }

    public static void putFile() throws Exception {
        String key;
        String value;
        DataOutputStream outStream = new DataOutputStream(
                new FileOutputStream(path));
        for (Map.Entry<String, String> i : map.entrySet()) {
            key = i.getKey();
            value = i.getValue();
            byte[] byteWord = key.getBytes("UTF-8");
            outStream.writeInt(byteWord.length);
            outStream.write(byteWord);
            outStream.flush();
            byteWord = value.getBytes("UTF-8");
            outStream.writeInt(byteWord.length);
            outStream.write(byteWord);
            outStream.flush();
        }
    }

        private static void packageMode(String[] args) {
        StringBuilder commands = new StringBuilder();
        for (String arg: args) {
            commands.append(arg);
            commands.append(' ');
        }
        separationLine(commands.toString());
    }

    private static void interactiveMode() throws Exception {
        Scanner scanner = new Scanner(System.in);
        try  {
            while (true) {
                System.out.print("$ ");
                String commands = scanner.nextLine();
                separationLine(commands);
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void separationLine(String line) {
        String[] commands = line.trim().split(";");
        try {
            for (int i = 0; i < commands.length; i++) {
                doCommand(commands[i]);
            }
        } catch (Exception e) {
            System.exit(0);
        }
    }

    private static void doCommand(String command)
            throws Exception {
        command = command.trim();
        String[] args = command.split("\\s+");
        boolean done = false;
        try {
            if (args.length > 0 && !args[0].isEmpty()) {
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
                } else {
                    throw new Exception(args[0] + ": Invalid command");
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (mode) {
                System.exit(-1);
            }
        }
    }

    public static void put(String[] args) throws Exception {
        if (args.length != 3) {
            throw new Exception("put: invalid number of arguments");
        }
        String key = args[1];
        String value = args[2];
        String s;
        s = map.put(key, value);
        if (s != null) {
            System.out.println("overwrite");
            System.out.println(s);
        } else {
            System.out.println("new");
        }
    }

    public static void get(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("get: invalid number of arguments");
        }
        String s = map.get(args[1]);
        if (s != null) {
            System.out.println("found");
            System.out.println(s);
        } else {
            System.out.println("not found");
        }
    }

    public static void remove(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("remove: invalid number of arguments");
        }
        String s = map.remove(args[1]);
        if (s != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

    public static void list(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("list: invalid number of arguments");
        }
        Set<String> keySet = map.keySet();
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
            throw new Exception("exit: invalid number of arguments");
        }
        putFile();
        map.clear();
        System.exit(0);
    }
}
