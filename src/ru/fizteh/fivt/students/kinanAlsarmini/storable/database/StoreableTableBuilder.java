package ru.fizteh.fivt.students.kinanAlsarmini.storable.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.TableBuilder;
import ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.MultifileMapUtils;

import java.io.File;
import java.text.ParseException;
import java.util.Set;

public class StoreableTableBuilder implements TableBuilder {
    DatabaseTableProvider provider;
    DatabaseTable table;

    private int currentBucket;
    private int currentFile;

    public StoreableTableBuilder(DatabaseTableProvider provider, DatabaseTable table) {
        this.provider = provider;
        this.table = table;
    }

    @Override
    public String get(String key) {
        Storeable value = table.get(key);

        try {
            String representation = provider.serialize(table, value);
            return representation;
        } catch (ColumnFormatException e) {
            return null;
        }
    }

    @Override
    public void put(String key, String value) {
        MultifileMapUtils.checkKeyPlacement(key, currentBucket, currentFile);

        Storeable objectValue = null;
        try {
            objectValue = provider.deserialize(table, value);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        table.put(key, objectValue);
    }

    @Override
    public Set<String> getKeys() {
        return table.rawGetKeys();
    }

    @Override
    public File getTableDirectory() {
        return new File(table.getDirectory(), table.getName());
    }

    @Override
    public void setCurrentFile(File curFile) {
        currentBucket = MultifileMapUtils.parseCurrentBucketNumber(curFile.getParentFile());
        currentFile = MultifileMapUtils.parseCurrentFileNumber(curFile);
    }
}
