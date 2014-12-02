package ru.fizteh.fivt.students.moskupols.filemap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moskupols on 26.09.14.
 */
class DataBaseTable {
    public Map<String, String> map;
    String targetPath;

    DataBaseTable() {
        targetPath = null;
        map = null;
    }

    DataBaseTable(String targetPath) throws Exception {
        this.targetPath = null;
        this.map = null;
        setTargetPath(targetPath);
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) throws Exception {
        this.targetPath = targetPath;
        if (map != null) {
            dump();
        }
        if (Files.exists(Paths.get(targetPath))) {
            reload();
        } else {
            map = new HashMap<>();
        }
    }

    public void reload() throws Exception {
        load(targetPath);
    }

    private static String nextWord(DataInputStream dataStream) throws IOException {
        byte[] intBytes = new byte[4];
        intBytes[0] = dataStream.readByte(); // throws EOF

        try {
            dataStream.readFully(intBytes, 1, 3);
        } catch (EOFException e) {
            throw new IOException("Database is corrupted");
        }
        int length = ByteBuffer.wrap(intBytes).getInt();
        if (length < 0) {
            throw new IOException("Database is corrupted");
        }
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        for (int i = 0; i < length; i++) {
            try {
                byteStream.write(dataStream.readByte());
            } catch (EOFException e) {
                throw new IOException("Database is corrupted");
            }
        }
        return new String(byteStream.toByteArray(), "UTF-8");
    }

    private void load(String path) throws Exception {
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
        byte[] bytes = word.getBytes("UTF-8");
        dataStream.writeInt(bytes.length);
        dataStream.write(bytes);
    }

    public void dump() throws IOException {
        dump(targetPath);
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
