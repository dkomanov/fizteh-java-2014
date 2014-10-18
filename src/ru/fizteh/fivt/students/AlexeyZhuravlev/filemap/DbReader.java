package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
class DbReader {

    private DataInputStream stream;

    public DbReader(String path) throws Exception {
        try {
            File dbFile = new File(path);
            if (!dbFile.exists()) {
                if (!dbFile.createNewFile()) {
                    throw new Exception("Cannot create new database file");
                }
            }
            stream = new DataInputStream(new FileInputStream(path));
        } catch (IOException | SecurityException e) {
            throw new Exception("Cannot create new database file");
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

    private String readNext() throws Exception {
        int length = stream.readInt();
        if (length < 0) {
            throw new Exception("Incorrect data base file: negative length of word");
        }
        ArrayList<Byte> wordBuilder = new ArrayList<>();
        try {
            for (int i = 0; i < length; i++) {
                byte symbol = stream.readByte();
                wordBuilder.add(symbol);
            }
        } catch (EOFException e) {
            throw new Exception("Incorrect data base file: unexpected end of file");
        } catch (OutOfMemoryError e) {
            throw new Exception("Not enough memory to store database");
        }
        byte[] word = new byte[length];
        for (int i = 0; i < length; i++) {
            word[i] = wordBuilder.get(i);
        }
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
