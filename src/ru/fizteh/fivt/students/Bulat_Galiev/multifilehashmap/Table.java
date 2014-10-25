package ru.fizteh.fivt.students.Bulat_Galiev.multifilehashmap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Table {
    private String tableName;
    private Path tablePath;
    private Map<Key, DatabaseSerializer> databaseFiles;

    public Table(final Path path, final String name) {
        databaseFiles = new HashMap<Key, DatabaseSerializer>();
        tablePath = path;
        tableName = name;
        try {
            readTableDir();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading table "
                    + tableName, e);
        }
    }

    public void commit() throws IOException {
        try {
            writeTableToDir();
        } catch (IOException e) {
            throw new IOException("Error writing table " + tableName, e);
        }
    }

    public void get(final String key) throws IOException {
        DatabaseSerializer databaseFile = databaseFiles.get(new Key(key));
        databaseFile.get(key);
    }

    public void put(final String key, final String value) throws IOException {
        if (!key.equals("") && !value.equals("")) {
            DatabaseSerializer databaseFile = databaseFiles.get(new Key(key));
            if (databaseFile == null) {
                int nbytes = key.getBytes("UTF-8")[0];
                int ndirectory = Math.abs(nbytes % 16);
                int nfile = Math.abs((nbytes / 16) % 16);
                databaseFile = new DatabaseSerializer(tablePath, ndirectory,
                        nfile);
                databaseFiles.put(new Key(ndirectory, nfile), databaseFile);
            }
            databaseFile.put(key, value);
        } else {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
    }

    public void remove(final String key) throws IOException {
        if (!key.equals("")) {
            DatabaseSerializer databaseFile = databaseFiles.get(new Key(key));
            databaseFile.remove(key);
        } else {
            throw new IllegalArgumentException("incorrect key");
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

    public void list() throws IOException {
        List<String> list = new LinkedList<String>();
        for (Entry<Key, DatabaseSerializer> pair : databaseFiles.entrySet()) {
            list.addAll(pair.getValue().list());
        }
        int iteration = 0;
        for (String current : list) {
            iteration++;
            System.out.print(current);
            if (iteration != list.size()) {
                System.out.print(", ");
            }
        }
        System.out.println();
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
            if (!curDir.toFile().isDirectory()) {
                throw new IOException();
            }
            String[] fileList = curDir.toFile().list();
            if (fileList.length == 0) {
                throw new IOException();
            }
            for (String file : fileList) {
                Path filePath = curDir.resolve(file);
                if (!filePath.toFile().isFile()) {
                    throw new IOException();
                }
                int ndirectory = Integer.parseInt(dir.substring(0,
                        dir.length() - 4));
                int nfile = Integer.parseInt(file.substring(0,
                        file.length() - 4));
                DatabaseSerializer databaseFile;
                try {
                    databaseFile = new DatabaseSerializer(tablePath,
                            ndirectory, nfile);
                    databaseFiles.put(new Key(ndirectory, nfile), databaseFile);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeTableToDir() throws IOException {
        Iterator<Entry<Key, DatabaseSerializer>> it = databaseFiles.entrySet()
                .iterator();
        while (it.hasNext()) {
            Entry<Key, DatabaseSerializer> databaseFile = it.next();
            databaseFile.getValue().disconnect();
            if (databaseFile.getValue().getnrecords() == 0) {
                it.remove();
            }
        }
    }
}
