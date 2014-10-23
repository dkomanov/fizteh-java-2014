package ru.fizteh.fivt.students.alina_chupakhina.multyfilehashmap;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Table {
    private static String tablename;
    private static String path;
    private static File table;
    private static Map<String, String> fm;
    private static int numberOfElements;
    private static final String INVALID_NUMBER_OF_ARGUMENTS_MESSAGE
            = "Invalid number of arguments";

    Table(String name, String pathname) throws Exception {
        fm = new TreeMap<String, String>();
        numberOfElements = 0;
        path = pathname + File.separator + name;
        table = new File(path);
        tablename = name;
        getTable();
        remove(table);
        table.mkdir();
    }
    public static void remove(final File file) throws Exception {
        if (file.isDirectory()) {
            File [] children = file.listFiles();
            if (children == null) {
                throw new Exception(
                        "rm: cannot remove");
            }
            for (int i = 0; i != children.length; i++) {
                remove(children[i]);
            }
        }
        if (!file.delete()) {
            throw new Exception(
                    "rm:" + file.getName() + ". Can not delete the file");
        }
        return;
    }

    private static void getTable() throws Exception {
        File[] dirs = table.listFiles();
        int i = 0;
        while (i < dirs.length) {
            if (!dirs[i].isDirectory()) {
                throw new Exception("create: " + dirs[i].getName()
                        + " is not directory");
            }
            File[] dats = dirs[i].listFiles();
            int j = 0;
            while (j < dats.length) {
                numberOfElements++;
                int nDirectory = Integer.parseInt(dirs[i].getName().substring(0,
                        dirs[i].getName().length() - 4));
                int nFile = Integer.parseInt(dats[j].getName().substring(0,
                        dats[j].getName().length() - 4));
                String key;
                String value;
                RandomAccessFile file
                        = new RandomAccessFile(dats[j].getAbsolutePath(), "r");
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
                        int hashcode = key.hashCode();
                        if (!(nDirectory == hashcode % 16)
                                || !(nFile == hashcode / 16 % 16)) {
                            throw new Exception("Error with read table");
                        }
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


    private static void putTable() throws Exception {
        String key;
        String value;
        for (Map.Entry<String, String> i : fm.entrySet()) {
            key = i.getKey();
            value = i.getValue();
            int hashcode = key.hashCode();
            Integer ndirectory = hashcode % 16;
            Integer nfile = hashcode / 16 % 16;
            String pathToDir = path + File.separator + ndirectory.toString()
                    + ".dir";
            //System.out.println(pathToDir);
            File file = new File(pathToDir);
            if (!file.exists()) {
                file.mkdir();
            }
            String pathToFile = path + File.separator + ndirectory.toString()
                    + ".dir" + File.separator + nfile.toString() + ".dat";
            //System.out.println(pathToFile);
            file = new File(pathToFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            DataOutputStream outStream  = new DataOutputStream(
                    new FileOutputStream(pathToFile));
            byte[] byteWord = key.getBytes("UTF-8");
            outStream.writeInt(byteWord.length);
            outStream.write(byteWord);
            outStream.flush();
            byteWord = value.getBytes("UTF-8");
            outStream.writeInt(byteWord.length);
            outStream.write(byteWord);
            outStream.flush();
            outStream.close();
        }
    }

    public static void put(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("put: "
                    + INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }
        String key = args[1];
        String value = args[2];
        String s = fm.put(key, value);
        if (s != null) {
            System.out.println("overwrite");
            System.out.println(s);
        } else {
            System.out.println("new");
            numberOfElements++;
        }
    }

    public static void get(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("get: "
                    + INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
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
            throw new IllegalArgumentException("remove: "
                    + INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }
        String key = args[1];
        String s = fm.remove(key);
        if (s != null) {
            System.out.println("removed");
            numberOfElements--;
        } else {
            System.out.println("not found");
        }
    }

    public static void list(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("list: "
                    + INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
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

    public static void exit() throws Exception {
        putTable();
    }

    public static String getName() {
        return tablename;
    }

    public static Integer getNumberOfElements() {
        return numberOfElements;
    }
}

