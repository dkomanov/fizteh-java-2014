package ru.fizteh.fivt.students.elina_denisova.j_unit;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyTable implements Table{

    private Path mainDir;
    private Map<String, Map<String, String>> databases;
    private static Map<Integer, ArrayList<String>> changes = new HashMap<>();
    private int numberChanges = 0;

    public MyTable (File tableDir) {
        try {
            databases = new HashMap<>();
            mainDir = tableDir.toPath();

            for (int i = 0; i < 16; i++) {
                File subDir = new File(tableDir, i + ".dir");
                for (int j = 0; j < 16; j++) {
                    File dbFile = new File(subDir, j + ".dat");
                    if (dbFile.exists()) {
                        String adds = Integer.toString(i*100 + j);
                        databases.put(adds, new HashMap<String, String>());
                        readFromFile(dbFile, databases.get(adds));
                    }
                }
            }
        } /*catch (FileNotFoundException e) {
            HandlerException.handler("TableProvider: Path-string cannot be converted to a Path", e);
        } catch (IOException e) {
            HandlerException.handler("TableProvider: Problems with reading from database file", e);
        }*/ catch (Exception e) {
            HandlerException.handler("TableProvider: Unknown error", e);
        }
    }

    public boolean containsKey(String adds) {
        return databases.containsKey(adds);
    }

    @Override
    public String getName(){
        return mainDir.toString();
    };

    @Override
    public String get(String key) throws IllegalArgumentException {
        if (key != null) {
            int hashCode = Math.abs(key.hashCode());
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;
            String adds = Integer.toString(dir*100 + file);
            return databases.get(adds).get(key);
        }
        else {
            throw new IllegalArgumentException("Table.get: Haven't key. ");
        }
    }

    @Override
    public String put(String key, String value) {
        if ((key != null) || (value != null)) {
            try {
                int hashCode = Math.abs(key.hashCode());
                int dir = hashCode % 16;
                int file = hashCode / 16 % 16;
                String adds = Integer.toString(dir * 100 + file);
                if (!databases.containsKey(adds)) {
                    File subDir = new File(mainDir.toString(), dir + ".dir");
                    if (!subDir.exists()) {
                        if (!subDir.mkdir()) {
                            throw new UnsupportedOperationException("ParserCommands.commandsExecution.put:"
                                    + " Unable to create directories in working catalog");
                        }
                    }
                    File dbFile = new File(subDir, file + ".dat");
                    if (!dbFile.exists()) {
                        if (!dbFile.createNewFile()) {
                            throw new UnsupportedOperationException("ParserCommands.commandsExecution.put:"
                                    + " Unable to create database files in working catalog");
                        }
                    }
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
                }
                else {
                    changes.get(numberChanges).add(oldValue);
                }
                return oldValue;


            } catch (IOException e) {
                throw new NullPointerException("MyTable.put: Cannot create new file. ");
            }
        }
        else {
            throw new IllegalArgumentException("MyTable.put: Haven't key or value. ");
        }
    }

    @Override
    public String remove(String key) {
        if (key != null) {
            int hashCode = Math.abs(key.hashCode());
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;
            String adds = Integer.toString(dir*100 + file);
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
        }
        else {
            throw new IllegalArgumentException("Table.get: Haven't key. ");
        }
    }

    @Override
    public int size() {
        int answer = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                String adds = Integer.toString(i*100 + j);
                if (databases.containsKey(adds)) {
                    answer += databases.get(adds).size();
                }
            }
        }
        return answer;
    }

    @Override
    public int commit() {
        numberChanges = 0;
        changes = new HashMap<>();

        int count = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                String adds = Integer.toString(i*100 + j);
                if (databases.containsKey(adds)) {
                    File directory = new File(mainDir.toString(), i + ".dir");
                    File dataBaseOld = new File(directory, j + ".dat");
                    try {
                        Files.delete(dataBaseOld.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException("MyTable.writeInFile: Can't overwrite file");
                    }
                    File dataBase = new File(directory, j + ".dat");
                    try (RandomAccessFile dbFile = new RandomAccessFile(dataBase, "rw")) {
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
                        for (int file = 0; file < 16; file++) {
                            String variable_adds = Integer.toString(i * 100 + file);
                            if (databases.containsKey(variable_adds)) {
                                k++;
                            }
                        }
                        if (k == 16) {
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
            throw new RuntimeException("Table.writeNext: Don't supported encoding UTF-8. ");
        } catch (IOException e) {
            throw new RuntimeException("Table.writeNext: Can't write to database. ", e);
        }
    }
    public void readFromFile(File dbFileName,  Map<String, String> data) throws IllegalArgumentException {
        try (RandomAccessFile file = new RandomAccessFile(dbFileName.toString(), "r")) {

            if (file.length() > 0) {
                while (file.getFilePointer() < file.length()) {
                    String key = readNext(file);
                    String value = readNext(file);
                    if (data.containsKey(key)) {
                        throw new IllegalArgumentException("Table.readFromFile: Two same keys in database file");
                    }
                    data.put(key, value);
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Table.readFromFile: File not found", e);
        } catch (IOException e) {
            throw new RuntimeException(" Table.readFromFile: Problems with reading from database file " + e.toString());
        } catch (Exception e) {
            throw new RuntimeException("Table.readFromFile: Unknown error", e);
        }
    }


    private String readNext(RandomAccessFile dbFile) throws IOException {
        try {
            int wordLength = dbFile.readInt();
            byte[] word = new byte[wordLength];
            dbFile.read(word, 0, wordLength);
            return new String(word, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException("Table.readNext: UTF-8 encoding is not supported");
        } catch (IOException e) {
            throw new IOException(" Table.readNext: Can't read from database " + e.toString());
        }
    }


    @Override
    public int rollback() {
        int count = 0;
        Map<Integer, ArrayList<String>> saveChanges = changes;
        for(int i = saveChanges.size(); i > 0; i--) {
            String[] com = new String[saveChanges.get(i).size()];
            com = saveChanges.get(i).toArray(com);
            String message;
            if (com[0].equals("put")) {
                if (com[3].equals("new")) {
                    message = remove(com[1]);
                } else {
                    message = put(com[1], com[3]);
                }
            }
            if (com[0].equals("remove")) {
                put(com[1], com[2]);
            }
            count++;
        }
        numberChanges = 0;
        changes = new HashMap<>();
        return count;
    }

    @Override
    public List<String> list() {
        List<String> listKey = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                String adds = Integer.toString(i*100 + j);
                if (databases.containsKey(adds)) {
                    for (String key : databases.get(adds).keySet())
                    {
                        listKey.add(key);
                    }
                }
            }
        }
        return listKey;
    }
}