package ru.fizteh.fivt.students.LevkovMiron.JUnit;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by Мирон on 24.10.2014 ru.fizteh.fivt.students.LevkovMiron.JUnit.
 */
public class TableClass implements Table {
    private String dbName;
    private HashMap<String, String>[][] tableBeforeChanges;
    private HashMap<String, String>[][] tableAfterChanges;
    File parentDirectory;
    File tableDirectory;
    HashMap<File, Integer> tableRowCounter;

    public TableClass(File parDir, File tabDir) {
        parentDirectory = parDir;
        tableDirectory = tabDir;
        tableAfterChanges = new HashMap[20][20];
        tableBeforeChanges = new HashMap[20][20];
        dbName = parDir.getName();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                tableAfterChanges[i][j] = new HashMap<>();
                tableBeforeChanges[i][j] = new HashMap<>();
            }
        }
        tableRowCounter = new HashMap<File, Integer>();
        tableRowCounter.put(tableDirectory, 0);
        try {
            reloadTable();
        } catch (IOException e) {
            System.exit(-2);
        } catch (NullPointerException e) {
            System.exit(-3);
        }
    }



    void runCommand(String inString, final PrintStream stream) {
        try {
            inString = inString.trim();
            if (inString.equals("show tables")) {
                showTables();
                return;
            }
            String[] command = inString.split(" ");
            if (command[0].equals("use")) {
                use(command[1]);
            } else if (command[0].equals("exit")) {
                exit();
            } else if (command[0].equals("drop")) {
                File f = new File(parentDirectory.getAbsolutePath() + "/" + command[1]);
                if (f.exists()) {
                    drop(f);
                    tableRowCounter.remove(f);
                    System.out.println("dropped");
                } else {
                    System.out.println(command[1] + " doesn't exist");
                }
            } else if (command[0].equals("create")) {
                create(command[1]);
            } else {
                if (tableDirectory == null && (command[0].equals("put") || command[0].equals("get")
                        || command[0].equals("list") || command[0].equals("remove") || command[0].equals("size")
                        || command[0].equals("commit") || command[0].equals("rollback"))) {
                    System.out.println("No tables in usage");
                    return;
                }
                if (command[0].equals("put")) {
                    put(command[1], command[2]);
                } else if (command[0].equals("get")) {
                    get(command[1]);
                } else if (command[0].equals("remove")) {
                    remove(command[1]);
                } else if (command[0].equals("list")) {
                    list();
                } else if (command[0].equals("commit")) {
                    commit();
                } else if (command[0].equals("rollback")) {
                    rollback();
                } else if (command[0].equals("size")) {
                    size();
                } else {
                    System.out.println("Unknown command");
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Wrong command format: to few arguments\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    void create(String name) {
        File fileToCreate = new File(parentDirectory.getAbsolutePath() + "/" + name);
        if (fileToCreate.exists()) {
            return;
        }
        fileToCreate.mkdir();
        tableRowCounter.put(fileToCreate, 0);
    }
    public void use(String name) throws IOException {
        int change = changesNumber();
        if (change > 0) {
            System.out.println(change + " unsaved changes");
            return;
        }
        File newTable = new File(parentDirectory.getAbsolutePath() + "/" + name);
        if (!newTable.exists()) {
            System.out.println("Table " + name + " doesn't exist");
            return;
        }
        tableDirectory = newTable;
        reloadTable();
        System.out.println("Using " + name);
    }
    void reloadTable() throws IOException {
        tableRowCounter.put(tableDirectory, 0);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                tableBeforeChanges[i][j].clear();
                tableAfterChanges[i][j].clear();
                File tablePart = new File(tableDirectory.getAbsolutePath() + "/" + i + ".dir/" + j + ".dat");
                if (tablePart.exists()) {
                    try (FileInputStream inputStream = new FileInputStream(tablePart)) {
                        int sz = readInt(inputStream);
                        String key = readString(inputStream, sz);
                        int sz2 = readInt(inputStream);
                        String value = readString(inputStream, sz2);
                        tableAfterChanges[i][j].put(key, value);
                        tableBeforeChanges[i][j].put(key, value);
                        tableRowCounter.put(tableDirectory, tableRowCounter.get(tableDirectory) + 1);
                    } catch (IOException e) {
                        throw new IOException("Can't read the file");
                    }
                }
            }
        }
    }
    String readString(final FileInputStream inStream, int size) throws IOException, OutOfMemoryError {
        ArrayList<Byte> tempData = new ArrayList<Byte>();
        for (int i = 0; i < size; i++) {
            byte[] oneByte = new byte[1];
            if (inStream.read(oneByte) == -1) {
                throw new OutOfMemoryError("Incorrect data. Unexpected end of file.");
            }
            tempData.add(oneByte[0]);
        }
        try {
            byte[] utfData = new byte[size];
            for (int i = 0; i < tempData.size(); i++) {
                utfData[i] = tempData.get(i);
            }
            return new String(utfData, "UTF-8");
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Data is too large.");
        }
    }
    int readInt(final FileInputStream inStream) throws IOException, OutOfMemoryError {
        byte[] utfData = new byte[4];
        if (inStream.read(utfData) < 4) {
            throw new OutOfMemoryError("Unexpected end of file.");
        }
        int value = ByteBuffer.wrap(utfData).getInt();
        if (value < 0) {
            throw new IOException();
        }
        return value;
    }
    void writeIntAndString(FileOutputStream stream, String key) throws IOException {
        byte[] data = ByteBuffer.allocate(4).putInt(key.length()).array();
        stream.write(data);
        stream.write(key.getBytes("UTF-8"));
    }
    private int changesNumber() {
        int res = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (Map.Entry<String, String> pair : tableAfterChanges[i][j].entrySet()) {
                    if (!pair.getValue().equals(tableBeforeChanges[i][j].get(pair.getKey()))) {
                        res++;
                    }
                }
                for (Map.Entry<String, String> pair : tableBeforeChanges[i][j].entrySet()) {
                    if (!pair.getValue().equals(tableAfterChanges[i][j].get(pair.getKey()))) {
                        res++;
                    }
                }
            }
        }
        return res;
    }
    void rewrite() throws IOException {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (tableAfterChanges[i][j].size() > 0) {
                    File directory = new File(tableDirectory.getAbsolutePath() + "/" + i + ".dir");
                    directory.mkdir();
                    File f = new File(tableDirectory.getAbsolutePath() + "/" + i + ".dir/" + j + ".dat");
                    f.createNewFile();
                    rewriteFile(f, i, j);
                }
            }
        }
    }
    void rewriteFile(File file, int i, int j) throws IOException, OutOfMemoryError {
        FileOutputStream outStream = new FileOutputStream(file);
        for (Map.Entry<String, String> pair : tableBeforeChanges[i][j].entrySet()) {
            writeIntAndString(outStream, pair.getKey());
            writeIntAndString(outStream, pair.getValue());
        }
    }
    public void showTables() {
        for (File table : parentDirectory.listFiles()) {
            if (table.isDirectory()) {
                System.out.println(table.getName() + " " + tableRowCounter.get(table));
            }
        }
    }
    public void drop(File deletedFile) {
        if (!deletedFile.isDirectory()) {
            deletedFile.delete();
            return;
        }
        for (File f : deletedFile.listFiles()) {
            drop(f);
        }
        if (tableDirectory != null && tableDirectory.equals(deletedFile)) {
            tableRowCounter.remove(tableDirectory);
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    tableAfterChanges[i][j].clear();
                    tableBeforeChanges[i][j].clear();
                }
            }
            tableDirectory = null;
        }
        deletedFile.delete();
    }
    public void exit() {
        commit();
        System.exit(1);
    }

    @Override
    public int commit() {
        int result = changesNumber();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                tableBeforeChanges[i][j].clear();
            }
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (Map.Entry<String, String> pair : tableAfterChanges[i][j].entrySet()) {
                    tableBeforeChanges[i][j].put(pair.getKey(), pair.getValue());
                }
            }
        }
        for (File tablePart : tableDirectory.listFiles()) {
            drop(tablePart);
        }
        try {
            rewrite();
        } catch (IOException e) {
            System.exit(-3);
        }
        return result;
    }
    @Override
    public String getName() {
        return dbName;
    }
    @Override
    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int h1 = key.hashCode() % 16;
        int h2 = (key.hashCode() / 16) % 16;
        return tableAfterChanges[h1][h2].get(key);
    }
    @Override
    public int size() {
        return tableRowCounter.get(tableDirectory);
    }
    @Override
    public List<String> list() {
        Set<String> res = new HashSet<String>();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                res.addAll(tableAfterChanges[i][j].keySet());
            }
        }
        return new ArrayList<String>(res);
    }
    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        int h1 = key.hashCode() % 16;
        int h2 = (key.hashCode() / 16) % 16;
        if (!tableAfterChanges[h1][h2].containsKey(key)) {
            tableRowCounter.replace(tableDirectory, tableRowCounter.get(tableDirectory) + 1);
        }
        return tableAfterChanges[h1][h2].put(key, value);
    }
    @Override
    public int rollback() {
        int result = changesNumber();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                tableAfterChanges[i][j].clear();
            }
        }
        int size = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (Map.Entry<String, String> pair : tableBeforeChanges[i][j].entrySet()) {
                    tableAfterChanges[i][j].put(pair.getKey(), pair.getValue());
                    size++;
                }
            }
        }
        tableRowCounter.put(tableDirectory, size);
        return result;
    }
    @Override
    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int h1 = key.hashCode() % 16;
        int h2 = (key.hashCode() / 16) % 16;
        if (tableAfterChanges[h1][h2].containsKey(key)) {
            tableRowCounter.replace(tableDirectory, tableRowCounter.get(tableDirectory) - 1);
        }
        return tableAfterChanges[h1][h2].remove(key);
    }
}
