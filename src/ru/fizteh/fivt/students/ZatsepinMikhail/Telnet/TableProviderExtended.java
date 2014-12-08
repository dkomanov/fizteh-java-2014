package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.Closeable;

public interface TableProviderExtended extends TableProvider, Closeable {
    Table getCurrentTable();
    void setCurrentTable(Table newTable);
}
