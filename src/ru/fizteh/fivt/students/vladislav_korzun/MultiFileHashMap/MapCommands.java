package ru.fizteh.fivt.students.vladislav_korzun.MultiFileHashMap;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class MapCommands {

    public Map<String, String> filemap;
    
    MapCommands(FileManager db) {
        filemap = db.filemap;
    }
    
    void put(String key, String value) {
        try {
            String val = filemap.put(key, value);
            if (val == null) {
                System.out.println("new");
            } else {
                System.out.println("owerwrite");
                System.out.println(val);
            }            
        } catch (NullPointerException e) {
            System.err.println("Key or value is null");
        }
    }
    
    void get(String key) {
        try {
            String val = filemap.get(key);
            if (val.equals(null)) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(val);
            }            
        } catch (NullPointerException e) {
            System.err.println("Key or value is null");
        } 
    }
    
    void remove(String key) {
        try {
            String val = filemap.remove(key);
            if (val.equals(null)) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }            
        } catch (NullPointerException e) {
            System.err.println("Key or value is null");
        } 
    }
    
    void list() {
        Set<String> keys = filemap.keySet();
        String joined = String.join(", ", keys);
        System.out.println(joined);
    }
    
    void exit(FileManager db, Path dbfile) {
        db.filemap = filemap;
        db.writeTable(dbfile);
    }
}


