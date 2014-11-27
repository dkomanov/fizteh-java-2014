package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;


import java.io.File;
import java.io.IOException;

public class MultiFileHashMapTableProviderFactory {

    public final MultiFileHashMapTableProvider create(final String dir) throws IOException {
        return new MultiFileHashMapTableProvider(new File(dir));
    }
}
