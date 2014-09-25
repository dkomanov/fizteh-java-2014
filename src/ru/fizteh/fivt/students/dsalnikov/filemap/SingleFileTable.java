package ru.fizteh.fivt.students.dsalnikov.filemap;


import ru.fizteh.fivt.students.dsalnikov.utils.FileMapUtils;
import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;


import java.util.Map;
import java.util.Set;
import java.io.File;


public class SingleFileTable extends ShellState implements Table {

    private Map<String, String> storage;
    private File dbfile;

    //read from file to map
    public SingleFileTable() {
        try {
            FileMapUtils.readToMap();
        } catch (Throwable exc) {
            System.err.println("file processing failed. Paths might be incorrect");
            System.exit(1);
        }
    }

    @Override
    public String get(String key) {
        return storage.get(key);
    }

    @Override
    public String put(String key, String value) {
        return storage.put(key, value);
    }

    @Override
    public Set<String> list() {
        return storage.keySet();
    }

    @Override
    public String remove(String key) {
        return storage.remove(key);
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
}
