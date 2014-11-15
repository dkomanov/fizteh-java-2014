package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class DataBaseReader implements AutoCloseable {

    private DataInputStream stream;

    public DataBaseReader(String path) throws Exception {
        try {
            stream = new DataInputStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            throw new Exception("Unfortunately database file not found");
        }
    }

    public void read(HashMap<String, String> needdatabase) throws Exception {
        boolean end = false;
        while (!end) {
            try {
                String key = readText();
                String value = readText();
                if (needdatabase.containsKey(key)) {
                    throw new Exception("Unfortunately two same keys in need database file");
                }
                needdatabase.put(key, value);
            } catch (IOException e) {
                end = true;
            }
        }
    }

    private String readText() throws IOException {
        int length = stream.readInt();
        byte[] wordArray = new byte[length];
        stream.readFully(wordArray);
        return new String(wordArray, "UTF-8");
    }

    public void close() throws Exception {
        try {
            stream.close();
        } catch (IOException e) {
            throw new Exception("Error in close");
        }
    }
}