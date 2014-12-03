package ru.fizteh.fivt.students.irina_karatsapova.parallel;

import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.TableProvider;

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
