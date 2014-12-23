package ru.fizteh.fivt.students.vladislav_korzun.JUnit;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;

public class MyTable implements Table{
    
    private String name;
    private Path path;
    private Map<String, String> fileMap;
    private Map<String, String> bufferMap;
    private int changes;

    public MyTable(Path currentTablePath, String currentTable) {
        this.path = currentTablePath;
        this.name = currentTable;
        DataBaseSerializer serializer = new DataBaseSerializer();
        serializer.readTable(this.path);
        this.fileMap = new HashMap<>(serializer.getMap());
        this.bufferMap = new HashMap<>(this.fileMap);
        changes = 0;
    }

    @Override
    public String getName() { 
            return name; 
    }

    @Override
    public String get(String key) {
        String val = fileMap.get(key);
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        return val;
    }

    @Override
    public String put(String key, String value) {
        String val = fileMap.put(key, value);
        changes++;
        return val;
    }

    @Override
    public String remove(String key) {
        String val = fileMap.remove(key);
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        changes++;
        return val;
    }

    @Override
    public int size() {
        return this.fileMap.size();
    }

    @Override
    public int commit() {
        DataBaseSerializer serializer = new DataBaseSerializer();
        serializer.setMap(this.fileMap);
        serializer.writeTable(path);
        this.bufferMap = new HashMap<>(this.fileMap);
        changes = 0;
        return 0;
    }

    @Override
    public int rollback() {        
        this.fileMap = new HashMap<>(this.bufferMap);
        return this.unsavedChanges();
    }

    @Override
    public List<String> list() {
        Set<String> keys = fileMap.keySet();
        List<String> keylist = new ArrayList<String>();
        for (String key : keys) {
            keylist.add(key);
        }
        return keylist;
    }

    public int unsavedChanges() {
        return this.changes;
    }
}
