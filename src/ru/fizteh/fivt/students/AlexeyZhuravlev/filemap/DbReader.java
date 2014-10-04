package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.io.*;
import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
class DbReader {

    private DataInputStream stream;

    public DbReader(String path) throws Exception {
        try {
            stream = new DataInputStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            throw new Exception("Database file not found");
        }
    }

    public void readData(HashMap<String, String> data) throws Exception {
        boolean eof = false;
        while (!eof) {
            try {
                String key = readNext();
                String value = readNext();
                if (data.containsKey(key)) {
                    throw new Exception("Error: two same keys in database file");
                }
                data.put(key, value);
            } catch (EOFException e) {
                eof = true;
            } catch (UnsupportedEncodingException e) {
                throw new Exception("Error: UTF-8 encoding is not supported");
            } catch (IOException e) {
                throw new Exception("Problems with reading from database file");
            }
        }
    }

    private String readNext() throws IOException {
        int length = stream.readInt();
        byte[] word = new byte[length];
        stream.readFully(word);
        return new String(word, "UTF-8");
    }

    public void close() throws Exception {
        try {
            stream.close();
        } catch (IOException e) {
            throw new Exception("Unable to close database file");
        }
    }
}
