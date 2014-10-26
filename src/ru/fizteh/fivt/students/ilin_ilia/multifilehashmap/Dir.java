package ru.fizteh.fivt.studenrts.theronsg.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class Dir {
    private File name;
    private Map<Integer, FileMap> maps;
    
    Dir(final String nam) throws IOException {
        name = new File(nam);
        maps = new TreeMap<>();
        if (name.exists()) {
            for (String file: name.list()) {
                maps.put(new Integer(file), new FileMap(Paths.get(nam).resolve(file).toString()));
            }
        } else {
            name.mkdir();
        }
    }

    public void delete() {
        if (maps.isEmpty()) {
            name.delete();
        } else {
            for (FileMap fm: maps.values()) {
                fm.delete();
            }
            name.delete();
        }
    }

    public void put(final int nfile, final String key, final String value)
            throws IOException {
        if (maps.containsKey(nfile)) {
            maps.get(key).put(key, value);
        } else {
            maps.put(nfile, new FileMap(Paths.get(name.getName()).resolve(new Integer(nfile).toString()).toString()));
            maps.get(nfile).put(key, value);
        }
    }

    public void get(final int nfile, final String key) {
        if (maps.containsKey(nfile)) {
            maps.get(nfile).get(key);
        } else {
            System.err.println("Can't find a file for this key: " + key + ".");
        }
    }
    
    public void remove(final int nfile, final String key) {
        if (maps.containsKey(nfile)) {
            maps.get(nfile).remove(key);
        } else {
            System.err.println("No key: " + key + ".");
        }
    }

    public int size() {
        return maps.size();
    }

    public String list() {
        String [] l = new String [maps.size()];
        int count = 0;
        for (int key: maps.keySet()) {
            l[count] = maps.get(key).list();
            count++;
        }
        return String.join(", ", l);
    }

    public void save() {
        for (int key: maps.keySet()) {
            maps.get(key).putFile();
            if (!maps.get(key).exists()) {
                maps.remove(key);
            }
        }
        if (maps.isEmpty()) {
            name.delete();
        }
    }
    
    public boolean exists() {
        return name.exists();
    }
}
