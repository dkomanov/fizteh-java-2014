package ru.fizteh.fivt.students.kinanAlsarmini.storable.database;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.AbstractStorage;
import ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.DistributedLoader;
import ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.DistributedSaver;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.StoreableUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

public class DatabaseTable extends AbstractStorage<String, Storeable> implements Table {
    DatabaseTableProvider provider;

    private List<Class<?>> columnTypes;

    public DatabaseTable(DatabaseTableProvider provider, String databaseDirectory, String tableName,
            List<Class<?>> columnTypes) {
        super(databaseDirectory, tableName);

        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("column types cannot be null");
        }

        this.columnTypes = columnTypes;
        this.provider = provider;

        try {
            checkTableDirectory();
            load();
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid file format");
        }
    }

    @Override
    public Storeable get(String key) {
        return storageGet(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key != null) {
            if (StoreableUtils.checkStringCorrect(key)) {
                throw new IllegalArgumentException("key cannot be empty");
            }
        }

        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        if (!checkAlienStoreable(value)) {
            throw new ColumnFormatException("alien storeable");
        }

        checkCorrectStoreable(value);

        return storagePut(key, value);
    }

    @Override
    public Storeable remove(String key) {
        return storageRemove(key);
    }

    @Override
    public int size() {
        return storageSize();
    }

    @Override
    public int commit() throws IOException {
        return storageCommit();
    }

    @Override
    public int rollback() {
        return storageRollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return getUncommittedChangesCount();
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public List<String> list() {
        return storageListKeys();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex > getColumnsCount()) {
            throw new IndexOutOfBoundsException();
        }

        return columnTypes.get(columnIndex);
    }

    @Override
    protected void load() throws IOException {
        if (provider == null) {
            return;
        }

        DistributedLoader.load(new StoreableTableBuilder(provider, this));
    }

    @Override
    protected void save() throws IOException {
        DistributedSaver.save(new StoreableTableBuilder(provider, this));
    }

    private void checkTableDirectory() throws IOException {
        File tableDirectory = new File(getDirectory(), getName());

        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
            writeSignatureFile();
        } else {
            File[] children = tableDirectory.listFiles();

            if (children == null || children.length == 0) {
                throw new IllegalArgumentException(String.format("table directory: %s is empty",
                            tableDirectory.getAbsolutePath()));
            }
        }
    }

    private void writeSignatureFile() throws IOException {
        File tableDirectory = new File(getDirectory(), getName());
        File signatureFile = new File(tableDirectory, DatabaseTableProvider.SIGNATURE_FILE);

        signatureFile.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(signatureFile));
        List<String> formattedColumnTypes = StoreableUtils.formatColumnTypes(columnTypes);
        String signature = StoreableUtils.join(formattedColumnTypes);

        writer.write(signature);
        writer.close();
    }

    public boolean checkAlienStoreable(Storeable storeable) {
        for (int index = 0; index < getColumnsCount(); ++index) {
            try {
                Object o = storeable.getColumnAt(index);

                if (o == null) {
                    continue;
                }

                if (!o.getClass().equals(getColumnType(index))) {
                    return false;
                }
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        }
        try {
            storeable.getColumnAt(getColumnsCount());
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
        return false;
    }

    public void checkCorrectStoreable(Storeable storeable) {
        for (int index = 0; index < getColumnsCount(); ++index) {
            try {
                StoreableUtils.checkValue(storeable.getColumnAt(index), columnTypes.get(index));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    Set<String> rawGetKeys() {
        return oldData.keySet();
    }
}
