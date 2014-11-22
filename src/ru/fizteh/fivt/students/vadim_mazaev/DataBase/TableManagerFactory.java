package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public final class TableManagerFactory implements TableProviderFactory {
    public TableManagerFactory() {
        //Do nothing, only for implemented interface.
    }
    
    public TableProvider create(String dir) throws DataBaseIOException {
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new TableManager(dir);
    }
}
