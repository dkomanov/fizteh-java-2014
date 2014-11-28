package ru.fizteh.fivt.students.elina_denisova.multi_file_hash_map;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Table {

    public Path dbFileName;
    private Map<String, String> data;

    public Table(String path) throws FileNotFoundException {
        try {
            dbFileName = Paths.get(path);
            data = new HashMap<String, String>();
            File dbFile = new File(path);
            if (!dbFile.exists() && !dbFile.createNewFile()) {
                throw new FileNotFoundException("Table: Cannot create new database file");
            }
            if (dbFile.exists()) {
                readFromFile();
            }
        } catch (FileNotFoundException e) {
            HandlerException.handler(e);
        } catch (Exception e) {
            HandlerException.handler("Table: Unknown error", e);
        }
    }

    public int recordsNumber() {
        return data.size();
    }


    public void readFromFile() throws IllegalArgumentException {
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
            HandlerException.handler(e);
        } catch (IllegalArgumentException e) {
            HandlerException.handler(e);
        } catch (FileNotFoundException e) {
            HandlerException.handler("Table.readFromFile: File not found", e);
        } catch (IOException e) {
            HandlerException.handler("Table.readFromFile: Problems with reading from database file", e);
        } catch (Exception e) {
            HandlerException.handler("Table.readFromFile: Unknown error", e);
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
            throw new IOException("Table.readNext: Can't read from database", e);
        }
    }

    public void writeInFile() {
        try (RandomAccessFile dbFile = new RandomAccessFile(dbFileName.toString(), "rw")) {

            for (Map.Entry<String, String> current : data.entrySet()) {
                writeNext(dbFile, current.getKey());
                writeNext(dbFile, current.getValue());
            }
        } catch (FileNotFoundException e) {
            HandlerException.handler("Table.writeInFile: File not found", e);
        } catch (Exception e) {
            HandlerException.handler("Table.writeInFile: Unknown error", e);
        }
    }

    private void writeNext(RandomAccessFile dbFile, String word) {
        try {
            dbFile.writeInt(word.getBytes("UTF-8").length);
            dbFile.write(word.getBytes("UTF-8"));
        } catch (IOException e) {
            HandlerException.handler("Table.writeNext: Can't read from database", e);
        } catch (Exception e) {
            HandlerException.handler("Table.writeNext: Unknown error", e);
        }
    }

    public void dbList() {
        System.out.print(String.join(", ", data.keySet()));
        System.out.println("");
    }

    public void dbPut(String key,  String value) {
        if (data.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(data.get(key));
            data.remove(key);
            data.put(key, value);
        } else {
            System.out.println("new");
            data.put(key, value);
        }
    }

    public void dbGet(String key) {
        if (data.containsKey(key)) {
            System.out.println("found");
            System.out.println(data.get(key));
        } else {
            System.out.println("not found");
        }
    }
    public void dbRemove(String key) {
        if (data.containsKey(key)) {
            System.out.println("removed");
            data.remove(key);
        } else {
            System.out.println("not found");
        }
    }


}
