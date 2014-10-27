package ru.fizteh.fivt.students.andrewzhernov.multifilemap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.HashMap;

public class DataBase {
    private static final int COUNT = 16;
    
    private Map<String, Integer> recordsCount;
    private Map<String, String> table;
    private String name;
    private Path dir;

    public DataBase(String directory) throws Exception {
        if (directory == null) {
            throw new Exception("Usage: java -Dfizteh.db.dir=<name> ...");
        }
        recordsCount = new HashMap<String, Integer>();
        table = new HashMap<String, String>();
        dir = Paths.get(directory);
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);
        } else {
            for (String tablename : dir.toFile().list()) {
                reloadTable(tablename);
                recordsCount.put(tablename, table.size());
            }
            table.clear();
        }
        name = null;
    }

    private static String readString(RandomAccessFile file) throws Exception {
        int wordSize = file.readInt();
        byte[] word = new byte[wordSize];
        file.read(word, 0, wordSize);
        return new String(word);
    }

    private static void writeString(RandomAccessFile file, String word) throws Exception {
        file.writeInt(word.getBytes("UTF-8").length);
        file.write(word.getBytes("UTF-8"));
    }

    public void reloadTable(String tablename) throws Exception {
        table.clear();
        name = tablename;
        Path tablePath = dir.resolve(tablename);
        for (int i = 0; i < COUNT; ++i) {
            Path tableDir = tablePath.resolve(Integer.toString(i) + ".dir");
            if (Files.isDirectory(tableDir)) {
                for (int j = 0; j < COUNT; ++j) {
                    Path tableFile = tableDir.resolve(Integer.toString(j) + ".dat");
                    if (Files.exists(tableFile)) {
                        RandomAccessFile file = new RandomAccessFile(tableFile.toString(), "r");
                        while (file.getFilePointer() < file.length()) {
                            String key = readString(file);
                            String value = readString(file);
                            table.put(key, value);
                        }
                        file.close();
                    }
                }            
            }
        }
    }

    public void saveTable() throws Exception {
        if (name == null) {
            return;
        } 
        Path tablePath = dir.resolve(name);
        for (int i = 0; i < COUNT; ++i) {
            Path tableDir = tablePath.resolve(Integer.toString(i) + ".dir");
            if (!Files.isDirectory(tableDir)) {
                Files.createDirectory(tableDir);
            }
            for (int j = 0; j < COUNT; ++j) {
                Path tableFile = tableDir.resolve(Integer.toString(j) + ".dat");
                if (!Files.exists(tableFile)) {
                    Files.createFile(tableFile);
                }
                RandomAccessFile file = new RandomAccessFile(tableFile.toString(), "rw");
                boolean written = false;
                for (String key : table.keySet()) {
                    int first = key.hashCode() % COUNT;
                    int second = key.hashCode() / COUNT % COUNT;
                    if (i == first && j == second) {
                        writeString(file, key);
                        writeString(file, table.get(key));
                        written = true;
                    }
                }
                file.close();
                if (!written) {
                    Files.deleteIfExists(tableFile);
                }
            }
            if (tableDir.toFile().list().length == 0) {
                Files.deleteIfExists(tableDir);
            }
        }
    }

    public void create(String tablename) throws Exception {
        Path tablePath = dir.resolve(tablename);
        if (Files.isDirectory(tablePath)) {
            System.out.println("tablename exists");
        } else {
            Files.createDirectory(tablePath);
            recordsCount.put(tablename, 0);
            System.out.println("created");
        }
    }

    public void drop(String tablename) throws Exception {
        Path tablePath = dir.resolve(tablename);
        if (Files.isDirectory(tablePath)) {
            Utils.remove(tablePath);
            recordsCount.remove(tablename);
            if (name != null && name.equals(tablename)) {
                name = null;
            }
            System.out.println("dropped");
        } else {
            System.out.println("tablename not exists");
        }
    }

    public void use(String tablename) throws Exception {
        Path tablePath = dir.resolve(tablename);
        if (Files.isDirectory(tablePath)) {
            if (name != null) {
                saveTable();
            }
            reloadTable(tablename);
            System.out.println("using tablename");
        } else {
            System.out.println("tablename not exists");
        }
    }

    public void showTables() throws Exception {
        for (String tablename : recordsCount.keySet()) {
            System.out.format("%s %d\n", tablename, recordsCount.get(tablename));
        }
    }

    public void put(String key, String value) throws Exception {
        if (name == null) {
            throw new Exception("The table is not selected");
        }
        if (table.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(table.get(key));
            table.put(key, value);
        } else {
            table.put(key, value);
            recordsCount.put(name, recordsCount.get(name) + 1);
            System.out.println("new");
        }
    }

    public void get(String key) throws Exception {
        if (name == null) {
            throw new Exception("The table is not selected");
        }
        if (table.containsKey(key)) {
            System.out.println("found");
            System.out.println(table.get(key));
        } else {
            System.out.println("not found");
        }
    }

    public void remove(String key) throws Exception {
        if (name == null) {
            throw new Exception("The table is not selected");
        }
        if (table.containsKey(key)) {
            table.remove(key);
            recordsCount.put(name, recordsCount.get(name) - 1);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

    public void list() throws Exception {
        if (name == null) {
            throw new Exception("The table is not selected");
        }
        System.out.println(String.join(", ", table.keySet()));
    }
}
