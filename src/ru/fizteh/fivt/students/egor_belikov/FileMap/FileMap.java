package ru.fizteh.fivt.students.egor_belikov.FileMap;

import java.io.*;
import java.util.*;
import java.nio.file.Paths;

public class FileMap {
    private static TreeMap<String, String> fileMap;
    private static String currentPath;
    private static boolean packageMode;
    private static RandomAccessFile currentDatabase;

        public static void main(String[] args) {
            try {
                currentPath = Paths.get(System.getProperty("db.file")).toString();
                if (new File(currentPath).exists()) {
                    currentDatabase = new RandomAccessFile(currentPath, "r");
                    makeFileMapFromFile();
                } else {
                    File file = new File(currentPath);
                    file.createNewFile();
                }
                packageMode = (args.length != 0);
                if (packageMode) {
                    pack(args);
                } else {
                    interactive();
                } 
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }


        private static void pack(String[] args) {
            StringBuilder commands = new StringBuilder();
            for (String arg: args) {
                commands.append(arg);
                commands.append(" ");
            }
            String[] splittedCommands = commands.toString().trim().split(";");
            try {
                for (String s: splittedCommands) {
                execute(s);
            }
            } catch (Exception exception) {
                System.exit(0);
            }
        }


        private static void execute(String s) throws Exception {
            String[] args = s.trim().split("\\s+");
            try {
                if (args.length > 0 && !args[0].isEmpty()) {
                    switch (args[0]) {
                        case "put":
                            if (args.length != 3) {
                                throw new Exception("put: invalid number of arguments");
                            } else {
                                put(args);
                            }
                            break;
                        case "get":
                            if (args.length != 2) {
                                throw new Exception("get: invalid number of arguments");
                            } else {
                                get(args);
                            }
                            break;
                        case "remove":
                            if (args.length != 2) {
                                throw new Exception("remove: invalid number of arguments");
                            } else {
                                remove(args);
                            }
                            break;
                        case "list":
                            if (args.length != 1) {
                                throw new Exception("list: invalid number of arguments");
                            } else {
                                list();
                            }
                            break;
                        case "exit":
                            if (args.length != 1) {
                                throw new Exception("exit: invalid number of arguments");
                            } else {
                                createFileMapDatabase();
                                fileMap.clear();
                                System.exit(0);
                            } 
                                break;
                        default:
                            throw new Exception(args[0] + ": Invalid command");
                    }

                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                if (packageMode) {
                    System.exit(1);
                }
            }
        }
        private static void createFileMapDatabase() throws Exception {
            String key;
            String value;
            DataOutputStream outStream = new DataOutputStream(
                new FileOutputStream(currentPath));
            for (Map.Entry<String, String> i : fileMap.entrySet()) {
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


        private static void list() {
            Set<String> keySet = fileMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                System.out.print(iterator.next() + ", ");
            }
            System.out.println();
        }


        private static void remove(String[] args) {
            String removed = fileMap.remove(args[1]);
            if (removed != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }


        private static void get(String[] args) {
            String found = fileMap.get(args[1]);
            if (found != null) {
                System.out.println("found");
                System.out.println(found);
            } else {
                System.out.println("not found");
            }
        }


        private static void put(String[] args) {
            String key = args[1];
            String value = args[2];
            String put = fileMap.put(key, value);
            if (put != null) {
                System.out.println("overwrite");
                System.out.println(put);
            } else {
                System.out.println("new");
            }
        }


        private static void interactive() {
        try  (Scanner scanner = new Scanner(System.in)) {
            try  {
                while (true) {
                    System.out.print("$ ");
                    String[] splittedCommands;
                    splittedCommands = scanner.nextLine().trim().split(";");
                    try {
                        for (String s: splittedCommands) {
                            execute(s);
                        }
                    } catch (Exception exception) {
                        System.exit(0);
                    }
                }
            } catch (NoSuchElementException exception) {
                System.err.println(exception.getMessage());
                System.exit(1);
            }
        }
        }


        private static void makeFileMapFromFile() throws Exception {
        String key;
        String value;
        boolean fileEnd = false;
        while (!fileEnd) {
            try {
                int keyLen = currentDatabase.readInt();
                byte[] keyBytes = new byte[keyLen];
                currentDatabase.readFully(keyBytes);
                key = new String(keyBytes, "UTF-8");
                int valueLen = currentDatabase.readInt();
                byte[] valueBytes = new byte[valueLen];
                currentDatabase.readFully(valueBytes);
                value = new String(valueBytes, "UTF-8");
                fileMap.put(key, value);
            } catch (IOException e) {
                fileEnd = true;
            }
        }
        }
}
