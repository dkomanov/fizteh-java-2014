package ru.fizteh.fivt.students.AlexanderKhalyapov.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;

public class TableState {
    private String currentTableName = "";
    private TableProvider holder;

    public TableState(final TableProvider tableHolder) {
        holder = tableHolder;
    }

    public final TableProvider getTableHolder() {
        return holder;
    }

    public final String getCurrentTableName() {
        return currentTableName;
    }

    public final void setCurrentTableName(final String currentTableName) {
        this.currentTableName = currentTableName;
    }

    /*
    * False - no chosen table.
    */
    public final boolean checkCurrentTable() {
        boolean result = currentTableName.equals("");
        if (result) {
            throw new NoActiveTableException();
        }
        return !result;
    }
}
