package ru.fizteh.fivt.students.sautin1.telnet.filemap;

import ru.fizteh.fivt.students.sautin1.telnet.ShellState;

import java.io.*;

/**
 * Class stores data which represents the current state of database.
 * It is passed through shell and commands.
 * Created by sautin1 on 10/12/14.
 */
public class DatabaseState<MappedValue, T extends GeneralTable<MappedValue>,
        P extends GeneralTableProvider<MappedValue, T>> extends ShellState {
    private T activeTable;
    private final P tableProvider;
    protected BufferedReader inStream;
    protected PrintWriter outStream;

    public DatabaseState(P tableProvider, BufferedReader inStream, PrintWriter outStream) {
        super(inStream, outStream);
        if (tableProvider == null) {
            throw new IllegalArgumentException("Wrong provider");
        }
        this.tableProvider = tableProvider;
        this.inStream = inStream;
        this.outStream = outStream;
    }

    public DatabaseState(P tableProvider) {
        this(tableProvider, new BufferedReader(new InputStreamReader(System.in)), new PrintWriter(System.out, true));
    }

    /**
     * Getter of activeTable field.
     * @return reference to the active table.
     */
    public T getActiveTable() {
        return activeTable;
    }

    /**
     * Setter of activeTable field.
     * @param activeTable a reference to a table which is supposed to become active.
     */
    public void setActiveTable(T activeTable) {
        this.activeTable = activeTable;
    }

    /**
     * Getter of tableProvider field.
     * @return provider.
     */
    public P getTableProvider() {
        return tableProvider;
    }

    public BufferedReader getInStream() {
        return inStream;
    }

    public PrintWriter getOutStream() {
        return outStream;
    }
}
