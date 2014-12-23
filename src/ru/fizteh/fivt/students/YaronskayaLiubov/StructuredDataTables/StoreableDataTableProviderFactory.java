package ru.fizteh.fivt.students.YaronskayaLiubov.StructuredDataTables;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 16.11.14.
 */

public class StoreableDataTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Directory is null");
        }
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Empty directory name");
        }
        return new StoreableDataTableProvider(path);
    }
}
