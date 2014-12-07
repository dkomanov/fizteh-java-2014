package ru.fizteh.fivt.students.pershik.JUnit;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pershik on 10/27/14.
 */
public class HashTable implements Table {

    public HashTable(String dbName, String parentDir) {
        name = dbName;
        dbDirPath = parentDir + File.separator + dbName;
        db = new HashMap<>();
        readDb();
    }

    private final int mod = 16;
    private String name;
    private Map<String, String> db;
    private int uncommitted;
    private String dbDirPath;

    public int getUncommittedCnt() {
        return uncommitted;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        checkArg("key", key);
        return db.get(key);
    }

    @Override
    public String put(String key, String value) {
        checkArg("key", key);
        checkArg("value", value);
        uncommitted++;
        return db.put(key, value);
    }

    @Override
    public String remove(String key) {
        checkArg("key", key);
        if (db.containsKey(key)) {
            uncommitted++;
        }
        return db.remove(key);
    }

    @Override
    public int size() {
        return db.size();
    }

    @Override
    public int commit() {
        int res = uncommitted;
        uncommitted = 0;
        writeDb();
        return res;
    }

    @Override
    public int rollback() {
        int res = uncommitted;
        uncommitted = 0;
        readDb();
        return res;
    }

    @Override
    public List<String> list() {
        return new ArrayList<>(db.keySet());
    }

    private void checkArg(String name, String value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(name + " shouldn't be null");
        }
    }

    private void writeDb() {
        try {
            removeFromDisk();
            Map<String, String>[][] dbParts = new HashMap[mod][mod];
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < mod; j++) {
                    dbParts[i][j] = new HashMap<>();
                }
            }
            for (String key : db.keySet()) {
                int hashCode = key.hashCode();
                int dirNumber = hashCode % mod;
                if (dirNumber < 0) {
                    dirNumber += 16;
                }
                int fileNumber = hashCode / mod % mod;
                if (fileNumber < 0) {
                    fileNumber += 16;
                }
                dbParts[dirNumber][fileNumber].put(key, db.get(key));
            }
            for (int i = 0; i < mod; i++) {
                String dirPath = dbDirPath + File.separator
                        + Integer.toString(i) + ".dir";
                File dir = new File(dirPath);
                for (int j = 0; j < mod; j++) {
                    String path = dirPath + File.separator
                            + Integer.toString(j) + ".dat";
                    File file = new File(path);
                    writeFile(dbParts[i][j], dir, file);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write db to file");
        }
    }

    private void writeFile(
            Map<String, String> dbPart, File dir, File file) throws IOException {
        if (dbPart.isEmpty()) {
            return;
        }
        if (!dir.mkdir()) {
            throw new IOException("Can't write db");
        }

        try (DataOutputStream stream = new DataOutputStream(
                new FileOutputStream(file))) {
            for (String key : dbPart.keySet()) {
                writeToken(stream, key);
                writeToken(stream, dbPart.get(key));
            }
        }
    }

    private void writeToken(DataOutputStream stream, String str)
            throws IOException {
        byte[] strBytes = str.getBytes("UTF-8");
        stream.writeInt(strBytes.length);
        stream.write(strBytes);
    }

    private void readDb() {
        try {
            db = new HashMap<>();
            File dbDir = new File(dbDirPath);
            String[] subdirectories = dbDir.list();
            for (String subdirectory : subdirectories) {
                String dirPath = dbDirPath + File.separator + subdirectory;
                File dir = new File(dirPath);
                if (!dir.isDirectory() || !isCorrectName(subdirectory, ".dir")
                        || !isCorrectSubdirectory(dir)) {
                    throw new RuntimeException("Incorrect database directory");
                }
                String[] files = dir.list();
                for (String file : files) {
                    String filePath = dirPath + File.separator + file;
                    readFile(new File(filePath));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read db from file");
        }
    }

    private void readFile(File file) throws IOException {
        try (DataInputStream stream = new DataInputStream(
                new FileInputStream(file))) {
            while (true) {
                String key = readToken(stream);
                String value = readToken(stream);
                if (key == null || value == null) {
                    break;
                }
                db.put(key, value);
            }
        }
    }

    private String readToken(DataInputStream stream) throws IOException {
        if (stream.available() == 0) {
            return null;
        }
        int size = stream.readInt();
        byte[] buf = new byte[size];
        stream.readFully(buf);
        return new String(buf, "UTF-8");
    }

    private boolean isCorrectSubdirectory(File dir) {
        String[] files = dir.list();
        for (String file : files) {
            File curFile = new File(file);
            if (curFile.isDirectory() || !isCorrectName(file, ".dat")) {
                return false;
            }
        }
        return true;
    }

    private boolean isCorrectName(String name, String suf) {
        try {
            if (name.length() < 4) {
                return false;
            }
            if (!name.endsWith(suf)) {
                return false;
            }
            name = name.replace(suf, "");
            int num = Integer.parseInt(name);
            return (0 <= num && num <= 15);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void removeFromDisk() throws IOException {
        String pathTable = dbDirPath;
        for (int i = 0; i < mod; i++) {
            String pathDir = pathTable + File.separator
                    + Integer.toString(i) + ".dir";
            for (int j = 0; j < mod; j++) {
                String pathFile = pathDir + File.separator
                        + Integer.toString(j) + ".dat";
                File curFile = new File(pathFile);
                if (curFile.exists() && !curFile.delete()) {
                    throw new IOException();
                }
            }
            File curDir = new File(pathDir);
            if (curDir.exists() && !curDir.delete()) {
                throw new IOException();
            }
        }
    }
}
