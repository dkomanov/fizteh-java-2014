package ru.fizteh.fivt.students.torunova.filemap;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by nastya on 04.10.14.
 */
public class FileMap implements Database {
    Map<String, String> map = new HashMap<>();
    String file;

    public FileMap(final String f) {
        DataInputStream fis = null;
        file = f;
		File f1 = new File(file);
		try {
			if (!f1.getAbsoluteFile().exists()) {
				f1.createNewFile();
			}
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
			System.exit(1);
		}
        try {
			fis = new DataInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.println("Caught FileNotFoundException: " + e.getMessage());
			System.exit(1);
		}
       	int  length = 0;
        try {
            while (fis.available() > 0) {
				length = fis.readInt();
                byte[] key = new byte[length];
                if (fis.read(key) != length) {
                    System.err.println("Incorrect file.");
                    System.exit(1);
                }
                length = fis.readInt();
                byte[] value = new byte[length];
                if (fis.read(value) != length) {
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
        DataOutputStream fos = null;
        try {
            fos = new DataOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            System.err.println("Caught FileNotFoundException: " + e.getMessage());
            System.exit(1);
        }
        Set<String> keys = map.keySet();
        for (String key : keys) {
            try {
             	fos.writeInt(key.length());
                fos.write(key.getBytes("UTF-8"));
              	fos.writeInt(map.get(key).length());
                fos.write(map.get(key).getBytes("UTF-8"));
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
                System.exit(1);
            }
        }
    }
}
