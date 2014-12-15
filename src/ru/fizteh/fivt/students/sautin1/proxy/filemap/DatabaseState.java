package ru.fizteh.fivt.students.sautin1.proxy.filemap;

/**
 * Class stores data which represents the current state of database.
 * It is passed through shell and commands.
 * Created by sautin1 on 10/12/14.
 */
public class DatabaseState<MappedValue, T extends GeneralTable<MappedValue>,
        P extends GeneralTableProvider<MappedValue, T>> {
    private T activeTable;
    private final P tableProvider;

    public DatabaseState() {
        tableProvider = null;
    }

    public DatabaseState(P tableProvider) {
        if (tableProvider == null) {
            throw new IllegalArgumentException("Wrong provider");
        }
        this.tableProvider = tableProvider;
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
}
