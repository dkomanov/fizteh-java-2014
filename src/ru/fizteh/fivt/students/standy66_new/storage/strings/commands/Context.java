package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

/**
 * Created by astepanov on 20.10.14.
 */
public class Context {
    private TableProvider provider;
    private Table currentTable;

    public Context(TableProvider provider) throws NullPointerException {
        if (provider == null) {
            throw new NullPointerException("database must not be null");
        }
        this.provider = provider;
        this.currentTable = null;
    }

    public TableProvider getProvider() {
        return provider;
    }

    public Table getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(Table currentTable) {
        this.currentTable = currentTable;
    }
}
