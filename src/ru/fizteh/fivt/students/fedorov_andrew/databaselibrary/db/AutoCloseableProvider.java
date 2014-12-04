package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.structured.TableProvider;

interface AutoCloseableProvider extends TableProvider, AutoCloseable {
    @Override
    void close();
}
