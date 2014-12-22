package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.structured.Table;

interface AutoCloseableTable extends Table, AutoCloseable {
    @Override
    void close();
}
