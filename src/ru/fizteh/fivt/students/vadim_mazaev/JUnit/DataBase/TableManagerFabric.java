package ru.fizteh.fivt.students.vadim_mazaev.JUnit.DataBase;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public final class TableManagerFabric implements TableProviderFactory {
    public TableManager create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new TableManager(dir);
    }
}
