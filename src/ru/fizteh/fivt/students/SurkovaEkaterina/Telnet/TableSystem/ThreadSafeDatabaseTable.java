package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap.*;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.StoreableUsage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ThreadSafeDatabaseTable extends ThreadSafeAbstractTable implements Table {
    private static final int MAX_DIRECTORIES_NUMBER = 16;
    private static final int MAX_FILES_NUMBER = 16;

    ThreadSafeDatabaseTableProvider provider;

    private List<Class<?>> columnTypes = null;

    public ThreadSafeDatabaseTable(ThreadSafeDatabaseTableProvider provider,
                                   String databaseDirectory,
                                   String tableName,
                                   List<Class<?>> columnTypes) {
        super(databaseDirectory, tableName);
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Column types cannot be empty list!");
        }
        this.columnTypes = columnTypes;
        this.provider = provider;

        try {
            checkTableDirectory();
            load();
        } catch (IOException e) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Invalid file format!");
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key != null) {
            if (key.matches("\\s*") || key.split("\\s+").length != 1) {
                throw new IllegalArgumentException(this.getClass().toString() + ": Key cannot be empty!");
            }
        }

        if (value == null) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Value cannot be null!");
        }

        if (!checkCorrectStorable(value)) {
            throw new ColumnFormatException(this.getClass().toString() + ": Incorrect storable!");
        }
        return super.put(key, value);
    }

    @Override
    public Storeable get(String key) {
        return super.get(key);
    }

    @Override
    public Storeable remove(String key) {
        return super.remove(key);
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public int commit() {
        return super.commit();
    }

    @Override
    public int rollback() {
        return super.rollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return super.getNumberOfUncommittedChanges();
    }

    @Override
    public int getColumnsCount() {
        return (columnTypes == null) ? 0 : columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        state.checkOperationCorrect();
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
        DatabaseTableCreator creator = new DatabaseTableCreator(provider, this);
        File tableDirectory = creator.getTableDirectory();
        if (tableDirectory.listFiles() == null) {
            return;
        }

        for (final File bucket : tableDirectory.listFiles()) {
            if (bucket.isFile()) {
                continue;
            }

            if (bucket.listFiles().length == 0) {
                throw new IllegalArgumentException(this.getClass().toString() + ": Empty bucket!");
            }


            for (final File file : bucket.listFiles()) {
                creator.setCurrentFile(file);
                FileMapReader.loadFromFile(file.getAbsolutePath(), creator);
            }
        }
    }

    @Override
    protected void save() throws IOException {
        DatabaseTableCreator creator = new DatabaseTableCreator(provider, this);

        File tableDirectory = creator.getTableDirectory();
        ArrayList<Set<String>> keysToSave = new ArrayList<Set<String>>();
        boolean isBucketEmpty;

        for (int bucketNumber = 0; bucketNumber < MAX_DIRECTORIES_NUMBER; ++bucketNumber) {
            keysToSave.clear();
            for (int index = 0; index < MAX_FILES_NUMBER; ++index) {
                keysToSave.add(new HashSet<String>());
            }
            isBucketEmpty = true;

            for (final String key : creator.getKeys()) {
                if (FileMapUsage.getDirNumber(key) == bucketNumber) {
                    int fileNumber = FileMapUsage.getFileNumber(key);
                    keysToSave.get(fileNumber).add(key);
                    isBucketEmpty = false;
                }
            }

            String bucketName = String.format("%d.dir", bucketNumber);
            File bucketDirectory = new File(tableDirectory, bucketName);

            if (isBucketEmpty) {
                FileMapUsage.deleteFile(bucketDirectory);
                continue;
            }

            for (int fileNumber = 0; fileNumber < MAX_FILES_NUMBER; ++fileNumber) {
                String fileName = String.format("%d.dat", fileNumber);
                File file = new File(bucketDirectory, fileName);
                if (keysToSave.get(fileNumber).isEmpty()) {
                    FileMapUsage.deleteFile(file);
                    continue;
                }
                if (!bucketDirectory.exists()) {
                    bucketDirectory.mkdir();
                }
                FileMapWriter.saveToFile(file.getAbsolutePath(), keysToSave.get(fileNumber), creator);
            }
        }
    }

    @Override
    public String getDirectory() {
        return directory;
    }

    private boolean checkCorrectStorable(Storeable storeable) {
        for (int index = 0; index < getColumnsCount(); ++index) {
            try {
                Object obj = storeable.getColumnAt(index);
                if (obj == null) {
                    continue;
                }
                if (!obj.getClass().equals(getColumnType(index))) {
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

    private void writeSignatureFile() throws IOException {
        File tableDirectory = new File(getDirectory(), getName());
        File signatureFile = new File(tableDirectory, ThreadSafeDatabaseTableProvider.SIGNATURE_FILE);
        signatureFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(signatureFile));
        List<String> formattedColumnTypes = StoreableUsage.formatColumnTypes(columnTypes);
        String signature = StoreableUsage.concatenateListEntries(formattedColumnTypes);
        writer.write(signature);
        writer.close();
    }

    private void checkTableDirectory() throws IOException {
        File tableDirectory = new File(getDirectory(), getName());
        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
            writeSignatureFile();
        } else {
            File[] children = tableDirectory.listFiles();
            if (children == null || children.length == 0) {
                throw new IllegalArgumentException(String.format(this.getClass().toString()
                                + ": Table directory %s is empty!",
                        tableDirectory.getAbsolutePath()));
            }
        }
    }

    public Set<String> getKeysSet() {
        return oldData.keySet();
    }

    @Override
    protected DatabaseFileDescriptor makeDescriptor(String key) {
        return FileMapUsage.makeDescriptor(key);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), getDirectory() + File.separator + getName());
    }
}
