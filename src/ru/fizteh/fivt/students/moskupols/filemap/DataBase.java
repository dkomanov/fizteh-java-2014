package ru.fizteh.fivt.students.moskupols.filemap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moskupols on 26.09.14.
 */
class DataBase {
    public Map<String, String> map;

    private static String nextWord(DataInputStream dataStream) throws IOException {
        int length = dataStream.readInt();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append(dataStream.readChar());
        }
        return builder.toString();
    }

    public void load(String path) throws Exception {
        map = new HashMap<>();
        try (DataInputStream dataStream = new DataInputStream(new FileInputStream(path))) {
            boolean eof = false;
            while (!eof) {
                try {
                    String key = nextWord(dataStream);
                    String value = nextWord(dataStream);
                    if (map.put(key, value) != null) {
                        throw new Exception("The db is invalid: the keys are not unique");
                    }
                } catch (EOFException e) {
                    eof = true;
                }
            }
        }
    }

    private static void putWord(DataOutputStream dataStream, String word) throws IOException {
        dataStream.writeInt(word.length());
        dataStream.writeChars(word);
    }

    public void dump(String path) throws IOException {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(path))) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                putWord(outputStream, entry.getKey());
                putWord(outputStream, entry.getValue());
            }
        }
    }
}
