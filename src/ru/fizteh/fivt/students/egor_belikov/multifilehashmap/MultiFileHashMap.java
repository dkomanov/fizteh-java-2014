package ru.fizteh.fivt.students.egor_belikov.multifilehashmap;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiFileHashMap {
    private static String currentPath;
    private static boolean isInteractiveMode;
    private static TreeMap<String, Integer> listOfTables;    
    private static TreeMap<String, String> currentFileMap;
    private static String currentTable;
    private static String separator;


    public static void main(String[] args) throws Exception {
        separator = File.separator;
        try {
            currentPath = Paths.get(System.getProperty("fizteh.db.dir")).toString();
            File directoryFromCurrentPath = new File(currentPath);
            if (directoryFromCurrentPath.exists() && directoryFromCurrentPath.isDirectory()) {
                regenerateFileMaps();
                isInteractiveMode = (args.length == 0);

                if (!isInteractiveMode) {
                    pack(args);

                } else {
                    interactive();
                }
            } else {
                throw new Exception("Directory on path in fizteh.db.dir not found");
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            System.exit(1);
        }
    }

    private static void regenerateFileMaps() throws Exception {
        File file = new File(currentPath);
        File[] fList = file.listFiles();
        for (File dir: fList) {
            if (dir.isDirectory()) {
                String currentTablePath = dir.toString();
                File tableDirectory = new File(currentTablePath);
                String tableName = tableDirectory.getName();
                if (!listOfTables.containsKey(tableName)) {
                    listOfTables.put(tableName, 0);
                }
                File[] directories;
                directories = tableDirectory.listFiles();
                for (File directory : directories) {
                    if (!directory.isDirectory()) {
                        throw new Exception(directory.getName() + " is not directory");
                    }
                    File[] datFiles = directory.listFiles();
                    for (File datFile : datFiles) {
                        if (!datFile.getName().matches("[0 - 15].dat")) {
                            throw new Exception("Filename must be .dat");
                        }
                        int nDirectory;
                        nDirectory = Integer.parseInt(directory.getName().substring(0, directory.getName().length() - 4));
                        int nFile;
                        nFile = Integer.parseInt(datFile.getName().substring(0, datFile.getName().length() - 4));
                        String stringKey, stringValue;
                        try (RandomAccessFile currentFile = new RandomAccessFile(datFile.getAbsolutePath(), "r")) {
                            while (true) {
                                try {
                                    int length = currentFile.readInt();
                                    byte[] bytes = new byte[length];
                                    currentFile.readFully(bytes);
                                    stringKey = new String(bytes, "UTF-8");
                                    length = currentFile.readInt();
                                    bytes = new byte[length];
                                    currentFile.readFully(bytes);
                                    stringValue = new String(bytes, "UTF-8");
                                    int keyHashcode = stringKey.hashCode();
                                    if (!(nDirectory == keyHashcode % 16) || !(nFile == keyHashcode / 16 % 16)) {
                                        throw new Exception("Current table damaged");
                                    }
                                    listOfTables.put(tableName, listOfTables.get(tableName) + 1);
                                } catch (IOException exception) {
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                throw new Exception(dir.getName() + " is not a path to directory");
            }
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

    private static void interactive() {
        Scanner scanner = new Scanner(System.in);
        try  {
            while (true) {
                System.out.print("$ ");
                String commands;
                commands = scanner.nextLine();
                try {
                    String[] splittedCommands = commands.trim().split(";");
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

    private static void execute(String s) throws Exception {        
        String[] args = s.trim().split("\\s+");
        if (args[0].equals("get") || args[0].equals("remove") || args[0].equals("list") || args[0].equals("put")) {
            if (currentTable.length() == 0) {
                throw new Exception("There is no table for this moment");
            }
        }
        if (args[0] == "create" || args[0] == "drop" || args[0] == "use" || args[0] == "get" || args[0] == "remove") {
            if (args.length != 2) {
                throw new Exception(args[0] + ": invalid number of arguments");
            }
        }
        if (args[0] == "list" || args[0] == "exit") {
            if (args.length != 1) {
                throw new Exception(args[0] + ": invalid number of arguments");
            }
        }
        if (args[0] == "put" && args.length != 3) {
                    throw new Exception(args[0] + ": invalid number of arguments");
        }
        switch (args[0]) {
            case "create":
                create(args);
                break;
            case "drop":
                drop(args);
                break;
            case "use":
                use(args);
                break;
            case "show":
                show(args);
                break;
            case "put":
                put(args);
                break;
            case "get":
                get(args);
                break;
            case "remove":
                remove(args);
                break;
            case "list":
                list(args);
                break;
            case "exit":
                if (currentTable.length() != 0) {
                    putCurrentMapToDirectory();
                }
                currentFileMap.clear();
                System.exit(0);
                break;
            default:
                throw new Exception("Invalid command");
        }
    }

    private static void create(String[] args) throws Exception {
        File currentFile;
        currentFile = new File(currentPath + separator + args[1]);
        if (!currentFile.exists()) {
            currentFile.createNewFile();
            if (!currentFile.isDirectory()) {
                throw new Exception(currentFile.toString() + ": is not a directory");
            } else {
                System.out.println("created");
            }
        } else {
            System.out.println(args[1] + " exists");
        }
    }

    private static void drop(String[] args) {
        
        File currentfile = new File(currentPath + separator + args[1]);
        if (currentfile.exists()) {
            if (currentfile.isDirectory()) {
                deleteDirectory(currentfile);
                System.out.println("dropped");
            } else {
                try {
                    throw new Exception(currentfile.toString() + ": is not a directory");
                } catch (Exception exception) {
                    Logger.getLogger(MultiFileHashMap.class.getName()).log(Level.SEVERE, null, exception);
                }
            }
        } else {
            System.out.println(args[1] + " not exists");
        }
    }

    private static void use(String[] args) throws IOException {
        if (currentTable.length() != 0) {
            putCurrentMapToDirectory();
        }
        File currentfile;
        currentfile = new File(currentPath + separator + args[1]);
        if (!currentfile.exists()) {
            System.out.println(args[1] + " not exists");
        }
        if (currentfile.isDirectory()) {
            currentTable = args[1];
            getMapFromDirectory(currentfile);
            System.out.println("using " + args[1]);
        } else {
            try {
                throw new Exception(currentfile.toString() + ": is not a directory");
            } catch (Exception exception) {
                Logger.getLogger(MultiFileHashMap.class.getName()).log(Level.SEVERE, null, exception);
            }
        }
    }

    private static void show(String[] args) {
        System.out.println("table_name row_count");
        listOfTables.entrySet().stream().forEach((table) -> {
            System.out.println(table.getKey() + " " + table.getValue());
        });
    }

    private static void put(String[] args) {
        String key = args[1], value = args[2], temp = currentFileMap.put(key, value);
        if (temp == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite\n" + temp);
        }
    }

    private static void get(String[] args) {
        String temp = currentFileMap.get(args[1]);
        if (temp == null) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + temp);
        }

    }

    private static void remove(String[] args) {
        String temp = currentFileMap.remove(args[1]);
        if (temp == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    private static void list(String[] args) {
        Iterator it = currentFileMap.keySet().iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + ", ");
        }
        System.out.println();
    }

    private static void deleteDirectory(File currentFile) {
        for (File i : currentFile.listFiles()) {
            deleteDirectory(i);
        }
        if (!currentFile.delete()) {
            try {
                throw new Exception("can't delete");
            } catch (Exception exception) {
                Logger.getLogger(MultiFileHashMap.class.getName()).log(Level.SEVERE, null, exception);
            }
        }
    }
    private static void putCurrentMapToDirectory() throws IOException {
        String stringKey, stringValue;
        File table = new File(currentPath + File.separator + currentTable);
        File[] dirs = table.listFiles();
        for (File dir: dirs) {
            deleteDirectory(dir);
        }
        for (Map.Entry<String, String> i : currentFileMap.entrySet()) {
            stringKey = i.getKey();
            stringValue = i.getValue();
            int hashcode = stringKey.hashCode();
            int ndirectory = hashcode % 16;
            int nfile = hashcode / 16 % 16;
            File newFile = new File(currentPath + separator + currentTable + separator + ndirectory + ".dir" + separator + nfile + ".dat");
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(newFile));
            byte[] byteWord = stringKey.getBytes("UTF-8");
            outStream.writeInt(byteWord.length);
            outStream.write(byteWord);
            outStream.flush();
            byteWord = stringValue.getBytes("UTF-8");
            outStream.writeInt(byteWord.length);
            outStream.write(byteWord);
            outStream.flush();
        }
    }

    private static void getMapFromDirectory(File currentFile) {
        for (File dir: currentFile.listFiles()) {
            if (dir.isDirectory()) {
                try {
                    RandomAccessFile randomAccessCurrentFile = new RandomAccessFile(currentFile, "r");
                    String stringKey, stringValue;
                    while (true) {
                        try {
                            int length = randomAccessCurrentFile.readInt();
                            byte[] bytesArray = new byte[length];
                            randomAccessCurrentFile.readFully(bytesArray);
                            stringKey = new String(bytesArray, "UTF-8");
                            length = randomAccessCurrentFile.readInt();
                            bytesArray = new byte[length];
                            randomAccessCurrentFile.readFully(bytesArray);
                            stringValue = new String(bytesArray, "UTF-8");
                            currentFileMap.put(stringKey, stringValue);
                        } catch (IOException exception) {
                            break;
                        }
                    }
                } catch (FileNotFoundException exception) {            
                    Logger.getLogger(MultiFileHashMap.class.getName()).log(Level.SEVERE, null, exception);
                }
            } else {
                try {
                    throw new Exception(dir.getName() + " is not directory");
                } catch (Exception exception) {
                    Logger.getLogger(MultiFileHashMap.class.getName()).log(Level.SEVERE, null, exception);
                }
            }
        }
    }
}
