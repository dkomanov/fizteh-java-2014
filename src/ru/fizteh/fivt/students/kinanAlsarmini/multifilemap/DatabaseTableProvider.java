package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap;

import ru.fizteh.fivt.storage.strings.*;

import javax.swing.plaf.multi.MultiInternalFrameUI;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.AbstractMap;

public class DatabaseTableProvider implements TableProvider {
    private static final String CHECK_EXPRESSION = "[^0-9A-Za-zА-Яа-я]";

    HashMap<String, MultifileTable> tables = new HashMap<String, MultifileTable>();
    private String databaseDirectoryPath;
    private MultifileTable activeTable = null;

    public DatabaseTableProvider(String databaseDirectoryPath) {
        this.databaseDirectoryPath = databaseDirectoryPath;
        File databaseDirectory = new File(databaseDirectoryPath);
        for (final File tableFile : databaseDirectory.listFiles()) {
            if (tableFile.isFile()) {
                continue;
            }
            MultifileTable table = new MultifileTable(databaseDirectoryPath, tableFile.getName());
            tables.put(table.getName(), table);
        }
    }

    public Table getTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("table's name cannot be null");
        }

        checkTableName(name);

        MultifileTable table = tables.get(name);

        if (table == null) {
            return table;
        }

        if (activeTable != null && activeTable.getUncommittedChangesCount() > 0) {
            throw new IllegalStateException(String.format("%d unsaved changes", activeTable.getUncommittedChangesCount()));
        }

        activeTable = table;

        return table;
    }

    public Table createTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("table's name cannot be null");
        }

        checkTableName(name);

        if (tables.containsKey(name)) {
            return null;
        }

        MultifileTable table = new MultifileTable(databaseDirectoryPath, name);
        tables.put(name, table);

        return table;
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("table's name cannot be null");
        }

        if (!tables.containsKey(name)) {
            throw new IllegalStateException(String.format("%s not exists", name));
        }

        tables.remove(name);

        File tableFile = new File(databaseDirectoryPath, name);
        MultifileMapUtils.deleteFile(tableFile);
    }

    public AbstractMap<String, Integer> getTables() {
        HashMap<String, Integer> shownTables = new HashMap<String, Integer>();

        for (Map.Entry<String, MultifileTable> table : tables.entrySet()) {
            shownTables.put(table.getKey(), table.getValue().size());
        }

        return shownTables;
    }

    private void checkTableName(String name) {
        Pattern pattern = Pattern.compile(CHECK_EXPRESSION);
        Matcher matcher = pattern.matcher(name);

        if (matcher.find()) {
            throw new IllegalArgumentException("bad symbol in table's name");
        }
    }
}
