package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.TableProvider;

import java.io.PrintWriter;

public class InterpreterStateDatabase {
    public TableProvider tableProvider;
    public Table table = null;
    public PrintWriter out;

    public InterpreterStateDatabase(TableProvider tableProvider, PrintWriter out) {
        this.tableProvider = tableProvider;
        this.out = out;
    }
}
