package ru.fizteh.fivt.students.sautin1.multifilemap.filemap;

/**
 * Class stores data which represents the current state of database.
 * It is passed through shell and commands.
 * Created by sautin1 on 10/12/14.
 */
public class DatabaseState<MappedValue, T extends GeneralTable<MappedValue>> {
    private GeneralTable<MappedValue> activeTable;
    private final GeneralTableProvider<MappedValue, T> tableProvider;

    public DatabaseState() {
        tableProvider = null;
    }

    public DatabaseState(GeneralTableProvider<MappedValue, T> tableProvider) {
        if (tableProvider == null) {
            throw new IllegalArgumentException("Wrong provider");
        }
        this.tableProvider = tableProvider;
    }

    /**
     * Getter of activeTable field.
     * @return reference to the active table.
     */
    public GeneralTable<MappedValue> getActiveTable() {
        return activeTable;
    }

    /**
     * Setter of activeTable field.
     * @param activeTable a reference to a table which is supposed to become active.
     */
    public void setActiveTable(GeneralTable<MappedValue> activeTable) {
        this.activeTable = activeTable;
    }

    /**
     * Getter of tableProvider field.
     * @return provider.
     */
    public GeneralTableProvider<MappedValue, T> getTableProvider() {
        return tableProvider;
    }
}
