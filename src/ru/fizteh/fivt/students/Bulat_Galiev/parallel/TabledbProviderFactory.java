package ru.fizteh.fivt.students.Bulat_Galiev.parallel;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TabledbProviderFactory implements TableProviderFactory {
    public final TableProvider create(final String dir) {
        if (dir != null) {
            return new TabledbProvider(dir);
        } else {
            throw new IllegalArgumentException("Null name directory.");
        }
    }
}
