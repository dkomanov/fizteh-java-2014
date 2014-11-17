package ru.fizteh.fivt.students.theronsg.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Table {
    private File tableName;
    private Map<Integer, Dir> data;
    public static int MAX_DIRS_AND_FILES_AMOUNT = 16;
    
    Table(final String name) throws IOException {
        tableName  = new File(name);
        data = new HashMap<>();
        if (tableName.exists()) {
            if (tableName.list() != null) {
                for (String file: tableName.list()) {
                    data.put(Integer.parseInt(file),
                            new Dir(Paths.get(tableName.getAbsolutePath()).resolve(file).toString()));
                }
            }
        } else {
            try {
                tableName.mkdir();
            } catch (SecurityException e) {
                System.err.println("Can't create the following directory: \"" + tableName.getName() + "\"");
            }
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
        int ndir = byt % MAX_DIRS_AND_FILES_AMOUNT;
        int nfile = (byt / MAX_DIRS_AND_FILES_AMOUNT) % MAX_DIRS_AND_FILES_AMOUNT;
        if (data.containsKey(ndir)) {
            data.get(ndir).put(nfile, key, value);
        } else {
            data.put(ndir, new Dir(Paths.get(tableName.getAbsolutePath())
                    .resolve(new Integer(ndir).toString()).toString()));
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
        int ndirectory = byt % MAX_DIRS_AND_FILES_AMOUNT;
        int nfile = (byt / MAX_DIRS_AND_FILES_AMOUNT) % MAX_DIRS_AND_FILES_AMOUNT;
        if (data.containsKey(ndirectory)) {
            data.get(ndirectory).get(nfile, key);
        } else {
            System.err.println("not found");
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
        int ndirectory = byt % MAX_DIRS_AND_FILES_AMOUNT;
        int nfile = (byt / MAX_DIRS_AND_FILES_AMOUNT) % MAX_DIRS_AND_FILES_AMOUNT;
        if (data.containsKey(ndirectory)) {
            data.get(ndirectory).remove(nfile, key);
        } else {
            System.err.println("not found");
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

    public void saveTable() throws IOException {
        for (Dir d: data.values()) {
            d.save();
        }
    }
}
