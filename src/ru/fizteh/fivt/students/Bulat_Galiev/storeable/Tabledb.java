package ru.fizteh.fivt.students.Bulat_Galiev.storeable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public final class Tabledb implements Table {
    private static final int KEYNUMBER = 16;
    private static final int INTNUMBER = 4;
    private String tableName;
    private Path tablePath;
    private Map<CellForKey, DatabaseSerializer> databaseFiles;
    private TabledbProvider localProvider;
    private List<Class<?>> columnTypeList;

    public Tabledb(final Path path, final String name,
            final TabledbProvider singleprovider,
            final List<Class<?>> columnListType) {
        databaseFiles = new HashMap<CellForKey, DatabaseSerializer>();
        tablePath = path;
        tableName = name;
        localProvider = singleprovider;
        columnTypeList = columnListType;
        try {
            readTableDir();
        } catch (IOException e) {
            System.err.print("Error reading table " + tableName + ": "
                    + e.getMessage());
            System.exit(-1);
        }
    }

    public String getName() {
        return tableName;
    }

    public TabledbProvider getLocalProvider() {
        return localProvider;
    }

    public int commit() {
        try {
            int diffnrecords = writeTableToDir();
            return diffnrecords;
        } catch (IOException e) {
            throw new RuntimeException("Error writing table " + tableName, e);
        }
    }

    public int rollback() {
        Iterator<Entry<CellForKey, DatabaseSerializer>> it = databaseFiles
                .entrySet().iterator();
        int diffnrecords = 0;
        while (it.hasNext()) {
            Entry<CellForKey, DatabaseSerializer> databaseFile = it.next();
            diffnrecords += databaseFile.getValue().rollback();
            if (databaseFile.getValue().getRecordsNumber() == 0) {
                it.remove();
            }
        }
        System.out.println(diffnrecords);
        return diffnrecords;
    }

    public Storeable get(final String oldkey) {
        if (oldkey != null) {
            String key = oldkey.trim();
            if (!key.isEmpty()) {
                DatabaseSerializer databaseFile;
                try {
                    databaseFile = databaseFiles.get(new CellForKey(key));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                if (databaseFile == null) {
                    return null;
                } else {
                    return databaseFile.get(key);
                }
            } else {
                throw new IllegalArgumentException("Empty key.");
            }
        } else {
            throw new IllegalArgumentException("Null key.");
        }
    }

    public Storeable put(final String oldkey, final Storeable value) {
        try {
            if ((oldkey != null) && (value != null)) {
                String key = oldkey.trim();
                if (!key.isEmpty()) {
                    DatabaseSerializer databaseFile = databaseFiles
                            .get(new CellForKey(key));
                    if (databaseFile == null) {

                        int nbytes = key.getBytes("UTF-8")[0];
                        int ndirectory = Math.abs(nbytes % KEYNUMBER);
                        int nfile = Math.abs((nbytes / KEYNUMBER) % KEYNUMBER);
                        databaseFile = new DatabaseSerializer(tablePath,
                                ndirectory, nfile, this);
                        databaseFiles.put(new CellForKey(ndirectory, nfile),
                                databaseFile);
                    }
                    if (!checkValue(this, value)) {
                        throw new ColumnFormatException("invalid value");
                    }
                    return databaseFile.put(key, value);
                } else {
                    throw new IllegalArgumentException("Empty key or name.");
                }
            } else {
                throw new IllegalArgumentException("Null key or name.");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public Storeable remove(final String oldkey) {
        if (oldkey != null) {
            String key = oldkey.trim();
            if (!key.isEmpty()) {
                DatabaseSerializer databaseFile;
                try {
                    databaseFile = databaseFiles.get(new CellForKey(key));
                    if (databaseFile == null) {
                        return null;
                    } else {
                        return databaseFile.remove(key);
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            } else {
                throw new IllegalArgumentException("Empty key.");
            }
        } else {
            throw new IllegalArgumentException("Null key.");
        }
    }

    public int size() {
        int numrecords = 0;
        for (Entry<CellForKey, DatabaseSerializer> databaseFile : databaseFiles
                .entrySet()) {
            numrecords += databaseFile.getValue().getRecordsNumber();
        }
        return numrecords;
    }

    public List<String> list() {
        List<String> list = new LinkedList<String>();
        for (Entry<CellForKey, DatabaseSerializer> pair : databaseFiles
                .entrySet()) {
            list.addAll(pair.getValue().list());
        }
        return list;
    }

    public void deleteTable() throws IOException {
        String[] dirList = tablePath.toFile().list();
        for (String curDir : dirList) {
            String[] fileList = tablePath.resolve(curDir).toFile().list();
            if (fileList != null) {
                for (String file : fileList) {
                    Paths.get(tablePath.toString(), curDir, file).toFile()
                            .delete();
                }
            }
            tablePath.resolve(curDir).toFile().delete();
        }
        tablePath.toFile().delete();
        databaseFiles.clear();
    }

    private void readTableDir() throws IOException {
        String[] dirList = tablePath.toFile().list();
        for (String dir : dirList) {
            if (!dir.equals("signature.tsv")) {
                Path curDir = tablePath.resolve(dir);
                if (!curDir.toFile().isDirectory()
                        || !dir.matches("([0-9]|1[0-5])\\.dir")) {
                    throw new IOException(
                            "it contains file or inappropriate directory "
                                    + dir.toString());
                }
                String[] fileList = curDir.toFile().list();
                if (fileList.length == 0) {
                    throw new IOException("it contains empty directory "
                            + dir.toString());
                }
                for (String file : fileList) {
                    Path filePath = curDir.resolve(file);
                    if (!filePath.toFile().isFile()
                            || !file.matches("([0-9]|1[0-5])\\.dat")) {
                        throw new IOException(dir.toString()
                                + "contains directory or inappropriate file"
                                + file.toString());
                    }
                    int ndirectory = Integer.parseInt(dir.substring(0,
                            dir.length() - INTNUMBER));
                    int nfile = Integer.parseInt(file.substring(0,
                            file.length() - INTNUMBER));
                    DatabaseSerializer databaseFile;
                    try {
                        databaseFile = new DatabaseSerializer(tablePath,
                                ndirectory, nfile, this);
                        databaseFiles.put(new CellForKey(ndirectory, nfile),
                                databaseFile);
                    } catch (Exception e) {
                        System.err.print(e.getMessage());
                        System.exit(-1);
                    }
                }
            }
        }
    }

    private int writeTableToDir() throws IOException {
        Iterator<Entry<CellForKey, DatabaseSerializer>> it = databaseFiles
                .entrySet().iterator();
        int diffnrecords = 0;
        while (it.hasNext()) {
            Entry<CellForKey, DatabaseSerializer> databaseFile = it.next();
            diffnrecords += databaseFile.getValue().commit();
            if (databaseFile.getValue().getRecordsNumber() == 0) {
                it.remove();
            }
        }
        return diffnrecords;
    }

    @Override
    public int getColumnsCount() {
        return columnTypeList.size();
    }

    @Override
    public Class<?> getColumnType(final int columnIndex)
            throws IndexOutOfBoundsException {
        return columnTypeList.get(columnIndex);
    }

    public boolean checkValue(final Table table, final Storeable value) {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            try {
                value.getColumnAt(i);
            } catch (IndexOutOfBoundsException ex) {
                return false;
            }
        }

        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (!((value.getColumnAt(i) == null)
                    || (value.getColumnAt(i).getClass() == Integer.class && table
                            .getColumnType(i) == Integer.class)
                    || ((value.getColumnAt(i).getClass() == Long.class || value
                            .getColumnAt(i).getClass() == Integer.class) && table
                            .getColumnType(i) == Long.class)
                    || (value.getColumnAt(i).getClass() == Byte.class && table
                            .getColumnType(i) == Byte.class)
                    || (value.getColumnAt(i).getClass() == Integer.class && table
                            .getColumnType(i) == Byte.class)
                    || (value.getColumnAt(i).getClass() == Float.class && table
                            .getColumnType(i) == Float.class)
                    || (value.getColumnAt(i).getClass() == Float.class && table
                            .getColumnType(i) == Double.class)
                    || (value.getColumnAt(i).getClass() == Double.class && table
                            .getColumnType(i) == Double.class)
                    || (value.getColumnAt(i).getClass() == Boolean.class && table
                            .getColumnType(i) == Boolean.class) || (value
                    .getColumnAt(i).getClass() == String.class && table
                    .getColumnType(i) == String.class))) {
                return false;
            }
        }

        try {
            value.getColumnAt(table.getColumnsCount());
        } catch (IndexOutOfBoundsException ex) {
            return true;
        }

        return false;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        int diffNumRecords = 0;
        for (Entry<CellForKey, DatabaseSerializer> databaseFile : databaseFiles
                .entrySet()) {
            diffNumRecords += databaseFile.getValue().getChangedRecordsNumber();
        }
        return diffNumRecords;
    }
}
