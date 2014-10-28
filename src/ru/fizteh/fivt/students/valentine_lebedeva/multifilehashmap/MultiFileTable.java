package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.valentine_lebedeva.Table;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.FileMapTable;

public final class MultiFileTable extends Table {
    private static final int MAX_NUMBER_OF_DIRECTORIES = 16;
    private static final int MAX_NUMBER_OF_FILES = 16;
    private File directory;

    public MultiFileTable(final String fileName) throws IOException {
        super();
        directory = new File(fileName);
        directory.mkdir();
        base = this.read();
    }

    @Override
    public Map<String, String> read() throws IOException {
        if (directory.list().length != 0) {
            for (final File dir : directory.listFiles()) {
                for (final File file : dir.listFiles()) {
                    FileMapTable tmp = new FileMapTable(file.getAbsolutePath());
                    base.putAll(tmp.getBase());
                    tmp.close();
                }
            }
        }
        return base;
    }

    public File getDirectory() {
        return directory;
    }

    @Override
    public void write() throws IOException {
        boolean curDirCreated = false;
        ArrayList<Map<String, String>> keys = new ArrayList<>();
        for (int i = 0; i < MAX_NUMBER_OF_DIRECTORIES; i++) {
            curDirCreated = false;
            String directName = String.format("%d.dir", i);
            File curDir = new File(directory.getAbsolutePath(), directName);
            keys.clear();
            for (int j = 0; j < MAX_NUMBER_OF_FILES; j++) {
                keys.add(new HashMap<>());
            }
            for (String key : base.keySet()) {
                if (getNumberOfDirectory(key) == i) {
                    if (!curDirCreated) {
                        curDir.mkdir();
                    }
                    curDirCreated = true;
                    keys.get(getNumberOfFile(key)).put(key, base.get(key));
                }
            }
            if (curDirCreated) {
                for (int j = 0; j < MAX_NUMBER_OF_FILES; j++) {
                    if (!keys.get(j).isEmpty()) {
                        String fileName = String.format("%d.dat", j);
                        File curFile = new File(curDir.getAbsolutePath(),
                                fileName);
                        FileMapTable tmp = new FileMapTable(
                                curFile.getAbsolutePath());
                        tmp.setBase(keys.get(j));
                        tmp.close();
                    }
                }
            }
        }
    }

    public int getNumberOfDirectory(final String key) {
        int hashcode = key.hashCode();
        return hashcode % MAX_NUMBER_OF_DIRECTORIES;
    }

    public int getNumberOfFile(final String key) {
        int hashcode = key.hashCode();
        return hashcode / MAX_NUMBER_OF_DIRECTORIES % MAX_NUMBER_OF_FILES;
    }

    @Override
    public void close() throws IOException {
        this.write();
    }
}
