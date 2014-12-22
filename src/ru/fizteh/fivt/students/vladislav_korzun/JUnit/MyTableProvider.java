package ru.fizteh.fivt.students.vladislav_korzun.JUnit;

import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class MyTableProvider implements TableProvider {
    
    private Set<String> tables;
    private Path rootDir;
    
    public MyTableProvider(String dir) throws DataBaseException {
        rootDir = Paths.get(dir);
        if (!rootDir.toFile().exists()) {
            rootDir.toFile().mkdir();
        } else {
            if (!rootDir.toFile().isDirectory()) {
                throw new IllegalArgumentException("Path of root directory is file"); 
            } else {
                tables = new HashSet<>();
                String[] tableDirs = rootDir.toFile().list();
                for (String currentTable: tableDirs) {
                    Path currentTablePath = rootDir.resolve(currentTable);
                    if (currentTablePath.toFile().isDirectory()) {
                        new MyTable(currentTablePath, currentTable);
                        tables.add(currentTable);
                    }
                }
            }
        }
        
    }

    @Override
    public Table getTable(String name) throws DataBaseException {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        if (this.tables.contains(name)) {
            return new MyTable(this.rootDir.resolve(name), name);
        } else {
            throw new IllegalArgumentException("No such table");
        }
    }

    @Override
    public Table createTable(String name) throws DataBaseException {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        if (!this.tables.contains(name)) {
            return null;
        } else {
            Path newTablePath = rootDir.resolve(name);
            newTablePath.toFile().mkdir();
            Table newTable = new MyTable(newTablePath, name);
            this.tables.add(name);
            return newTable;
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        Path tableDir = this.rootDir.resolve(name);
        if (!this.tables.remove(name)) {
            throw new IllegalStateException("No table with name"  + name);
        } else {
            removeData(tableDir);
        }
        
    }
    
    public Set<String> list() {
        return this.tables;
    }

    private void removeData(Path tableDir) {
        File table = new File(tableDir.toString());
        File[] dirs = table.listFiles();
        for (File dir : dirs) {
            File[] fls = dir.listFiles();
            for (File fl : fls) {
                fl.delete();
            }
            dir.delete();
        }
    }
}
