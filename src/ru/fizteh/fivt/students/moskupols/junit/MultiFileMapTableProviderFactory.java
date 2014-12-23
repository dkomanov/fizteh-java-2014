package ru.fizteh.fivt.students.moskupols.junit;

import java.nio.file.Paths;

/**
 * Created by moskupols on 17.11.14.
 */
public class MultiFileMapTableProviderFactory implements KnownDiffTableProviderFactory {
    @Override
    public KnownDiffTableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("db dir should not be null");
        }
        return new MultiFileMapTableProvider(Paths.get(dir));
    }
}
