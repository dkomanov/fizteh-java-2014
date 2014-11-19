package ru.fizteh.fivt.students.Volodin_Denis.JUnit.main;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.InterpreterState;

public class StringInterpreterState implements InterpreterState {
    private TableProvider tableProvider;
    private Table table;
    
    public StringInterpreterState(TableProvider tableProvider) {
        this.tableProvider = tableProvider;
    }
    
    public TableProvider getTableProvider() {
        return tableProvider;
    }
    
    public Table getTable() {
        return table;
    }
    
    public void setTable(Table table) {
        this.table = table; 
    }
}
