package ru.fizteh.fivt.students.theronsg.multifilehashmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Dir {
    private File name;
    private Map<Integer, FileMap> maps;
    
    Dir(final String nam) throws IOException {
        name = new File(nam);
        maps = new HashMap<>();
        if (name.exists()) {
            for (String file: name.list()) {
                String f = file.substring(0, file.length() - 4);
                maps.put(Integer.parseInt(f), new FileMap(Paths.get(name.getAbsolutePath()).resolve(f).toString()));
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
    /**
     *  @param n means nfile.
     *  I've changed it for checkstyle.
     */

    public void put(final int n, final String key, final String value)
            throws IOException {
        if (maps.containsKey(n)) {
            maps.get(n).put(key, value);
        } else {
            maps.put(n, new FileMap(Paths.get(name.getAbsolutePath()).resolve(new Integer(n).toString()).toString()));
            maps.get(n).put(key, value);
        }
    }

    public void get(final int nfile, final String key) {
        if (maps.containsKey(nfile)) {
            maps.get(nfile).get(key);
        } else {
            System.err.println("not found");
        }
    }
    
    public void remove(final int nfile, final String key) {
        if (maps.containsKey(nfile)) {
            maps.get(nfile).remove(key);
        } else {
            System.err.println("not found");
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

    public void save() throws FileNotFoundException {
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
