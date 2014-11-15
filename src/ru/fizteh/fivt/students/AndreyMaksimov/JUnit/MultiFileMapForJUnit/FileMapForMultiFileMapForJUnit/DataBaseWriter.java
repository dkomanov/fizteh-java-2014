package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataBaseWriter implements  AutoCloseable {

    private DataOutputStream stream;

    public DataBaseWriter(String needPath) throws Exception {
        try {
            stream = new DataOutputStream(new FileOutputStream(needPath));
        } catch (FileNotFoundException e) {
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }

    public void write(HashMap<String, String> data) throws Exception {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            try {
                writetext(entry.getKey());
                writetext(entry.getValue());
            } catch (Exception e) {
                throw new Exception("Error with writing");
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
            throw new Exception("Error with close database");
        }
    }
}
