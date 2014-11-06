package ru.fizteh.fivt.students.standy66_new.storage.strings;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

/**
 * Created by astepanov on 20.10.14.
 */
public class StringDatabaseFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("dir is null or empty");
        }
        File f = new File(dir).getAbsoluteFile();
        f.mkdirs();
        if (f.isFile()) {
            throw new IllegalStateException("db dir is a regular file");
        }
        if (!f.canRead()) {
            throw new IllegalStateException("dir cannot be read");
        }
        return new StringDatabase(f);
    }
}
