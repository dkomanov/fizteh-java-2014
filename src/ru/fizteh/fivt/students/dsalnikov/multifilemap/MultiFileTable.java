package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.dsalnikov.filemap.SingleFileTable;
import ru.fizteh.fivt.students.dsalnikov.utils.FilePathsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MultiFileTable implements Table {

    private Map<String, SingleFileTable> db;
    private File dbpath;

    public MultiFileTable(String path) {
        dbpath = new File(path);
        if (!dbpath.isDirectory() && dbpath.exists()) {
            System.err.println("proval");
            throw new IllegalArgumentException("error while creating multifiletable");
        } else {
            db = new HashMap<>();
            if (dbpath.list() != null) {
                for (File contents : dbpath.listFiles()) {
                    for (File files : contents.listFiles()) {
                        SingleFileTable sft = new SingleFileTable(files);
                        db.put(files.getAbsolutePath(), sft);
                    }
                }
            }
        }
    }

    public String getName() {
        return dbpath.getName();
    }

    public String get(String key) {
        SingleFileTable result = db.get(new FilePathsProvider(dbpath.toString(), key).getPath());
        if (result == null) {
            return null;
        } else {
            return result.get(key);
        }
    }


    public String put(String key, String value) {
        String path = new FilePathsProvider(dbpath.toString(), key).getPath();
        SingleFileTable result = db.get(path);
        if (result == null) {
            SingleFileTable nsft = new SingleFileTable(new File(path));
            db.put(path, nsft);
            return nsft.put(key, value);
        } else {
            return result.put(key, value);
        }
    }

    public List<String> list() {
        List<String> rv = new ArrayList<>();
        for (SingleFileTable sft : db.values()) {
            rv.addAll(sft.list());
        }
        return rv;
    }

    public String remove(String key) {
        String path = new FilePathsProvider(dbpath.toString(), key).getPath();
        SingleFileTable result = db.get(path);
        if (result == null) {
            return null;
        } else {
            return result.remove(key);
        }
    }

    public int size() {
        int counter = 0;
        for (SingleFileTable std : db.values()) {
            counter += std.size();
        }
        return counter;
    }

    public int commit() {
        int counter = 0;
        for (SingleFileTable std : db.values()) {
            counter += std.commit();
        }
        try {
            for (File dirs : dbpath.listFiles()) {
                for (File dbfile : dirs.listFiles()) {
                    if (dirs.listFiles().length == 0) {
                        Files.delete(dirs.toPath());
                    }
                }
            }
        } catch (NullPointerException e) {
            System.err.println("FUUUUUUUU\t" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return counter;
    }

    public int rollback() {
        int counter = 0;
        for (SingleFileTable sft : db.values()) {
            counter += sft.commit();

        }
        return counter;
    }

    public int exit() {
        return 0;
    }

    public int getAmountOfChanges() {
        int changesCounter = 0;

        for (SingleFileTable f : db.values()) {
            changesCounter += f.getChangesCount();
        }
        return changesCounter;
    }
}
