package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base;

import java.io.File;
import java.util.Set;

public class SimpleTableBuilder implements TableBuilder {
    StringTable table;

    public SimpleTableBuilder(StringTable table) {
        this.table = table;
    }

    @Override
    public String get(String key) {
        return table.rawGet(key);
    }

    @Override
    public void put(String key, String value) {
        table.rawPut(key, value);
    }

    @Override
    public Set<String> getKeys() {
        return table.oldData.keySet();
    }

    @Override
    public File getTableDirectory() {
        return new File(table.getDirectory(), table.getName());
    }

    @Override
    public void setCurrentFile(File currentFile) {
    }
}
