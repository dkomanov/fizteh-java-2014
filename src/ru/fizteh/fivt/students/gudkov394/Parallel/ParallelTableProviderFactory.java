package ru.fizteh.fivt.students.gudkov394.Parallel;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

/**
 * Created by kagudkov on 17.11.14.
 */
public class ParallelTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        return new ParallelTableProvider(dir);
    }
}
