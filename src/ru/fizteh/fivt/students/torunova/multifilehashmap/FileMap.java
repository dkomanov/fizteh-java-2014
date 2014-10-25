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
        file = f;
        DataInputStream fis = new DataInputStream(new FileInputStream(file));
        int  length;
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
            map.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
        }
    }

    public String put(String key, String value) {
       return map.put(key, value);
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
        DataOutputStream fos = new DataOutputStream(new FileOutputStream(file));
        Set<String> keys = map.keySet();
        for (String key : keys) {
            fos.writeInt(key.getBytes("UTF-8").length);
            fos.write(key.getBytes("UTF-8"));
            fos.writeInt(map.get(key).getBytes("UTF-8").length);
            fos.write(map.get(key).getBytes("UTF-8"));
        }

    }
}
