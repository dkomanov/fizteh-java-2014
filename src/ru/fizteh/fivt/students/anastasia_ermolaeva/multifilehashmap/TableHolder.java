package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap;

import ru.fizteh.fivt.students.anastasia_ermolaeva.
        multifilehashmap.util.ExitException;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TableHolder implements Map<String, Table>, AutoCloseable {
    private Path rootPath;
    private Map<String, Table> tableMap;

    public TableHolder(final Path rootPath) throws ExitException {
        this.rootPath = rootPath;
        this.tableMap = new HashMap<>();
        File rootDirectory = rootPath.toFile();
        String[] childDirectories = rootDirectory.list();
        for(String s: childDirectories) {
            File currentDir = new File(rootPath.toAbsolutePath().toString()
                    + File.separator + s);
            if (!currentDir.isDirectory()) {
                System.err.println("Child directory is not a dir");
            } else {
                String tableName = currentDir.getName();
                tableMap.put(tableName, new Table(rootPath, tableName));
            }
        }
    }
    public Map<String, Table> getTableMap() {
        return tableMap;
    }
    @Override
    public void close() throws Exception {
        for (Map.Entry<String, Table> entry : tableMap.entrySet()){
            entry.getValue().close();
        }
        tableMap.clear();
    }

    @Override
    public int size() {
        return tableMap.size();
    }

    @Override
    public boolean isEmpty() {
        return tableMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return tableMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return tableMap.containsValue(value);
    }

    @Override
    public Table get(Object key) {
        return tableMap.get(key);
    }

    @Override
    public Table put(String key, Table value) {
        return tableMap.put(key, value);
    }

    @Override
    public Table remove(Object key) {
        return tableMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Table> m) {
        tableMap.putAll(m);
    }

    @Override
    public void clear() {
        tableMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return tableMap.keySet();
    }

    @Override
    public Collection<Table> values() {
        return tableMap.values();
    }

    @Override
    public Set<Entry<String, Table>> entrySet() {
        return tableMap.entrySet();
    }
}
