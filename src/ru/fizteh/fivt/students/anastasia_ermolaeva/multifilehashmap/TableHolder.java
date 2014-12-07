package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap;

import ru.fizteh.fivt.students.anastasia_ermolaeva.
        multifilehashmap.util.ExitException;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TableHolder implements AutoCloseable {
    private Path rootPath;
    private Map<String, Table> tableMap;

    public Path getRootPath() {
        return rootPath;
    }
    public TableHolder(final Path rootPath) {
        this.rootPath = rootPath;
        this.tableMap = new HashMap<>();
        File rootDirectory = rootPath.toFile();
        String[] childDirectories = rootDirectory.list();
        for (String s: childDirectories) {
            File currentDir = new File(rootPath.toAbsolutePath().toString()
                    + File.separator + s);
            if (!currentDir.isDirectory()) {
                System.err.println("Child directories are not directories");
                System.exit(1);
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
    public void close() throws ExitException {
        for (Map.Entry<String, Table> entry : tableMap.entrySet()) {
            entry.getValue().close();
        }
        tableMap.clear();
    }
}
