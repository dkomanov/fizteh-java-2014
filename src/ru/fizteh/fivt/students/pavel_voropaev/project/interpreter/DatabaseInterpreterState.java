package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.InputStream;
import java.io.PrintStream;

public class DatabaseInterpreterState implements InterpreterState {
    private Table activeTable;
    private TableProvider database;

    private InputStream in;
    private PrintStream out;
    private PrintStream err;

    public DatabaseInterpreterState(TableProvider database) {
        this.in = System.in;
        this.out = System.out;
        this.err = System.err;

        activeTable = null;
        this.database = database;
    }

    public DatabaseInterpreterState(TableProvider database, InputStream in, PrintStream out, PrintStream err) {
        this.in = in;
        this.out = out;
        this.err = err;

        activeTable = null;
        this.database = database;
    }

    public TableProvider getDatabase() {
        return database;
    }

    public Table getActiveTable() {
        return activeTable;
    }

    public void setActiveTable(Table table) {
        activeTable = table;
    }


    @Override
    public InputStream getInputStream() {
        return in;
    }

    @Override
    public PrintStream getOutputStream() {
        return out;
    }

    @Override
    public PrintStream getErrorStream() {
        return err;
    }

    @Override
    public boolean isExitSafe() {
        return (activeTable == null || activeTable.getNumberOfUncommittedChanges() == 0); // Possibly dangerous
    }
}
