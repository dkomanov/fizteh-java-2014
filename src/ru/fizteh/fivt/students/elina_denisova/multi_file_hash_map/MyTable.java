package ru.fizteh.fivt.students.elina_denisova.multi_file_hash_map;

import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTable {

    private Path mainDir;
    private Map<String, Map<String, String>> databases;
    private static Map<Integer, ArrayList<String>> changes = new HashMap<>();
    private int numberChanges = 0;

    public static final int COUNT_OBJECT = 16;
    public static final int COMMON_CONSTANT_INDEX = 100;


    public MyTable(File tableDir) {
        try {
            databases = new HashMap<>();
            mainDir = tableDir.toPath();

            for (int i = 0; i < COUNT_OBJECT; i++) {
                File subDir = new File(tableDir, i + ".dir");
                for (int j = 0; j < COUNT_OBJECT; j++) {
                    File dbFile = new File(subDir, j + ".dat");
                    if (dbFile.exists()) {
                        String adds = Integer.toString(i * COMMON_CONSTANT_INDEX + j);
                        databases.put(adds, new HashMap<String, String>());
                        readFromFile(dbFile, databases.get(adds));
                    }
                }
            }
        } catch (Exception e) {
            HandlerException.handler("MyTableProvider: ", e);
        }
    }

    public boolean containsKey(String adds) {
        return databases.containsKey(adds);
    }


    public String getName() {
        return mainDir.toString();
    };


    public String get(String key) throws IllegalArgumentException {
        if (key != null) {
            int hashCode = Math.abs(key.hashCode());
            int dir = hashCode % COUNT_OBJECT;
            int file = hashCode / COUNT_OBJECT % COUNT_OBJECT;
            String adds = Integer.toString(dir * COMMON_CONSTANT_INDEX + file);
            return databases.get(adds).get(key);
        } else {
            throw new IllegalArgumentException("MyTable.get: Haven't key. ");
        }
    }


    public String put(String key, String value) {
        if ((key != null) || (value != null)) {
            int hashCode = Math.abs(key.hashCode());
            int dir = hashCode % COUNT_OBJECT;
            int file = hashCode / COUNT_OBJECT % COUNT_OBJECT;
            String adds = Integer.toString(dir * COMMON_CONSTANT_INDEX + file);
            if (!databases.containsKey(adds)) {
                databases.put(adds, new HashMap<String, String>());
            }

            String oldValue = databases.get(adds).get(key);
            databases.get(adds).put(key, value);
            numberChanges++;
            changes.put(numberChanges, new ArrayList<String>());
            changes.get(numberChanges).add("put");
            changes.get(numberChanges).add(key);
            changes.get(numberChanges).add(value);
            if (oldValue == null) {
                changes.get(numberChanges).add("new");
            } else {
                changes.get(numberChanges).add(oldValue);
            }
            return oldValue;
        } else {
            throw new IllegalArgumentException("MyTable.put: Haven't key or value. ");
        }
    }


    public String remove(String key) {
        if (key != null) {
            int hashCode = Math.abs(key.hashCode());
            int dir = hashCode % COUNT_OBJECT;
            int file = hashCode / COUNT_OBJECT % COUNT_OBJECT;
            String adds = Integer.toString(dir * COMMON_CONSTANT_INDEX + file);
            if (!databases.containsKey(adds)) {
                return null;
            } else {
                String oldValue = databases.get(adds).get(key);
                databases.get(adds).remove(key);
                numberChanges++;
                changes.put(numberChanges, new ArrayList<String>());
                changes.get(numberChanges).add("remove");
                changes.get(numberChanges).add(key);
                changes.get(numberChanges).add(oldValue);
                return oldValue;
            }
        } else {
            throw new IllegalArgumentException("MyTable.get: Haven't key. ");
        }
    }


    public int size() {
        int answer = 0;
        for (int i = 0; i < COUNT_OBJECT; i++) {
            for (int j = 0; j < COUNT_OBJECT; j++) {
                String adds = Integer.toString(i * COMMON_CONSTANT_INDEX + j);
                if (databases.containsKey(adds)) {
                    answer += databases.get(adds).size();
                }
            }
        }
        return answer;
    }


    public int commit() {
        numberChanges = 0;
        changes = new HashMap<>();

        int count = 0;
        for (int i = 0; i < COUNT_OBJECT; i++) {
            for (int j = 0; j < COUNT_OBJECT; j++) {
                String adds = Integer.toString(i * COMMON_CONSTANT_INDEX + j);
                if (databases.containsKey(adds)) {
                    File directory = new File(mainDir.toString(), i + ".dir");
                    if (!directory.exists()) {
                        if (!directory.mkdir()) {
                            throw new UnsupportedOperationException("ParserCommands.commandsExecution.put:"
                                    + " Unable to create directories in working catalog");
                        }
                    }
                    File dataBaseOld = new File(directory, j + ".dat");
                    if (dataBaseOld.exists()) {
                        try {
                            Files.delete(dataBaseOld.toPath());
                        } catch (IOException e) {
                            throw new RuntimeException("MyTable.writeInFile: Can't overwrite file");
                        }
                    }

                    if (!dataBaseOld.exists()) {
                        try {
                            if (!dataBaseOld.createNewFile()) {
                                throw new UnsupportedOperationException("ParserCommands.commandsExecution.put:"
                                        + " Unable to create database files in working catalog");
                            }
                        } catch (IOException e) {
                            throw new UnsupportedOperationException("ParserCommands.commandsExecution.put:"
                                    + " Unable to create database files in working catalog");
                        }
                    }
                    try (RandomAccessFile dbFile = new RandomAccessFile(dataBaseOld, "rw")) {
                        for (Map.Entry<String, String> current : databases.get(adds).entrySet()) {
                            count++;
                            writeNext(dbFile, current.getKey());
                            writeNext(dbFile, current.getValue());
                        }
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException("MyTable.writeInFile: File not found");
                    } catch (IOException e) {
                        throw new RuntimeException("MyTable.writeInFile: Can't write to file.", e);
                    }

                    if (databases.get(adds).size() == 0) {
                        File subDir = new File(mainDir.toString(), i + ".dir");
                        File dbFile = new File(subDir, j + ".dat");
                        try {
                            if (dbFile.exists()) {
                                Files.delete(dbFile.toPath());
                            }
                        } catch (IOException e) {
                            throw new IllegalStateException("MyTable.remove: "
                                    + "Cannot delete database file. ", e);
                        }
                        databases.remove(adds);

                        int k = 0;
                        for (int file = 0; file < COUNT_OBJECT; file++) {
                            String variableAdds = Integer.toString(i * COMMON_CONSTANT_INDEX + file);
                            if (databases.containsKey(variableAdds)) {
                                k++;
                            }
                        }
                        if (k == COUNT_OBJECT) {
                            try {
                                Files.delete(subDir.toPath());
                            } catch (DirectoryNotEmptyException e) {
                                throw new IllegalStateException("MyTable.remove: Cannot remove table subdirectory. "
                                        + "Redundant files", e);
                            } catch (IOException e) {
                                throw new IllegalStateException("MyTable.remove: "
                                        + "Cannot delete database subdirectory", e);
                            }
                        }
                    }
                }
            }
        }
        return count;

    }

    private void writeNext(RandomAccessFile dbFile, String word) throws IOException {
        try {
            dbFile.writeInt(word.getBytes("UTF-8").length);
            dbFile.write(word.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MyTable.writeNext: Don't supported encoding UTF-8. ");
        } catch (IOException e) {
            throw new RuntimeException("MyTable.writeNext: Can't write to database. ", e);
        }
    }
    public void readFromFile(File dbFileName,  Map<String, String> data) throws IllegalArgumentException {
        try (RandomAccessFile file = new RandomAccessFile(dbFileName.toString(), "r")) {

            if (file.length() > 0) {
                while (file.getFilePointer() < file.length()) {
                    String key = readNext(file);
                    String value = readNext(file);
                    if (data.containsKey(key)) {
                        throw new IllegalArgumentException("MyTable.readFromFile: Two same keys in database file");
                    }
                    data.put(key, value);
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("MyTable.readFromFile: File not found", e);
        } catch (IOException e) {
            throw new RuntimeException(" MyTable.readFromFile: "
                    + "Problems with reading from database file " + e.toString());
        } catch (Exception e) {
            throw new RuntimeException("MyTable.readFromFile: ", e);
        }
    }


    private String readNext(RandomAccessFile dbFile) throws IOException {
        try {
            int wordLength = dbFile.readInt();
            byte[] word = new byte[wordLength];
            dbFile.read(word, 0, wordLength);
            return new String(word, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException("MyTable.readNext: UTF-8 encoding is not supported");
        } catch (IOException e) {
            throw new IOException(" MyTable.readNext: Can't read from database " + e.toString());
        }
    }


    public List<String> list() {
        List<String> listKey = new ArrayList<>();
        for (int i = 0; i < COUNT_OBJECT; i++) {
            for (int j = 0; j < COUNT_OBJECT; j++) {
                String adds = Integer.toString(i * COMMON_CONSTANT_INDEX + j);
                if (databases.containsKey(adds)) {
                    for (String key : databases.get(adds).keySet()) {
                        listKey.add(key);
                    }
                }
            }
        } return listKey;
    }
}
