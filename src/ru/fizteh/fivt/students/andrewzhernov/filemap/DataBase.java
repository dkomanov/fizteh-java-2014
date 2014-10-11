package ru.fizteh.fivt.students.andrewzhernov.filemap;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;

public class DataBase {
    private HashMap<String, String> dataBase;
    private String dbPath;

    public static File openFile(String name) {
        if (name.charAt(0) == File.separatorChar) {
            return new File(name);
        } else {
            return new File(System.getProperty("user.dir") + File.separator + name);
        }
    }

    public DataBase(String name) throws Exception {
        dataBase = new HashMap<String, String>();
        File path = openFile(name);
        if (path.isDirectory()) {
            throw new Exception("database: it is a directory");
        } else { 
            dbPath = path.getCanonicalPath();
            if (path.exists()) {
                RandomAccessFile file = new RandomAccessFile(dbPath, "r");
                while (file.getFilePointer() < file.length()) {
                    String key = readFromDataBase(file);
                    String value = readFromDataBase(file);
                    dataBase.put(key, value);
                }
                file.close();
            }
        }
    }

    private String readFromDataBase(RandomAccessFile file) throws Exception {
        try {
            int wordSize = file.readInt();
            byte[] word = new byte[wordSize];
            file.read(word, 0, wordSize);
            return new String(word);
        } catch (Exception e) {
            throw new Exception("database: input error");
        }
    }

    private void writeToDataBase(RandomAccessFile file, String word) throws Exception {
        try {
            file.writeInt(word.getBytes("UTF-8").length);
            file.write(word.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new Exception("database: output error");
        }
    }

    public void writeInFile() throws Exception {
        try {
            RandomAccessFile file = new RandomAccessFile(dbPath, "rw");
            Iterator<HashMap.Entry<String, String>> it = dataBase.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry<String, String> pairs = it.next();
                writeToDataBase(file, pairs.getKey());
                writeToDataBase(file, pairs.getValue());
            }
            file.close();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
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
        for (String key : dataBase.keySet()) {
            System.out.print(key + " ");
        }
        System.out.println();
    }
}
