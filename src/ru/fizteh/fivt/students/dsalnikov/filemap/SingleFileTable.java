package ru.fizteh.fivt.students.dsalnikov.filemap;


import ru.fizteh.fivt.students.dsalnikov.utils.FileMapUtils;
import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;

import java.io.File;
import java.util.Map;


public class SingleFileTable extends ShellState implements Table {

    private Map<String, String> storage;
    private File dbfile;

    //readToMap from file to map
    public SingleFileTable() {
        try {
            String path = System.getProperty("db.file");
            dbfile = new File(path);
            storage = FileMapUtils.readToMap(dbfile);
        } catch (Throwable exc) {
            System.err.println("file processing failed. Paths might be incorrect");
            System.exit(1);
        }
    }

    public SingleFileTable(String path) {
        try {
            dbfile = new File(path);
            storage = FileMapUtils.readToMap(dbfile);
        } catch (Throwable exc) {
            System.err.println("file processing failed. Paths might be incorrect");
            System.exit(1);
        }
    }

    @Override
    public String get(String key) {
        String result = storage.get(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println(String.format("found\n'%s'", result));
        }
        return result;
    }

    @Override
    public String put(String key, String value) {
        String rv = storage.put(key, value);
        if (rv == null) {
            System.out.println("new");
        } else {
            System.out.println(String.format("overwrite\n'%s'", rv));
        }
        return rv;
    }

    @Override
    public void list() {
        for (String s : storage.keySet()) {
            System.out.println(s);
        }
    }

    @Override
    public String remove(String key) {
        String result = storage.remove(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
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

    public int size() {
        return storage.size();
    }
}
