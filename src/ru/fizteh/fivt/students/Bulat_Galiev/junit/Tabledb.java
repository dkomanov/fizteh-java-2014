package ru.fizteh.fivt.students.Bulat_Galiev.junit;

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

import ru.fizteh.fivt.storage.strings.Table;

public final class Tabledb implements Table {
    private static final int KEYNUMBER = 16;
    private static final int INTNUMBER = 4;
    private String tableName;
    private Path tablePath;
    private Map<Key, DatabaseSerializer> databaseFiles;

    public Tabledb(final Path path, final String name) {
        databaseFiles = new HashMap<Key, DatabaseSerializer>();
        tablePath = path;
        tableName = name;
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

    public int getdiffnrecords() throws IOException {
        Iterator<Entry<Key, DatabaseSerializer>> it = databaseFiles.entrySet()
                .iterator();
        int diffnrecords = 0;
        while (it.hasNext()) {
            Entry<Key, DatabaseSerializer> databaseFile = it.next();
            diffnrecords += databaseFile.getValue().getchangednrecords();
        }
        return diffnrecords;
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
        Iterator<Entry<Key, DatabaseSerializer>> it = databaseFiles.entrySet()
                .iterator();
        int diffnrecords = 0;
        while (it.hasNext()) {
            Entry<Key, DatabaseSerializer> databaseFile = it.next();
            diffnrecords += databaseFile.getValue().rollback();
            if (databaseFile.getValue().getnrecords() == 0) {
                it.remove();
            }
        }
        System.out.println(diffnrecords);
        return diffnrecords;
    }

    public String get(final String oldkey) {
        if (oldkey != null) {
            String key = oldkey.trim();
            if (!key.isEmpty()) {
                DatabaseSerializer databaseFile;
                try {
                    databaseFile = databaseFiles.get(new Key(key));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return (databaseFile == null) ? null : databaseFile.get(key);
            } else {
                throw new IllegalArgumentException("Empty key.");
            }
        } else {
            throw new IllegalArgumentException("Null key.");
        }
    }

    public String put(final String oldkey, final String oldvalue) {
        try {
            if ((oldkey != null) && (oldvalue != null)) {
                String key = oldkey.trim();
                String value = oldvalue.trim();
                if ((!key.isEmpty()) && (!value.isEmpty())) {
                    DatabaseSerializer databaseFile = databaseFiles
                            .get(new Key(key));
                    if (databaseFile == null) {

                        int nbytes = key.getBytes("UTF-8")[0];
                        int ndirectory = Math.abs(nbytes % KEYNUMBER);
                        int nfile = Math.abs((nbytes / KEYNUMBER) % KEYNUMBER);
                        databaseFile = new DatabaseSerializer(tablePath,
                                ndirectory, nfile);
                        databaseFiles.put(new Key(ndirectory, nfile),
                                databaseFile);
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

    public String remove(final String oldkey) {
        if (oldkey != null) {
            String key = oldkey.trim();
            if (!key.isEmpty()) {
                DatabaseSerializer databaseFile;
                try {
                    databaseFile = databaseFiles.get(new Key(key));
                    return (databaseFile == null) ? null : databaseFile
                            .remove(key);
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
        for (Entry<Key, DatabaseSerializer> databaseFile : databaseFiles
                .entrySet()) {
            numrecords += databaseFile.getValue().getnrecords();
        }
        return numrecords;
    }

    public List<String> list() {
        List<String> list = new LinkedList<String>();
        for (Entry<Key, DatabaseSerializer> pair : databaseFiles.entrySet()) {
            list.addAll(pair.getValue().list());
        }
        return list;
    }

    public void deleteTable() throws IOException {
        String[] dirList = tablePath.toFile().list();
        for (String curDir : dirList) {
            String[] fileList = tablePath.resolve(curDir).toFile().list();
            for (String file : fileList) {
                Paths.get(tablePath.toString(), curDir, file).toFile().delete();
            }
            tablePath.resolve(curDir).toFile().delete();
        }
        tablePath.toFile().delete();
        databaseFiles.clear();
    }

    private void readTableDir() throws IOException {
        String[] dirList = tablePath.toFile().list();
        for (String dir : dirList) {
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
                int ndirectory = Integer.parseInt(dir.substring(0, dir.length()
                        - INTNUMBER));
                int nfile = Integer.parseInt(file.substring(0, file.length()
                        - INTNUMBER));
                DatabaseSerializer databaseFile;
                try {
                    databaseFile = new DatabaseSerializer(tablePath,
                            ndirectory, nfile);
                    databaseFiles.put(new Key(ndirectory, nfile), databaseFile);
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                    System.exit(-1);
                }
            }
        }
    }

    private int writeTableToDir() throws IOException {
        Iterator<Entry<Key, DatabaseSerializer>> it = databaseFiles.entrySet()
                .iterator();
        int diffnrecords = 0;
        while (it.hasNext()) {
            Entry<Key, DatabaseSerializer> databaseFile = it.next();
            diffnrecords += databaseFile.getValue().commit();
            if (databaseFile.getValue().getnrecords() == 0) {
                it.remove();
            }
        }
        return diffnrecords;
    }

}
