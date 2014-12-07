package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

/**
 * @author AlexeyZhuravlev
 */
public class StructuredTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException();
        } else {
            return new StructuredTableProvider(path);
        }
    }
}
