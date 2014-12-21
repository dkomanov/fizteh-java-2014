package ru.fizteh.fivt.students.vadim_mazaev.Remote;

import java.io.PrintStream;

import ru.fizteh.fivt.storage.structured.TableProvider;

public class ClientDbState extends DataBaseState {
    private PrintStream printStream;

    public ClientDbState(TableProvider manager, PrintStream printStream) {
        super(manager);
        this.printStream = printStream;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

}
