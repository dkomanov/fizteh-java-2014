package ru.fizteh.fivt.students.dnovikov.junit;

import ru.fizteh.fivt.students.dnovikov.junit.Interpreter.InterpreterState;

public class DataBaseState implements InterpreterState {
    private DataBaseProvider provider;
    private DataBaseTable currentTable;

    public DataBaseState(DataBaseProvider provider) {
        this.provider = provider;
        this.currentTable = null;
    }

    public DataBaseTable getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(DataBaseTable currentTable) {
        this.currentTable = currentTable;
    }

    public DataBaseProvider getTableProvider() {
        return provider;
    }

    public void saveCurrentTable() {
        if (currentTable != null) {
            currentTable.save();
        }
    }
}
