package ru.fizteh.fivt.students.RadimZulkarneev.Main;


import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.RadimZulkarneev.Interpreter.InterpreterState;

public class DataBaseInterpreterState implements InterpreterState {
    private TableProvider provider;
    private Table usedTable;

    public DataBaseInterpreterState(TableProvider provider) {
        this.provider = provider;
        usedTable = null;
    }

    public void setUsedTable(Table other) {
        usedTable = other;
    }

    public Table getUsedTable() {
        return usedTable;
    }

    public TableProvider getTableProvider() {
        return provider;
    }
}
