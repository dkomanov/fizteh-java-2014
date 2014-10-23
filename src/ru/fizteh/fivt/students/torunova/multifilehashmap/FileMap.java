package ru.fizteh.fivt.students.torunova.multifilehashmap;

import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectFileException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by nastya on 19.10.14.
 */
public class FileMap {
    Map<String, String> map = new HashMap<>();
    String file;

    public FileMap(final String f) throws IncorrectFileException, IOException {
        DataInputStream fis = null;
        file = f;
        fis = new DataInputStream(new FileInputStream(file));
        int  length = 0;
        while (fis.available() > 0) {
            length = fis.readInt();
            byte[] key = new byte[length];
            if (fis.read(key) != length) {
                throw new IncorrectFileException(
                        "Real key length does not match specified in file length.");
            }
            length = fis.readInt();
            byte[] value = new byte[length];
            if (fis.read(value) != length) {
                throw new IncorrectFileException(
                        "Real value length does not match specified in file length.");
            }
            String k = new String(key, "UTF-8");
            String v = new String(value, "UTF-8");
            map.put(k, v);
        }
    }

    public boolean put(String key, String value) {
        if (map.containsKey(key)) {
            map.remove(key);
            map.put(key, value);
            return false;
        }
        map.put(key, value);
        return true;
    }

    public String get(String key) {
        return map.get(key);
    }

    public boolean remove(String key) {
        if (map.containsKey(key)) {
            map.remove(key);
            return true;
        }
        return false;
    }

    public Set<String> list() {
        return map.keySet();
    }
    public boolean isEmpty() {
        return map.isEmpty();
    }
    public int size() {
        return map.size();
    }

    public void close() throws  IOException {
        DataOutputStream fos = null;
        fos = new DataOutputStream(new FileOutputStream(file));
        Set<String> keys = map.keySet();
        for (String key : keys) {
            fos.writeInt(key.length());
            fos.write(key.getBytes("UTF-8"));
            fos.writeInt(map.get(key).length());
            fos.write(map.get(key).getBytes("UTF-8"));
        }

    }
}
