package ru.fizteh.fivt.students.anastasia_ermolaeva.junit;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.util.ExitException;

public class DBTable implements Table {
    private static final int DIR_AMOUNT = 16;
    private static final int FILES_AMOUNT = 16;
    private Map<String, String> allRecords; //Records readed from real file
    private Map<String, String> sessionChanges;
    private Path dbPath; // The table's directory.
    private String name;

    public DBTable(final Path rootPath, final String name) {
        dbPath = rootPath.resolve(name);
        this.name = name;
        allRecords = new HashMap<>();
        sessionChanges = new HashMap<>();
        create();
    }

    public DBTable(final Path rootPath, final String name,
                   final Map<String, String> records) {
        dbPath = rootPath.resolve(name);
        this.name = name;
        allRecords = Collections.synchronizedMap(records);
        sessionChanges = new HashMap<>();
    }

    public static void main() {
        //
    }
    private void create() {
        try {
            read();
        } catch (ExitException e) {
            System.exit(e.getStatus());
        }
    }
    public final Path getDBTablePath() {
        return dbPath;
    }

    private String readUtil(final RandomAccessFile dbFile)
            throws ExitException {
        try {
            int wordLength = dbFile.readInt();
            byte[] word = new byte[wordLength];
            dbFile.read(word, 0, wordLength);
            return new String(word, "UTF-8");
        } catch (IOException | SecurityException e) {
            System.err.println("Error reading the table");
            throw new ExitException(1);
        }
    }

    private void read() throws ExitException {
        File pathDirectory = dbPath.toFile();
        if (pathDirectory.list()== null ||pathDirectory.list().length == 0)
            return;
        File[] tableDirectories = pathDirectory.listFiles();
        for (File t : tableDirectories) {
            // Checking subdirectories.
            if (!t.isDirectory()) {
                throw new IllegalStateException("Table subdirectories "
                           + "are not actually directories");
            }
        }
        for (File directory : tableDirectories) {
            File[] directoryFiles = directory.listFiles();
            int k = directory.getName().indexOf('.');
            if ((k < 0) || !(directory.getName().substring(k).equals(".dir"))) {
                throw new IllegalStateException("Table subdirectories don't "
                               + "have appropriate name");
            }
            try {
                /*
                Delete .dir and check(automatically )
                if the subdirectory has the suitable name.
                If not, then parseInt throws NumberFormatException,
                error message is shown.
                Then program would finish with exit code != 0.
                 */
                if (directory.list().length == 0) {
                    throw new IllegalStateException
                            ("Table has the wrong format");
                }
                int nDirectory = Integer.parseInt(
                        directory.getName().substring(0, k));
                for (File file : directoryFiles) {
                    try {
                        k = file.getName().indexOf('.');
                        /*
                        Checking files' names the same way
                        we did with directories earlier.
                        */
                        if ((k < 0)
                                || !(file.getName().
                                substring(k).equals(".dat"))) {
                            throw new IllegalStateException
                                    ("Table subdirectory's files doesn't "
                                         + "have appropriate name");
                        }
                        int nFile = Integer.parseInt(
                                file.getName().substring(0, k));
                        try (RandomAccessFile dbFile =
                                     new RandomAccessFile
                                             (file.getAbsolutePath(), "r")) {
                            if (dbFile.length() > 0) {
                                while (dbFile.getFilePointer()
                                        < dbFile.length()) {
                                    String key = readUtil(dbFile);
                                    String value = readUtil(dbFile);
                                    allRecords.put(key, value);
                                }
                            }
                            dbFile.close();
                        } catch (IOException e) {
                            System.err.println("Error reading to table");
                            throw new ExitException(1);
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException("Subdirectories' files "
                                    + "have wrong names, "
                                     + "expected(0.dat-15.dat)");
                    }
                }
            } catch (NumberFormatException e) {
                throw new
                        IllegalStateException("Subdirectories' names "
                        + "are wrong, "
                        + "expected(0.dir - 15.dir)");
            }
        }
    }

    private void write(Map<String, String> records) throws ExitException {
        Map<String, String>[][] db = new Map[DIR_AMOUNT][FILES_AMOUNT];
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                db[i][j] = new HashMap<>();
            }
        }
        for (Map.Entry<String, String> entry : records.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                int nDirectory = Math.abs(key.getBytes("UTF-8")[0]
                        % DIR_AMOUNT);
                int nFile = Math.abs((key.getBytes("UTF-8")[0] / DIR_AMOUNT)
                        % FILES_AMOUNT);
                db[nDirectory][nFile].put(key, value);
            } catch (UnsupportedEncodingException e) {
                System.err.println("Can't encode the record");
                throw new ExitException(1);
            }
        }
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                if (!db[i][j].isEmpty()) {
                    Integer nDirectory = i;
                    Integer nFile = j;
                    Path newPath = dbPath.resolve(nDirectory.toString()+".dir");
                    File directory = newPath.toFile();
                    if (!directory.exists()) {
                        if (!directory.mkdir()) {
                            System.err.println("Cannot create directory");
                            throw new ExitException(1);
                        }
                    }
                    Path newFilePath = directory.toPath().
                            resolve(nFile.toString() + ".dat");
                    File file = newFilePath.toFile();
                    try {
                        file.createNewFile();
                    } catch (IOException | SecurityException e) {
                        System.err.println(e.getMessage());
                        throw new ExitException(1);
                    }
                    try (RandomAccessFile dbFile = new
                            RandomAccessFile(file, "rw")) {
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
                        System.err.println(e.getMessage());
                        throw new ExitException(1);
                    }
                } else {
                    //Deleting empty files and directories.
                    Integer nDirectory = i;
                    Integer nFile = j;
                    Path newPath = dbPath.resolve(nDirectory.toString()+".dir");
                    File directory = newPath.toFile();
                    if (directory.exists()) {
                        Path newFilePath = directory.toPath().
                                resolve(nFile.toString() + ".dat");
                        File file = newFilePath.toFile();
                        try {
                            Files.deleteIfExists(file.toPath());
                        } catch (IOException | SecurityException e) {
                            System.err.println(e);
                            throw new ExitException(1);
                        }
                        if (directory.list().length == 0) {
                            try {
                                Files.delete(directory.toPath());
                            } catch (IOException e) {
                                System.err.println(e);
                                throw new ExitException(1);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                db[i][j].clear();
            }
        }
    }

    public final void close() {
        commit();
    }

    private void writeUtil(final String word,
                           final RandomAccessFile dbFile) throws ExitException {
        try {
            dbFile.writeInt(word.getBytes("UTF-8").length);
            dbFile.write(word.getBytes("UTF-8"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new ExitException(1);
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

    @Override
    public String put(String key, String value) {
        if (key == null || value == null ) {
            throw new
                    IllegalArgumentException("Key and/or value "
                    + "is a null-string");
        }
        /*
        The record with key has already been changed
        or added during the session.
        */
        if (sessionChanges.containsKey(key)) {
            return sessionChanges.put(key, value);
        }
        // The record with key hasn't been changed yet.
        if (allRecords.containsKey(key)) {
           sessionChanges.put(key, value);
           return allRecords.get(key);
        }
        // Absolutely new record
        return sessionChanges.put(key, value);
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        if (sessionChanges.containsKey(key)) {
            // The record with key has been deleted during the session.
            if (sessionChanges.get(key) == null) {
                return null;
            } else {
                /*
                The record with key has already been changed
                or added during the session.
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
        Set<String> keyList = new HashSet<>();
        keyList.addAll(allRecords.keySet());
        for (Map.Entry<String, String> entry: sessionChanges.entrySet()) {
            if (keyList.contains(entry.getKey())) {
                // If the record was deleted during the session.
                if (entry.getValue() == null) {
                    keyList.remove(entry.getKey());
                }
            } else {
                if (entry.getValue() != null) {
                    keyList.add(entry.getKey());
                }
            }
        }
        return keyList.size();
    }

    @Override
    public int commit() {
        int numberOfChanges = sessionChanges.size();
        // Execute changes to disk
        Map<String, String> tempStorage = new HashMap<>();
        tempStorage.putAll(allRecords);
        for (Map.Entry<String, String> entry: sessionChanges.entrySet()) {
            if (tempStorage.containsKey(entry.getKey())) {
                // If the record was deleted during the session.
                if (entry.getValue() == null) {
                    tempStorage.remove(entry.getKey());
                }
                else {
                    //If the value was changed during the session.
                    tempStorage.put(entry.getKey(),
                            entry.getValue());
                }
            } else {
                if(entry.getValue() != null) {
                    tempStorage.put(entry.getKey(), entry.getValue());
                }
            }
        }
        try {
            write(tempStorage);
            allRecords = Collections.synchronizedMap(tempStorage);
            sessionChanges.clear();
        } catch (ExitException e) {
            System.err.println("Error while commiting");
            System.exit(e.getStatus());
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
        Set<String> keyList = new HashSet<>();
        keyList.addAll(allRecords.keySet());
        for (Map.Entry<String, String> entry: sessionChanges.entrySet()) {
            if (keyList.contains(entry.getKey())) {
                // If the record was deleted during the session.
                if (entry.getValue() == null) {
                    keyList.remove(entry.getKey());
                }
            } else {
                keyList.add(entry.getKey());
            }
        }
        List<String> list = new ArrayList<>();
        list.addAll(keyList);
        return list;
    }
}
