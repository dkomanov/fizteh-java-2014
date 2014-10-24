package ru.fizteh.fivt.students.olga_chupakhina.multifilehashmap;

import java.util.*;
import java.io.*;


public class MultiFileHashMap {
    private static String path;
    private static boolean mode;
    private static String curTable;
    private static TreeMap<String, String> map;
    private static TreeMap<String, Integer> tableList;

    public static void main(String[] args)
            throws Exception {
        try {
            path = System.getProperty("fizteh.db.dir");
            File dirPath = new File(path);
            if (!dirPath.exists() || !dirPath.isDirectory()) {
                throw new Exception("directory not exist");
            }
            getMaps();
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

                if (args[0].equals("use")) {
                    use(args);

                } else if (args[0].equals("create")) {
                    create(args);

                } else if (args[0].equals("drop")) {
                    drop(args);

                } else if (args[0].equals("show")) {
                    show(args);

                } else if (args[0].equals("put")) {
                    if (curTable.length() == 0) {
                        throw new Exception("no table");
                    }
                    put(args);

                } else if (args[0].equals("get")) {
                    if (curTable.length() == 0) {
                        throw new Exception("no table");
                    }
                    get(args);

                } else if (args[0].equals("remove")) {
                    if (curTable.length() == 0) {
                        throw new Exception("no table");
                    }
                    remove(args);

                } else if (args[0].equals("list")) {
                    if (curTable.length() == 0) {
                        throw new Exception("no table");
                    }
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

    public static void create(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("create: invalid number of arguments");
        }
        File file = new File(path + File.separator + args[1]);
        if (file.exists()) {
            System.out.println(args[1] + " exists");
        } else {
            file.createNewFile();
            if (file.isDirectory()) {
                System.out.println("created");
            } else {
                throw new Exception(file.toString() + ": is not a directory");
            }
        }
    }

    public static void drop(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("create: invalid number of arguments");
        }
        File file = new File(path + File.separator + args[1]);
        if (!file.exists()) {
            System.out.println(args[1] + " not exists");
        } else {
            if (file.isDirectory()) {
                delete(file);
                System.out.println("dropped");
            } else {
                throw new Exception(file.toString() + ": is not a directory");
            }
        }
    }

    private static void delete(File f)
            throws Exception {
        File[] fList = f.listFiles();
        for (File i : fList) {
            delete(i);
        }
        if (!f.delete()) {
            throw new Exception("cannot delete");
        }
        return;
    }

    public static void use(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("create: invalid number of arguments");
        }
        if (curTable.length() != 0) {
            putMap();
        }
        File file = new File(path + File.separator + args[1]);
        if (!file.exists()) {
            System.out.println(args[1] + " not exists");
        }
        if (file.isDirectory()) {
            curTable = args[1];
            getMap(file);
            System.out.println("using " + args[1]);
        } else {
            throw new Exception(file.toString() + ": is not a directory");
        }
    }

    public static void getMap(File file)
            throws Exception {
        File[] fList = file.listFiles();
        for (File dir: fList) {
            if (dir.isDirectory()) {
                getFile(dir.toString());
            } else {
                throw new Exception (dir.getName() + " is not directory");
            }
        }

    }

    public static void getMaps()
            throws Exception {
        File file = new File(path);
        File[] fList = file.listFiles();
        for (File dir: fList) {
            if (dir.isDirectory()) {
                getTable(dir.toString());
            } else {
                throw new Exception (dir.getName() + " is not directory");
            }
        }

    }
    //на ввод путь к таблице
    private static void getTable(String tableDir) throws Exception {
        File tabDir = new File(tableDir);
        String tableName = tabDir.getName();
        if (!tableList.containsKey(tableName)) {
            tableList.put(tableName, 0);
        }
        File[] dirs = tabDir.listFiles();
        int i = 0;
        while (i < dirs.length) {
            if (!dirs[i].isDirectory()) {
                throw new Exception(dirs[i].getName() + " is not directory");
            }
            File[] dats = dirs[i].listFiles();
            int j = 0;
            while (j < dats.length) {
                if (!dats[j].getName().matches("[0 - 15].dat")) {
                    throw new Exception("Invalid file name");
                }
                int nDirectory = Integer.parseInt(dirs[i].getName().substring(0, dirs[i].getName().length() - 4));
                int nFile = Integer.parseInt(dats[j].getName().substring(0, dats[j].getName().length() - 4));
                String key;
                String value;
                RandomAccessFile file = new RandomAccessFile(dats[j].getAbsolutePath(), "r");
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
                        int hashcode = key.hashCode();
                        if (!(nDirectory == hashcode % 16) || !(nFile == hashcode / 16 % 16)) {
                            throw new Exception("Error with read table");
                        }
                        tableList.put(tableName, tableList.get(tableName) + 1);
                    } catch (IOException e) {
                        end = true;
                    }
                }
                file.close();
                j++;
            }
            i++;
        }
    }
    public static void getFile(String f) throws Exception {
        RandomAccessFile file = new RandomAccessFile(f, "r");
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

    public static void putMap() throws Exception {
        String key;
        String value;
        File table = new File(path + File.separator + curTable);
        File[] dirs = table.listFiles();
        for (File dir: dirs) {
            delete(dir);
            }
        for (Map.Entry<String, String> i : map.entrySet()) {
            key = i.getKey();
            value = i.getValue();
            int hashcode = key.hashCode();
            int ndirectory = hashcode % 16;
            int nfile = hashcode / 16 % 16;
            File file = new File(path + File.separator + curTable
                    + File.separator + ndirectory + ".dir" + File.separator + nfile + ".dat");
            if (!file.exists()) {
                file.createNewFile();
            }
            DataOutputStream outStream = new DataOutputStream(
                    new FileOutputStream(file));
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

    public  static void show(String[] args) throws Exception {
        System.out.println("table_name row_count");
        for (Map.Entry table : tableList.entrySet()) {
            System.out.println(table.getKey() + " " + table.getValue());
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
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + ", ");
        }
        System.out.println();
    }

    public static void exit(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("exit: invalid number of arguments");
        }
        if (curTable.length() != 0) {
            putMap();
        }
        map.clear();
        System.exit(0);
    }
}

