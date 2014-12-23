package ru.fizteh.fivt.students.anastasia_ermolaeva.util;

import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.NoActiveTableException;

public class DBState {
    private String currentTableName = "";
    private Object providerObject;

    public DBState(final Object tableHolder) {
        providerObject = tableHolder;
    }

    public final Object getTableHolder() {
        return providerObject;
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
