package ru.fizteh.fivt.students.dsalnikov.filemap;

import ru.fizteh.fivt.students.dsalnikov.utils.CountingTools;
import ru.fizteh.fivt.students.dsalnikov.utils.FileMapUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class SingleFileTable implements Table {

    private final File dbfile;
    private Set<String> deleted;
    private Map<String, String> changed;
    private Map<String, String> storage;
    private int changesCount;

    //readToMap from file to map
    public SingleFileTable(File file) {
        dbfile = file;
        initialize();
        try {
            storage = FileMapUtils.readToMap(dbfile);
        } catch (Throwable exc) {
            System.err.println("file processing failed. Paths might be incorrect");
            System.exit(1);
        }
    }

    //Warning:only can be used with for empth files
    public SingleFileTable(String filepath) {
        dbfile = new File(filepath);
        initialize();
    }

    private void initialize() {
        changesCount = 0;
        deleted = new HashSet<>();
        changed = new HashMap<>();
    }

    @Override
    public String getName() {
        return dbfile.getName();
    }

    @Override
    public String get(String key) {
        String result;
        result = changed.get(key);
        if (result == null) {
            if (deleted.contains(key)) {
                return null;
            }
            result = storage.get(key);
        }
        return result;
    }

    @Override
    public String put(String key, String value) {
        String vl = storage.get(key);
        String rv = changed.put(key, value);
        if (rv == null) {
            changesCount += 1;
            if (!deleted.contains(key)) {
                rv = vl;
            }
        }
        if (vl != null) {
            deleted.add(key);
        }
        return rv;
    }

    @Override
    public List<String> list() {
        List<String> rv = new ArrayList<>();
        rv.addAll(storage.keySet());
       // for (String s : storage.keySet()) {
         //   System.out.println(s);
        //}
        return rv;
    }

    @Override
    public String remove(String key) {
        String result = storage.get(key);
        if (result == null && !deleted.contains(key)) {
            result = storage.get(key);
        }
        if (changed.containsKey(key)) {
            changesCount -= 1;
            changed.remove(key);
            if (storage.containsKey(key)) {
                deleted.add(key);
            }
        } else {
            if (storage.containsKey(key) && !deleted.contains(key)) {
                deleted.add(key);
                changesCount += 1;
            }
        }
        return result;
    }

    @Override
    public int exit() {
        try {
            FileMapUtils.flush(dbfile, storage);
        } catch (Throwable exc) {
            System.err.println(exc.getMessage());
            System.exit(1);
        }
        return 0;
    }

    @Override
    public int size() {
        return CountingTools.countSize(storage, changed, deleted);
    }

    @Override
    public int commit() {
        int result = getChangesCount();
        for (String key : deleted) {
            storage.remove(key);
        }
        storage.putAll(changed);
        try {
            FileMapUtils.flush(dbfile, storage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reset();
        return result;
    }

    private void reset() {
        changed.clear();
        deleted.clear();
        changesCount = 0;
    }

    @Override
    public int rollback() {
        int result = getChangesCount();
        reset();
        return result;
    }

    public int getChangesCount() {
        return CountingTools.countChanges(storage, changed, deleted);
    }
}
