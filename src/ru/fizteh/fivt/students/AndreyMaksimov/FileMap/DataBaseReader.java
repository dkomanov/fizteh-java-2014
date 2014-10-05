package ru.fizteh.fivt.students.MaksimovAndrey.FileMap;

import java.io. *;
import java.util.HashMap;

public class DataBaseReader {

    private DataInputStream stream;

    public DataBaseReader(String path) throws Exception {
        try {
            stream = new DataInputStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }

    public void read(HashMap<String, String> needdatabase) throws Exception {
        boolean end = false;
        while (!end) {
            try {
                String key = readtext();
                String value = readtext();
                if (needdatabase.containsKey(key)) {
                    throw new Exception("ERROR: Two same keys");
                }
                needdatabase.put(key, value);
            } catch (IOException e) {
                end = true;
            }
        }
    }

    private String readtext() throws IOException {
        int length = stream.readInt();
        byte[] wordArray = new byte[length];
        stream.readFully(wordArray);
        return new String(wordArray, "UTF-8");
    }

    public void close() throws Exception {
        try {
            stream.close();
        } catch (IOException e) {
            throw new Exception("ERROR: Error in close");
        }
    }
}
