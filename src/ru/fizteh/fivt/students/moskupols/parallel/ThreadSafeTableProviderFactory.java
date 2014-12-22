package ru.fizteh.fivt.students.moskupols.parallel;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.moskupols.junit.MultiFileMapTableProvider;
import ru.fizteh.fivt.students.moskupols.storeable.StringBackedTableProviderFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by moskupols on 14.12.14.
 */
public class ThreadSafeTableProviderFactory implements TableProviderFactory {
    private final TableProviderFactory structuredProviderFactory;

    public ThreadSafeTableProviderFactory() {
        this.structuredProviderFactory =
                new StringBackedTableProviderFactory(
                        dir -> new MultiFileMapTableProvider(
                                Paths.get(dir), ThreadSafeMultiFileMapTableAdaptor::new));
    }

    @Override
    public TableProvider create(String path) throws IOException {
        return new ThreadSafeCachingTableProvider(structuredProviderFactory.create(path));
    }
}
