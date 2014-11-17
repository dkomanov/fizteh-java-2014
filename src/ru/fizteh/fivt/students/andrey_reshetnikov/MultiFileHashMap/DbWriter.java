package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

class DbWriter {

    private DataOutputStream stream;

    public DbWriter(String path) throws Exception {
        try {
            stream = new DataOutputStream(new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            throw new Exception("Database file not found");
        }
    }

    public void writeData(HashMap<String, String> data) throws Exception {
        for (Map.Entry<String, String> entry: data.entrySet()) {
            try {
                writeNext(entry.getKey());
                writeNext(entry.getValue());
            } catch (IOException e) {
                throw new Exception("Problems with writing");
            }
        }
        stream.close();
    }

    private void writeNext(String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        stream.writeInt(byteWord.length);
        stream.write(byteWord);
    }
}
