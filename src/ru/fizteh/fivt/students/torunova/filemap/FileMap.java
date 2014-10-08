package ru.fizteh.fivt.students.torunova.filemap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by nastya on 04.10.14.
 */
public class FileMap implements Database {
    HashMap<String, String> map = new HashMap<>();
    String file;

    public FileMap(final String f) {
        FileInputStream fis = null;
        file = f;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.err.println("Caught FileNotFoundException: " + e.getMessage());
            System.exit(1);
        }
        byte[] length = new byte[4];
        try {
            while (fis.read(length) != -1) {
                int lk = (length[0] << 24) + (length[1] << 16) + (length[2] << 8) + length[3];
                byte[] key = new byte[lk];
                if (fis.read(key) != lk) {
                    //TODO:There will be exception;
                    System.err.println("Incorrect file.");
                    System.exit(1);
                }
                if (fis.read(length) != 4) {
                    //TODO:There will be exception;
                    System.err.println("Incorrect file.");
                    System.exit(1);
                }
                int lv = (length[0] << 24) + (length[1] << 16) + (length[2] << 8) + length[3];
                byte[] value = new byte[lv];
                if (fis.read(value) != lv) {
                    //TODO:There will be exception;
                    System.err.println("Incorrect file.");
                    System.exit(1);
                }
                String k = new String(key, "UTF-8");
                String v = new String(value, "UTF-8");
                map.put(k, v);
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public boolean put(String key, String value) {
        if (map.containsKey(key)) {
            map.remove(key);
            map.put(key, value);
            return false;
        }
        map.put(key, value);
        return true;
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public boolean remove(String key) {
        if (map.containsKey(key)) {
            map.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public Set<String> list() {
        return map.keySet();
    }

    @Override
    public void close() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("Caught FileNotFoundException: " + e.getMessage());
            System.exit(1);
        }
        Set<String> keys = map.keySet();
        for (String key : keys) {
            try {
                ByteBuffer bb1 = ByteBuffer.allocate(4);
                byte[] lk = bb1.putInt(key.getBytes("UTF-8").length).array();
                fos.write(lk);
                fos.write(key.getBytes("UTF-8"));
                ByteBuffer bb2 = ByteBuffer.allocate(4);
                byte[] lv = bb2.putInt(map.get(key).getBytes("UTF-8").length).array();
                fos.write(lv);
                fos.write(map.get(key).getBytes("UTF-8"));
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
                System.exit(1);
            }
        }
    }
}
