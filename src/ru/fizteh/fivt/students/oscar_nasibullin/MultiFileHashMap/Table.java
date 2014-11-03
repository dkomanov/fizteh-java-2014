package ru.fizteh.fivt.students.oscar_nasibullin.MultiFileHashMap;



import java.io.*;
import java.lang.String;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Table  {

    private static Map<DataFileHasher, DataFile> datFiles;
    final String name;


    public Table(String tableName) {
        name = tableName;
    }

    public void open() throws Exception {
        datFiles = new TreeMap<>();
        String tablePath = System.getProperty("fizteh.db.dir") + "/" + name;

        if (!Paths.get(tablePath).toFile().exists()) {
            Paths.get(tablePath).toFile().mkdir();
        } else if (!Paths.get(tablePath).toFile().isDirectory()) {
            throw new Exception(tablePath + ": is not a directory");
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                DataFileHasher hasher = new DataFileHasher(i,j);
                DataFile newDataFile = new DataFile(tablePath ,hasher);
                datFiles.put(hasher, newDataFile);
            }
        }
    }

    public final void commit() {
        for (Map.Entry<DataFileHasher, DataFile> entry : datFiles.entrySet()) {
            entry.getValue().commit();
        }
    }


    public final String put(final List<String> args) {
        if (args.size() != 3) {
            throw new IllegalArgumentException("Illegal arguments for put");
        }

        DataFile data = datFiles.get(new DataFileHasher(args.get(1)));
        String rezultMessage = "";
        if (data.containsKey(args.get(1))) {
            rezultMessage = "overwrite\n" + data.get(args.get(1));
            data.remove(args.get(1));
            data.put(args.get(1), args.get(2));
        } else {
            rezultMessage = "new";
            data.put(args.get(1), args.get(2));
        }
        return rezultMessage;
    }

    public final String get(final List<String> args) {
        if (args.size() != 2) {
            throw new IllegalArgumentException("Illegal arguments for get");
        }

        DataFile data = datFiles.get(new DataFileHasher(args.get(1)));
        String rezultMessage = "";
        if (data.containsKey(args.get(1))) {
            rezultMessage = "found\n" + data.get(args.get(1));
        } else {
            rezultMessage = "not found";
        }
        return rezultMessage;
    }

    public final String remove(final List<String> args) {
        if (args.size() != 2) {
            throw new IllegalArgumentException("Illegal arguments for remove");
        }

        DataFile data = datFiles.get(new DataFileHasher(args.get(1)));
        String rezultMessage = "";
        if (data.containsKey(args.get(1))) {
            rezultMessage = "removed";
            data.remove(args.get(1));
        } else {
            rezultMessage = "not found";
        }
        return rezultMessage;
    }

    public final String list(final List<String> args) {
        if (args.size() != 1) {
            throw new IllegalArgumentException("Illegal arguments for list");
        }

        boolean firstWord = true;
        String rezultMessage = "";

        for (Map.Entry<DataFileHasher, DataFile> dataEntry : datFiles.entrySet())

            for (Map.Entry<String, String> entry : dataEntry.getValue().entrySet()) {
            if (firstWord) {
                rezultMessage = entry.getKey();
                firstWord = false;
            } else {
                rezultMessage += ", " + entry.getKey();
            }
        }
        return rezultMessage;
    }

    public int size() {
        int totalSize = 0;
        for (Map.Entry<DataFileHasher, DataFile> entry : datFiles.entrySet()) {
            totalSize += entry.getValue().size();
        }
        return totalSize;
    }

    public void clear() {
        String tablePath = System.getProperty("fizteh.db.dir") + "/" + name;
        File[] folders = Paths.get(tablePath).toFile().listFiles();
        if (folders == null)
            return;
        for (File folder : folders) {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null)
                    for (File file : files)
                        file.delete();
            }
            folder.delete();
        }
        new File(tablePath).delete();
    }


    public void close() throws Exception {
        if (!datFiles.isEmpty()) {
            for (Map.Entry<DataFileHasher, DataFile> entry : datFiles.entrySet()) {
                entry.getValue().close();
            }
            String tablePath = System.getProperty("fizteh.db.dir") + "/" + name;
            File[] folders = Paths.get(tablePath).toFile().listFiles();
            if (folders != null) {
                for (File folder : folders) {
                    if (folder.exists() && folder.isDirectory() && folder.list().length == 0) {
                        folder.delete();
                    }
                }
            }
            datFiles.clear();
        }
    }
}
