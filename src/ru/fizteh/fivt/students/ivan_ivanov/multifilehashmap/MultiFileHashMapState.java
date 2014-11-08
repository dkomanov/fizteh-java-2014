package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class MultiFileHashMapState {

    private MultiFileHashMapTableProvider provider;
    private MultiFileHashMapTable currentTable;
    private int flag;

    public MultiFileHashMapState(final File inFile) throws IOException {

        flag = 0;
        currentTable = null;
        MultiFileHashMapTableProviderFactory factory = new MultiFileHashMapTableProviderFactory();
        provider = (MultiFileHashMapTableProvider) factory.create(inFile.getPath());
    }

    public final int getFlag() {

        return flag;
    }

    public final void changeFlag() {

        if (0 == flag) {
            flag = 1;
        } else {
            flag = 0;
        }
    }


    public final MultiFileHashMapTable createTable(final String name) throws IOException {

        return provider.createTable(name);
    }

    public final MultiFileHashMapTable getTable(final String name) throws IOException {

        return provider.getTable(name);
    }

    public final MultiFileHashMapTable getCurrentTable() throws IOException {

        return currentTable;
    }

    public final void setCurrentTable(final String name) throws IOException {

        currentTable = (MultiFileHashMapTable) provider.getTable(name);
    }

    public final void setCurrentTable() throws IOException {
        currentTable = null;
    }

    public final  void deleteTable(final String name) throws IOException {
        provider.removeTable(name);
    }

    public final String putToCurrentTable(final String key, final String value) {
        return currentTable.put(key, value);
    }

    public final String getFromCurrentTable(final String key) {
        return currentTable.get(key);
    }

    public final String removeFromCurrentTable(final String key) {
        return currentTable.remove(key);
    }
    public final Map<String, String> getDataBaseFromCurrentTable() {
        return currentTable.getDataBase();
    }
    public final Set<String> getTableSet() {
        return provider.getTables();
    }
}
