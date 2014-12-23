package ru.fizteh.fivt.students.irina_karatsapova.proxy;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.TableProvider;

import java.io.PrintWriter;

public class InterpreterState {
    public TableProvider tableProvider;
    public Table table = null;
    public PrintWriter out;

    public InterpreterState(TableProvider tableProvider, PrintWriter out) {
        this.tableProvider = tableProvider;
        this.out = out;
    }
}
