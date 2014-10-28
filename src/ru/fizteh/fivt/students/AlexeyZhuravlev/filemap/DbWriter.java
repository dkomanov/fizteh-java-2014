package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AlexeyZhuravlev
 */
class DbWriter implements AutoCloseable{

    private DataOutputStream stream;

    public DbWriter(String path) throws Exception {
        try {
            stream = new DataOutputStream(new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            throw new Exception("Unable to write to database file: file not found");
        } catch (SecurityException e) {
            throw new Exception("Security violation: unable to write to database file");
        }
    }

    public void writeData(HashMap<String, String> data) throws Exception {
        for (Map.Entry<String, String> entry: data.entrySet()) {
            try {
                writeNext(entry.getKey());
                writeNext(entry.getValue());
            } catch (UnsupportedEncodingException e) {
                throw new Exception("Error: UTF-8 encoding is not supported");
            } catch (IOException e) {
                throw new Exception("Problems with writing to database file");
            }
        }
    }

    private void writeNext(String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        stream.writeInt(byteWord.length);
        stream.write(byteWord);
    }

    public void close() throws Exception {
        try {
            stream.close();
        } catch (IOException e) {
            throw new Exception("Unable to close database file");
        }
    }

}
