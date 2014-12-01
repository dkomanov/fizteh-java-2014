package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.TableSystem;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap.FileMapUsage;

import java.io.File;
import java.text.ParseException;
import java.util.Set;

public class DatabaseTableCreator implements DatabaseTableCreatorInterface {
    ThreadSafeDatabaseTableProvider provider;
    ThreadSafeDatabaseTable table;

    private int currentBucket;
    private int currentFile;

    public DatabaseTableCreator(ThreadSafeDatabaseTableProvider provider, ThreadSafeDatabaseTable table) {
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
        FileMapUsage.checkKeyPlacement(key, currentBucket, currentFile);

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
        return table.getKeysSet();
    }

    @Override
    public File getTableDirectory() {
        return new File(table.getDirectory(), table.getName());
    }

    @Override
    public void setCurrentFile(File curFile) {
        currentBucket = FileMapUsage.parseCurrentBucketNumber(curFile.getParentFile());
        currentFile = FileMapUsage.parseCurrentFileNumber(curFile);
    }
}
