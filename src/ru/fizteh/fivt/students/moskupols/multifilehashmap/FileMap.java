package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The FileMap class stores a String-String map in file.
 */
class FileMap {
    private Map<String, String> map;
    final Path targetPath;
    private final Predicate<String> keyChecker;

    public FileMap(Path targetPath) throws Exception {
        this(targetPath, null);
    }

    public FileMap(Path targetPath, Predicate<String> keyChecker) throws Exception {
        this.targetPath = targetPath;
        this.keyChecker = keyChecker;
        if (Files.exists(targetPath)) {
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

    private void load(Path path) throws Exception {
        map = new HashMap<>();
        try (DataInputStream dataStream = new DataInputStream(new FileInputStream(path.toFile()))) {
            boolean eof = false;
            while (!eof) {
                try {
                    String key = nextWord(dataStream);
                    if (keyChecker != null && !keyChecker.test(key)) {
                        throw new Exception(
                                "The db is invalid: some keys are stored not in appropriate place");
                    }
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

    public void flush() throws IOException {
        dump(targetPath);
    }

    public void dump(Path path) throws IOException {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(path.toFile()))) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                putWord(outputStream, entry.getKey());
                putWord(outputStream, entry.getValue());
            }
        }
    }

    // Some methods of Table interface

    public String get(String key) {
        if (null == key) {
            throw new IllegalArgumentException();
        }
        return map.get(key);
    }

    public String put(String key, String value) {
        if (null == key || null == value) {
            throw new IllegalArgumentException();
        }
        return map.put(key, value);
    }

    public String remove(String key) {
        if (null == key) {
            throw new IllegalArgumentException();
        }
        return map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public List<String> list() {
        return new ArrayList<>(map.keySet());
    }
}
