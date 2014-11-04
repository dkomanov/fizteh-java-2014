package ru.fizteh.fivt.students.torunova.junit;

import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nastya on 19.10.14.
 */
public class FileMap {
    Map<String, String> map = new HashMap<>();
    Map<String, String> diff = new HashMap<>();
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
                        "Error in file " + file + " : real key length does not match specified in file length.");
            }
            length = fis.readInt();
            byte[] value = new byte[length];
            if (fis.read(value) != length) {
                throw new IncorrectFileException(
                        "Error in file " + file + " : real value length does not match specified in file length.");
            }
            map.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
            diff.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
        }
    }

    public String put(String key, String value) {
       return diff.put(key, value);
    }

    public String get(String key) {
        return diff.get(key);
    }

    public String remove(String key) {
          return  diff.remove(key);
    }

    public Set<String> list() {
        return diff.keySet();
    }

    public boolean isEmpty() {
        return diff.isEmpty();
    }

    public int size() {
        return diff.size();
    }

    public int rollback() {
        int numberOfRevertedChanges = countChangedEntries();
        diff = new HashMap<>();
        diff.putAll(map);
        return numberOfRevertedChanges;
    }
    public int commit() throws  IOException {
        DataOutputStream fos = new DataOutputStream(new FileOutputStream(file));
        Set<String> keys = diff.keySet();

        for (String key : keys) {
            fos.writeInt(key.getBytes("UTF-8").length);
            fos.write(key.getBytes("UTF-8"));
            fos.writeInt(diff.get(key).getBytes("UTF-8").length);
            fos.write(diff.get(key).getBytes("UTF-8"));
        }
        int numberOfChangedEntries = countChangedEntries();
        map = new HashMap<>();
        map.putAll(diff);
        return  numberOfChangedEntries;
    }
    public int countChangedEntries() {
        int numberOfChangedEntries = 0;
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(diff.keySet());
        allKeys.addAll(map.keySet());
        for (String key: allKeys) {
            if (map.get(key) == null || diff.get(key) == null) {
                numberOfChangedEntries++;
                continue;
            }
            if (!map.get(key).equals(diff.get(key))) {
                numberOfChangedEntries++;
            }
        }
        return numberOfChangedEntries;
    }
}
