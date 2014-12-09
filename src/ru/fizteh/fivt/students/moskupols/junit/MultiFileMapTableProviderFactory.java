package ru.fizteh.fivt.students.moskupols.junit;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.nio.file.Paths;

/**
 * Created by moskupols on 17.11.14.
 */
public class MultiFileMapTableProviderFactory implements TableProviderFactory {
    @Override
    public KnownDiffTableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("db dir should not be null");
        }
        return new MultiFileMapTableProvider(Paths.get(dir));
    }
}
