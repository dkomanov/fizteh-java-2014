package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

class DbReader implements AutoCloseable {

    private DataInputStream stream;

    public DbReader(String path) throws Exception {
        try {
            stream = new DataInputStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            FileOutputStream out = new FileOutputStream(path);
            stream = new DataInputStream(new FileInputStream(path));
        }
    }

    public void readData(HashMap<String, String> data) throws Exception {
        while (true) {
            try {
                String key = readNext();
                String value = readNext();
                if (data.containsKey(key)) {
                    throw new TwoSameKeysException();
                }
                data.put(key, value);
            } catch (EOFException e) {
                break;
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
        }
        byte[] word = new byte[length];
        for (int i = 0; i < length; i++) {
            word[i] = wordBuilder.get(i);
        }
        return new String(word, "UTF-8");
    }

    @Override
    public void close() throws Exception {
        try {
            stream.close();
        } catch (IOException e) {
            throw new Exception("Unable to close database file");
        }
    }
}
