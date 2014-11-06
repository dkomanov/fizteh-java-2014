package ru.fizteh.fivt.students.anastasia_ermolaeva.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.DatabaseIOException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.Utility;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DBTable implements Table {
    static final String DIR_SUFFIX = ".dir";
    static final String FILE_SUFFIX = ".dat";
    static final String ENCODING = "UTF-8";
    static final int START_P = 0;
    static final int DIR_AMOUNT = 16;
    static final int FILES_AMOUNT = 16;
    /*
    * Records readed from real file.
    */
    private Map<String, String> allRecords;
    /*
    * Writes changes during session .
    */
    private Map<String, String> sessionChanges;
    /*
    * Path to table's directory.
    */
    private Path dbPath;
    private String name;

    public DBTable(final Path rootPath, final String name) {
        dbPath = rootPath.resolve(name);
        this.name = name;
        allRecords = new HashMap<>();
        sessionChanges = new HashMap<>();
        read();
    }

    public DBTable(final Path rootPath, final String name,
                   final Map<String, String> records) {
        dbPath = rootPath.resolve(name);
        this.name = name;
        allRecords = records;
        sessionChanges = new HashMap<>();
    }

    public final Path getDBTablePath() {
        return dbPath;
    }

    private String readUtil(final RandomAccessFile dbFile, final String fileName) {
        byte[] word = null;
        try {
            int wordLength = dbFile.readInt();
            if (wordLength <= 0) {
                throw new DatabaseIOException(fileName + ": invalid file format");
            }
            word = new byte[wordLength];
            dbFile.read(word, 0, wordLength);
            return new String(word, ENCODING);
        } catch (IOException | OutOfMemoryError e) {
            throw new DatabaseIOException(fileName + ": invalid file format");
        }
    }

    private void read() {
        Utility.checkDirectorySubdirs(dbPath);
        try {
            for (File directory : dbPath.toFile().listFiles()) {
                try {
                    File[] directoryFiles = directory.listFiles();
                    int k = directory.getName().indexOf('.');
                    if ((k < 0) || !(directory.getName().substring(k).equals(DIR_SUFFIX))) {
                        throw new DatabaseIOException("Table subdirectories don't "
                                + "have appropriate name");
                    }
                    try {
                    /*
                    * Delete .dir and check
                    * if the subdirectory has the suitable name.
                     */
                        if (directory.list().length == 0) {
                            throw new DatabaseIOException(
                                    "Table has the wrong format");
                        }
                        int nDirectory = Integer.parseInt(directory.getName().substring(START_P, k));
                        for (File file : directoryFiles) {
                            try {
                                k = file.getName().indexOf('.');
                        /*
                        Checking files' names the same way
                        we did with directories earlier.
                        */
                                if ((k < 0)
                                        || !(file.getName().
                                        substring(k).equals(FILE_SUFFIX))) {
                                    throw new DatabaseIOException(
                                            "Table subdirectory's files doesn't "
                                                    + "have appropriate name");
                                }
                                int nFile = Integer.parseInt(file.getName().substring(START_P, k));
                                try (RandomAccessFile dbFile =
                                             new RandomAccessFile(
                                                     file.getAbsolutePath(), "r")) {
                                    if (dbFile.length() > 0) {
                                        while (dbFile.getFilePointer()
                                                < dbFile.length()) {
                                            String key = readUtil(dbFile, file.getName());
                                            int expectedNDirectory = Math.abs(key.getBytes(ENCODING)[0]
                                                    % DIR_AMOUNT);
                                            int expectedNFile = Math.abs((key.getBytes(ENCODING)[0] / DIR_AMOUNT)
                                                    % FILES_AMOUNT);
                                            String value = readUtil(dbFile, file.getName());
                                            if (expectedNDirectory == nDirectory
                                                    && expectedNFile == nFile) {
                                                allRecords.put(key, value);
                                            } else {
                                                throw new DatabaseIOException("Records locates"
                                                        + " in the wrong file");
                                            }
                                        }
                                    }
                                    dbFile.close();
                                } catch (IOException e) {
                                    throw new DatabaseIOException(e.getMessage());
                                }
                            } catch (NumberFormatException e) {
                                throw new DatabaseIOException("Subdirectories' files "
                                        + "have wrong names, "
                                        + "expected: " + START_P + FILE_SUFFIX
                                        + (FILES_AMOUNT - 1) + FILE_SUFFIX);
                            }
                        }
                    } catch (NumberFormatException e) {
                        throw new
                                DatabaseIOException("Subdirectories' names "
                                + "are wrong, "
                                + "expected: " + START_P + DIR_SUFFIX
                                + (DIR_AMOUNT - 1) + DIR_SUFFIX);
                    }
                } catch (NullPointerException n) {
                    new DatabaseIOException("Access forbidden");
                }
            }
        } catch (NullPointerException n) {
            throw new DatabaseIOException("Access forbidden");
        }
    }

    private void write() {
        Map<String, String>[][] db = new Map[DIR_AMOUNT][FILES_AMOUNT];
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                db[i][j] = new HashMap<>();
            }
        }
        for (Map.Entry<String, String> entry : allRecords.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                int nDirectory = Math.abs(key.getBytes(ENCODING)[0]
                        % DIR_AMOUNT);
                int nFile = Math.abs((key.getBytes(ENCODING)[0] / DIR_AMOUNT)
                        % FILES_AMOUNT);
                db[nDirectory][nFile].put(key, value);
            } catch (UnsupportedEncodingException e) {
                throw new DatabaseIOException(e.getMessage());
            }
        }
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                if (!db[i][j].isEmpty()) {
                    int nDirectory = i;
                    int nFile = j;
                    Path newPath = dbPath.resolve(nDirectory + DIR_SUFFIX);
                    File directory = newPath.toFile();
                    if (!directory.exists()) {
                        try {
                            Files.createDirectory(newPath);
                        } catch (IOException e) {
                            throw new DatabaseIOException(e.getMessage());
                        }
                    }
                    Path newFilePath = directory.toPath().
                            resolve(nFile + FILE_SUFFIX);
                    try {
                        Files.deleteIfExists(newFilePath);
                        Files.createFile(newFilePath);
                        RandomAccessFile dbFile = new
                                RandomAccessFile(newFilePath.toFile(), "rw");
                        dbFile.setLength(0);
                        for (Map.Entry<String, String> entry
                                : db[i][j].entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            writeUtil(key, dbFile);
                            writeUtil(value, dbFile);
                        }
                        dbFile.close();
                    } catch (IOException e) {
                        throw new DatabaseIOException(e.getMessage());
                    }
                } else {
                    /*
                    *Deleting empty files and directories.
                    */
                    int nDirectory = i;
                    int nFile = j;
                    Path newPath = dbPath.resolve(nDirectory + DIR_SUFFIX);
                    File directory = newPath.toFile();
                    if (directory.exists()) {
                        Path newFilePath = directory.toPath().
                                resolve(nFile + FILE_SUFFIX);
                        File file = newFilePath.toFile();
                        try {
                            Files.deleteIfExists(file.toPath());
                            if (directory.list().length == 0) {
                                Files.delete(directory.toPath());
                            }
                        } catch (IOException | SecurityException e) {
                            throw new DatabaseIOException(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void writeUtil(final String word,
                           final RandomAccessFile dbFile) {
        try {
            dbFile.writeInt(word.getBytes(ENCODING).length);
            dbFile.write(word.getBytes(ENCODING));
        } catch (IOException e) {
            throw new DatabaseIOException(e.getMessage());
        }
    }

    public int getNumberOfChanges() {
        return sessionChanges.size();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        if (sessionChanges.containsKey(key)) {
            return sessionChanges.get(key);
        } else {
            return allRecords.get(key);
        }
    }

    private Map<String, String> makeRelevantVersion() {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.putAll(allRecords);
        for (Map.Entry<String, String> entry : sessionChanges.entrySet()) {
            if (resultMap.containsKey(entry.getKey())) {
                /*
                * If the record was deleted during the session.
                */
                if (entry.getValue() == null) {
                    resultMap.remove(entry.getKey());
                } else {
                    /*
                    * If the value was changed during the session.
                    */
                    resultMap.put(entry.getKey(),
                            entry.getValue());
                }
            } else {
                if (entry.getValue() != null) {
                    resultMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return resultMap;
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new
                    IllegalArgumentException("Key and/or value "
                    + "is a null-string");
        }
        /*
        * The record with key has already been changed
        * or added during the session.
        */
        if (sessionChanges.containsKey(key)) {
            return sessionChanges.put(key, value);
        }
        /*
        *The record with key hasn't been changed yet.
        */
        if (allRecords.containsKey(key)) {
            sessionChanges.put(key, value);
            return allRecords.get(key);
        }
        /*
        * Absolutely new record.
        */
        return sessionChanges.put(key, value);
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        if (sessionChanges.containsKey(key)) {
            /*
            * The record with key has been deleted during the session.
            */
            if (sessionChanges.get(key) == null) {
                return null;
            } else {
                /*
                * The record with key has already been changed
                * or added during the session.
                */
                return sessionChanges.put(key, null);
            }
        } else {
            if (allRecords.containsKey(key)) {
                sessionChanges.put(key, null);
                return allRecords.get(key);
            }
            return null;
        }
    }

    @Override
    public int size() {
        return makeRelevantVersion().size();
    }

    @Override
    public int commit() {
        int numberOfChanges = sessionChanges.size();
        /* 
        * Execute changes to disk
        */
        if (numberOfChanges != 0) {
            allRecords = makeRelevantVersion();
            write();
            sessionChanges.clear();
        }
        return numberOfChanges;
    }

    @Override
    public int rollback() {
        int numberOfChanges = sessionChanges.size();
        sessionChanges.clear();
        return numberOfChanges;
    }

    @Override
    public List<String> list() {
        Map<String, String> relevantMap = makeRelevantVersion();
        List<String> list = new ArrayList<>();
        list.addAll(relevantMap.keySet());
        return list;
    }
}
