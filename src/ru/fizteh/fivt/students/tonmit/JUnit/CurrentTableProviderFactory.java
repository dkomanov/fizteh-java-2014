package ru.fizteh.fivt.students.tonmit.JUnit;

import java.io.File;

public class CurrentTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("dir is null or empty");
        }
        File f = new File(dir).getAbsoluteFile();
        if (!f.mkdir()) {
            throw new IllegalArgumentException("Can't create directory: " + dir);
        }
        if (f.isFile()) {
            throw new IllegalArgumentException("db dir is a regular file");
        }
        if (!f.canWrite()) {
            throw new IllegalArgumentException("dir is read only");
        }
        return new CurrentTableProvider();
    }
}
