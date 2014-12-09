package ru.fizteh.fivt.students.SukhanovZhenya.FileMap;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class FileMap {
    static Map<String, String> fMap;
    static RandomAccessFile randomAccessFile;

    FileMap(String path) {
        if (path == null) {
            System.err.println("There is no file path!");
            incorrectFile();
        }
        fMap = new HashMap<>();
        try {
            randomAccessFile = new RandomAccessFile(path, "rw");
        } catch (FileNotFoundException | SecurityException e) {
            System.err.println(e.getMessage());
            incorrectFile();
        }
    }

    void add(String key, String value) {
        String old = fMap.put(key, value);
        if (old != null) {
            System.out.print("overwrite\nold ");
            System.out.println(old);
        } else {
            System.out.println("new");
        }
    }

    void get(String key) {
        String res = fMap.get(key);
        if (res == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(res);
        }
    }

    void remove(String key) {
        String value = fMap.get(key);
        if (value == null) {
            if (fMap.containsKey(key)) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.print("remove ");
            System.out.println(key);
            fMap.put(key, null);
        }
    }

    void list() {
        System.out.println(String.join(", ", fMap.keySet()));
    }

    void getFile() {
        try {
            int sizeLength;
            long readied = 1;
            try {
                while (true) {
                    sizeLength = randomAccessFile.readInt();
                    readied += 4;

                    byte[] key = new byte[sizeLength];

                    try {
                        randomAccessFile.readFully(key);
                        readied += sizeLength;
                        sizeLength = randomAccessFile.readInt();
                        readied += 4;
                        byte[] value = new byte[sizeLength];
                        randomAccessFile.readFully(value);
                        readied += sizeLength;
                        fMap.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
                    } catch (EOFException e) {
                        System.err.println(e.getMessage());
                        incorrectFile();
                    }
                }
            } catch (EOFException e) {
                if (readied < randomAccessFile.length()) {
                    System.err.println(randomAccessFile.length());
                    System.err.println(readied);
                    incorrectFile();
                }
            }
        } catch (IOException | OutOfMemoryError ioe) {
            System.err.println(ioe.getMessage());
            incorrectFile();
        }
    }

    void appendFile() {
        try {
            randomAccessFile.setLength(0);
            for (Map.Entry<String, String> pair : fMap.entrySet()) {
                if (pair.getValue() != null) {
                    randomAccessFile.writeInt(pair.getKey().getBytes().length);
                    randomAccessFile.write(pair.getKey().getBytes("UTF-8"));
                    randomAccessFile.writeInt(pair.getValue().getBytes("UTF-8").length);
                    randomAccessFile.write(pair.getValue().getBytes("UTF-8"));
                }
            }

        } catch (IOException | OutOfMemoryError ioe) {
            System.err.println(ioe.getMessage());
            incorrectFile();
        }
    }

    private static void incorrectFile() {
        System.err.println("Incorrect file");
        System.exit(1);
    }

    void exit() {
        System.exit(0);
    }
}
