package ru.fizteh.fivt.students.moskupols.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.moskupols.parallel.CachingTableProvider;
import ru.fizteh.fivt.students.moskupols.parallel.ThreadSafeTableProviderFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by moskupols on 25.12.14.
 */
public class AutoCloseableTableProviderFactoryImpl implements AutoCloseableTableProviderFactory {

    private TableProviderFactory delegate = new ThreadSafeTableProviderFactory();
    private List<AutoCloseableCachingTableProvider> providersIssued = new LinkedList<>();
    private final SingleWriterLoggingProxyFactory logger;

    public AutoCloseableTableProviderFactoryImpl(SingleWriterLoggingProxyFactory logger) {
        this.logger = logger;
    }

    protected List<AutoCloseableCachingTableProvider> getProvidersIssued() {
        if (providersIssued == null) {
            throw new IllegalStateException("Already closed");
        }
        return providersIssued;
    }

    @Override
    public void close() throws Exception {
        for (AutoCloseableCachingTableProvider provider : getProvidersIssued()) {
            provider.close();
        }
    }

    @Override
    public TableProvider create(String path) throws IOException {
        return (TableProvider) logger.wrap(
                new AutoCloseableCachingTableProviderImpl(
                        (CachingTableProvider) delegate.create(path),
                        new LoggedAutoCloseableTableAdaptorFactory(logger)),
                AutoCloseableCachingTableProvider.class);
    }
}
