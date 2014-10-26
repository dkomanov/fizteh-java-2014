package ru.fizteh.fivt.students.alina_chupakhina.multyfilehashmap;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Table {
    private String tablename;
    private String path;
    private File table;
    private Map<String, String> fm;
    private int numberOfElements;
    private static final String INVALID_NUMBER_OF_ARGUMENTS_MESSAGE
            = ": Invalid number of arguments";
    private static final int MAGIC_NUMBER = 16;

    Table(String name, String pathname) throws Exception {
        fm = new TreeMap<>();
        numberOfElements = 0;
        path = pathname + File.separator + name;
        table = new File(path);
        tablename = name;
        getTable();
        rm();
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
                    System.out.println("Error while reading table " + tablename);
                }
            }
            if (!dir.delete()) {
                System.out.println("Error while reading table " + tablename);
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
                        int length = file.readInt();
                        byte[] bytes = new byte[length];
                        file.readFully(bytes);
                        key = new String(bytes, "UTF-8");
                        length = file.readInt();
                        bytes = new byte[length];
                        file.readFully(bytes);
                        value = new String(bytes, "UTF-8");
                        numberOfElements++;
                        fm.put(key, value);
                        if (!(nDirectory ==  Math.abs(key.getBytes("UTF-8")[0] % MAGIC_NUMBER))
                                || !(nFile == Math.abs((key.getBytes("UTF-8")[0] / MAGIC_NUMBER) % MAGIC_NUMBER))) {
                            System.err.println("Error while reading table " + tablename);
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


    private void putTable() throws Exception {
        String key;
        String value;
        for (Map.Entry<String, String> i : fm.entrySet()) {
            key = i.getKey();
            value = i.getValue();
            Integer ndirectory = Math.abs(key.getBytes("UTF-8")[0] % MAGIC_NUMBER);
            Integer nfile = Math.abs((key.getBytes("UTF-8")[0] / MAGIC_NUMBER) % MAGIC_NUMBER);
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
        System.out.println();
    }

    public void checkNumOfArgs(String operation,
              int correctValue, int testValue) throws IllegalArgumentException {
        if (testValue != correctValue) {
            throw new IllegalArgumentException(operation
                    + INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }
    }

    public void exit() throws Exception {
        putTable();
    }

    public String getName() {
        return tablename;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }
}


