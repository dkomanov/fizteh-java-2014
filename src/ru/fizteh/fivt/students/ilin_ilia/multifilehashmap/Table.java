package ru.fizteh.fivt.studenrts.theronsg.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class Table {
    private File tableName;
    private Map<Integer, Dir> data;
    
    Table(final String name) throws IOException {
        tableName  = new File(name);
        data = new TreeMap<>();
        if (tableName.exists()) {
            if (tableName.list() != null) {
                for (String file: tableName.list()) {
                    System.out.println(file);
                    data.put(Integer.parseInt(file), new Dir(Paths.get(name).resolve(file).toString()));
                }
            }
        } else {
            tableName.mkdir();
        }
    }
    
    public void drop() {
        if (data.isEmpty()) {
            tableName.delete();
        } else {
            for (Dir s: data.values()) {
                s.delete();
            }
            tableName.delete();
        }
    }

    public void put(final String key, final String value) 
            throws IOException {
        byte byt = 0;
        try {
            byt = key.getBytes("UTF-8")[0];
        } catch (UnsupportedEncodingException e) {
            System.out.println("Can't uncode key to UTF-8");
            System.exit(-1);
        }
        int ndir = byt % 16;
        int nfile = (byt / 16) % 16;
        if (data.containsKey(ndir)) {
            data.get(key).put(nfile, key, value);
        } else {
            data.put(ndir, new Dir(Paths.get(tableName.getName()).resolve(new Integer(ndir).toString()).toString()));
            data.get(ndir).put(nfile, key, value);
        }
    }

    public void get(final String key) {
        byte byt = 0;
        try {
            byt = key.getBytes("UTF-8")[0];
        } catch (UnsupportedEncodingException e) {
            System.out.println("Can't uncode key to UTF-8");
            System.exit(-1);
        }
        int ndirectory = byt % 16;
        int nfile = (byt / 16) % 16;
        if (data.containsKey(ndirectory)) {
            data.get(key).get(nfile, key);
        } else {
            System.err.println("No key: " + key + ".");
        }
    }

    public int size() {
        int summ = data.size();
        for (int i: data.keySet()) {
            summ += data.get(i).size();
        }
        return summ;
    }
    
    public void remove(final String key) {
        byte byt = 0;
        try {
            byt = key.getBytes("UTF-8")[0];
        } catch (UnsupportedEncodingException e) {
            System.out.println("Can't uncode key to UTF-8");
            System.exit(-1);
        }
        int ndirectory = byt % 16;
        int nfile = (byt / 16) % 16;
        if (data.containsKey(ndirectory)) {
            data.get(key).remove(nfile, key);
        } else {
            System.err.println("No key: " + key + ".");
        }
    }

    public void list() {
        String[] l = new String[data.size()];
        int count = 0;
        for (int key: data.keySet()) {
            l[count] = data.get(key).list();
            count++;
        }
        System.out.println(String.join(", ", l));
    }

    public void saveTable() {
        for (Dir d: data.values()) {
            d.save();
        }
    }
}
