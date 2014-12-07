package ru.fizteh.fivt.students.pershik.Storeable;

/**
 * Created by pershik on 11/8/14.
 */

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by pershik on 10/28/14.
 */
public class StoreableTableProviderFactory implements TableProviderFactory {

    public StoreableTableProviderFactory() { }

    @Override
    public StoreableTableProvider create(String dir)
            throws IllegalArgumentException, IOException {
        if (dir == null) {
            throw new IllegalArgumentException("null is not a directory");
        }
        File dbDir = new File(dir);
        if (dbDir.exists() && !dbDir.isDirectory()) {
            throw new IllegalArgumentException(dir + " isn't directory");
        }
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) {
                throw new IOException("Can't create " + dir);
            }
        }
        return new StoreableTableProvider(dir);
    }
}

