package ru.fizteh.fivt.students.MaksimovAndrey.FileMap;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

public class DataBaseWriter {

    private DataOutputStream stream;

    public DataBaseWriter(String needPath) throws Exception {
        try {
            stream = new DataOutputStream(new FileOutputStream(needPath));
        } catch (FileNotFoundException e) {
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }

    public void write(HashMap<String,String> data) throws Exception {
        for (Map.Entry<String,String> entry: data.entrySet()) {
            try {
                writetext(entry.getKey());
                writetext(entry.getValue());
            }
            catch (Exception e) {
                throw new Exception("ERROR: Error with writing");
            }
        }
    }

    private void writetext(String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        stream.writeInt(byteWord.length);
        stream.write(byteWord);
        stream.flush();
    }

    public void close() throws Exception {
        try {
            stream.close();
        } catch (IOException e) {
            throw new Exception("ERROR: Error with close database");
        }
    }
}

