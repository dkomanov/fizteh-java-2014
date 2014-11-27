package ru.fizteh.fivt.students.olga_chupakhina.multyfilehashmap;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Table {
    private String tableName;
    private String path;
    private File table;
    private Map<String, String> fm;
    private int numberOfElements;

    Table(String name, String pathname) throws Exception {
        fm = new TreeMap<>();
        numberOfElements = 0;
        path = pathname + File.separator + name;
        table = new File(path);
        tableName = name;
        getTable();
    }

    public void rm() throws Exception {
        File[] dirs = this.table.listFiles();
        for (File dir : dirs) {
            if (!dir.isDirectory()) {
                throw new Exception(dir.getName()
                        + " is not directory");
            }
            File[] dats = dir.listFiles();
            if (dats.length == 0) {
                System.err.println("Empty folders found");
                System.exit(-1);
            }
            for (File dat : dats) {
                if (!dat.delete()) {
                    System.out.println("Error while reading table " + tableName);
                }

            }
            if (!dir.delete()) {
                System.out.println("Error while reading table " + tableName);
            }
        }
    }

    private void getTable() throws Exception {
        File[] dirs = this.table.listFiles();
        for (File dir : dirs) {
            if (!dir.isDirectory()) {
                throw new Exception(dir.getName()
                        + " is not directory");
            }
            File[] dats = dir.listFiles();
            if (dats.length == 0) {
                System.err.println("Empty folders found");
                System.exit(-1);
            }
            for (File dat : dats) {
                int nDirectory = Integer.parseInt(dir.getName().substring(0,
                        dir.getName().length() - 4));
                int nFile = Integer.parseInt(dat.getName().substring(0,
                        dat.getName().length() - 4));
                String key;
                String value;
                RandomAccessFile file
                        = new RandomAccessFile(dat.getAbsolutePath(), "r");
                boolean end = false;
                while (!end) {
                    try {
                        key = readValue(file);
                        value = readValue(file);
                        numberOfElements++;
                        fm.put(key, value);
                        if (!(nDirectory ==  Math.abs(key.getBytes("UTF-8")[0] % 16))
                                || !(nFile == Math.abs((key.getBytes("UTF-8")[0]
                                / 16) % 16))) {
                            System.err.println("Error while reading table " + tableName);
                            System.exit(-1);
                        }
                    } catch (IOException e) {
                        end = true;
                    }
                }
                file.close();
            }
        }
    }

    private String readValue(RandomAccessFile file) throws Exception {
        int length = file.readInt();
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        String value = new String(bytes, "UTF-8");
        return value;
    }

    private void putTable() throws Exception {
        String key;
        String value;
        rm();
        for (Map.Entry<String, String> i : fm.entrySet()) {
            key = i.getKey();
            value = i.getValue();
            Integer ndirectory = Math.abs(key.getBytes("UTF-8")[0] % 16);
            Integer nfile = Math.abs((key.getBytes("UTF-8")[0] / 16) % 16);
            String pathToDir = path + File.separator + ndirectory.toString()
                    + ".dir";
            File file = new File(pathToDir);
            if (!file.exists()) {
                file.mkdir();
            }
            String pathToFile = path + File.separator + ndirectory.toString()
                    + ".dir" + File.separator + nfile.toString() + ".dat";
            file = new File(pathToFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            DataOutputStream outStream  = new DataOutputStream(
                    new FileOutputStream(pathToFile, true));
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

    public void put(String[] args) throws Exception {
        checkNumOfArgs("put", 3, args.length);
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

    public void get(String[] args) throws Exception {
        checkNumOfArgs("get", 2, args.length);
        String key = args[1];
        String s = fm.get(key);
        if (s != null) {
            System.out.println("found");
            System.out.println(s);
        } else {
            System.out.println("not found");
        }
    }

    public void remove(String[] args) throws Exception {
        checkNumOfArgs("remove", 2, args.length);
        String key = args[1];
        String s = fm.remove(key);
        if (s != null) {
            System.out.println("removed");
            numberOfElements--;
        } else {
            System.out.println("not found");
        }
    }

    public void list(String[] args) throws Exception {
        checkNumOfArgs("list", 1, args.length);
        Set<String> keySet = fm.keySet();
        int counter = 0;
        for (String current : keySet) {
            ++counter;
            System.out.print(current);
            if (counter != keySet.size()) {
                System.out.print(", ");
            }
        }
    }

    public void checkNumOfArgs(String operation,
                               int correctValue, int testValue) throws IllegalArgumentException {
        if (testValue != correctValue) {
            throw new IllegalArgumentException(operation
                    + ": Invalid number of arguments");
        }
    }

    public void exit() throws Exception {
        putTable();
    }

    public String getName() {
        return tableName;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }
}
