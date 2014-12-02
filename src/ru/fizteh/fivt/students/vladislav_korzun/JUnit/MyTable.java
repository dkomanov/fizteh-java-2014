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
    private Map<String, String> filemap;
    private Map<String, String> buffermap;

    public MyTable(Path currentTablePath, String currentTable) {
        filemap = new HashMap<>();
        this.path = currentTablePath;
        this.name = currentTable;
        FileManager filemanager = new FileManager();
        filemanager.readTable(this.path);
        this.filemap = filemanager.filemap;
        this.buffermap = this.filemap;
    }

    @Override
    public String getName() {
        if (this.name != null) {
            return name;
        }
        return null;
    }

    @Override
    public String get(String key) {
        String val = filemap.get(key);
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (val.equals(null)) {
            return null;
        } else {
            return val;
        }
    }

    @Override
    public String put(String key, String value) {
        String val = filemap.put(key, value);
        if (val == null) {
            return null;
        } else {
            return val;
        }
    }

    @Override
    public String remove(String key) {
        String val = filemap.remove(key);
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (val.equals(null)) {
            return null;
        } else {
            return val;
        }
        
    }

    @Override
    public int size() {
        return this.filemap.size();
    }

    @Override
    public int commit() {
        FileManager filemanager = new FileManager();
        filemanager.filemap = this.filemap;
        filemanager.writeTable(path);
        return 0;
    }

    @Override
    public int rollback() {
        this.filemap = this.buffermap;
        return 0;
    }

    @Override
    public List<String> list() {
        Set<String> keys = filemap.keySet();
        List<String> keylist = new ArrayList<String>();
        for (String key : keys) {
            keylist.add(key);
        }
        return keylist;
    }

    int unsavedChanges() {
        return this.filemap.size() - this.buffermap.size();
    }
}
