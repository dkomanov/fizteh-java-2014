package ru.fizteh.fivt.students.pershik.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 * Created by pershik on 11/9/14.
 */
public class StoreableTable implements Table {

    private final int mod = 16;

    private String dbDirPath;
    private List<Class<?>> signature;
    private String name;
    private StoreableTableProvider provider;
    private Map<String, Storeable> db;
    private int uncommitted;

    public StoreableTable(StoreableTableProvider newProvider,  String dbName,
                          String parentDir, List<Class<?>> newSignature) {
        name = dbName;
        dbDirPath = parentDir + File.separator + dbName;
        db = new HashMap<>();
        signature = newSignature;
        provider = newProvider;
        readDb();
    }

    @Override
    public Storeable put(String key, Storeable value)
            throws IllegalArgumentException {
        checkExistance();
        checkArg("key", key);
        checkArg("value", value);
        checkSignature(value);
        uncommitted++;
        return db.put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        checkExistance();
        checkArg("key", key);
        if (db.containsKey(key)) {
            uncommitted++;
        }
        return db.remove(key);
    }

    @Override
    public int size() {
        checkExistance();
        return db.size();
    }

    @Override
    public int commit() throws IOException {
        checkExistance();
        int res = uncommitted;
        uncommitted = 0;
        writeDb();
        return res;
    }

    @Override
    public int rollback() {
        checkExistance();
        int res = uncommitted;
        uncommitted = 0;
        readDb();
        return res;
    }

    @Override
    public int getColumnsCount() {
        checkExistance();
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex)
            throws IndexOutOfBoundsException {
        checkExistance();
        if (columnIndex >= getColumnsCount()) {
            throw new IndexOutOfBoundsException("ColumnIndex is out of bounds");
        }
        return signature.get(columnIndex);
    }

    @Override
    public String getName() {
        checkExistance();
        return name;
    }

    @Override
    public Storeable get(String key)
            throws IllegalArgumentException {
        checkExistance();
        checkArg("key", key);
        return db.get(key);
    }

    public List<String> list() {
        return new ArrayList<>(db.keySet());
    }

    private void writeDb() throws IOException {
        removeFromDisk();
        Map<String, Storeable>[][] dbParts = new HashMap[mod][mod];
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
    }

    private void writeFile(
            Map<String, Storeable> dbPart, File dir, File file)
            throws IOException {
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
                String strValue = provider.serialize(this, dbPart.get(key));
                writeToken(stream, strValue);
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
                if ("signature.tsv".equals(dir.getName())) {
                    continue;
                }
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
        try {
            try (DataInputStream stream = new DataInputStream(
                    new FileInputStream(file))) {
                while (true) {
                    String key = readToken(stream);
                    String valueStr = readToken(stream);
                    if (key == null || valueStr == null) {
                        break;
                    }
                    Storeable value = provider.deserialize(this, valueStr);
                    checkSignature(value);
                    db.put(key, value);
                }
            }
        } catch (ParseException e) {
            throw new IOException("Invalid db directory");
        } catch (ColumnFormatException e) {
            throw new IOException("Invalid db format");
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
                    throw new IOException("Can't remove from disk");
                }
            }
            File curDir = new File(pathDir);
            if (curDir.exists() && !curDir.delete()) {
                throw new IOException("Can't remove from disk");
            }
        }
    }

    private void checkSignature(Storeable storeable) {
        try {
            for (int i = 0; i < signature.size(); i++) {
                if (storeable.getColumnAt(i) == null) {
                    continue;
                }
                if (!storeable.getColumnAt(i).getClass().equals(signature.get(i))) {
                    throw new ColumnFormatException("Invalid column format");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("Invalid column number");
        }
    }

    private void checkExistance() throws IllegalStateException {
        if (!provider.contains(name)) {
            throw new IllegalStateException("Table doesn't exist anymore");
        }
    }

    private void checkArg(String name, Object value)
            throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(name + " shouldn't be null");
        }
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return uncommitted;
    }
}
