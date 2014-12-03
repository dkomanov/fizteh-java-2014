package ru.fizteh.fivt.students.andrewzhernov.filemap;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.HashMap;

public class DataBase {
    private Map<String, String> dataBase;
    private String dbPath;

    public DataBase(String name) throws Exception {
        if (name == null) {
            throw new Exception("Usage: java -Ddb.file=<name> ...");
        }
        dataBase = new HashMap<String, String>();
        File dbFile = openFile(name);
        if (dbFile.isDirectory()) {
            throw new Exception("Can't create file, " + name + " is a directory");
        } else { 
            dbPath = dbFile.getCanonicalPath();
            if (dbFile.exists()) {
                loadFromDisk();
            }
        }
    }

    private static File openFile(String name) throws Exception {
        if (name.charAt(0) == File.separatorChar) {
            return new File(name);
        } else {
            return new File(System.getProperty("user.dir") + File.separator + name);
        }
    }

    private String readFromDataBase(RandomAccessFile file) throws Exception {
        int wordSize = file.readInt();
        byte[] word = new byte[wordSize];
        file.read(word, 0, wordSize);
        return new String(word);
    }

    private void writeToDataBase(RandomAccessFile file, String word) throws Exception {
        file.writeInt(word.getBytes("UTF-8").length);
        file.write(word.getBytes("UTF-8"));
    }

    public void loadFromDisk() throws Exception {
        RandomAccessFile file = new RandomAccessFile(dbPath, "r");
        while (file.getFilePointer() < file.length()) {
            String key = readFromDataBase(file);
            String value = readFromDataBase(file);
            dataBase.put(key, value);
        }
        file.close();
    }

    public void saveToDisk() throws Exception {
        RandomAccessFile file = new RandomAccessFile(dbPath, "rw");
        for (String key : dataBase.keySet()) {
            writeToDataBase(file, key);
            writeToDataBase(file, dataBase.get(key));
        }
        file.close();
    }

    public void put(String key, String value) {
        if (dataBase.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(dataBase.get(key));
            dataBase.remove(key);
            dataBase.put(key, value);
        } else {
            System.out.println("new");
            dataBase.put(key, value);
        }
    }

    public void get(String key) {
        if (dataBase.containsKey(key)) {
            System.out.println("found");
            System.out.println(dataBase.get(key));
        } else {
            System.out.println("not found");
        }
    }

    public void remove(String key) {
        if (dataBase.containsKey(key)) {
            System.out.println("removed");
            dataBase.remove(key);
        } else {
            System.out.println("not found");
        }
    }

    public void list() {
        System.out.println(String.join(", ", dataBase.keySet()));
    }
}
