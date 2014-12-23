package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.TableProvider;

import java.io.PrintWriter;

public class InterpreterStateDatabase {
    private TableProvider tableProvider;
    private Table table = null;
    public PrintWriter out;

    public InterpreterStateDatabase(TableProvider tableProvider, PrintWriter out) {
        this.tableProvider = tableProvider;
        this.out = out;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void stopUsingTable() {
        table = null;
    }

    public TableProvider getTableProvider() {
        return tableProvider;
    }
}
