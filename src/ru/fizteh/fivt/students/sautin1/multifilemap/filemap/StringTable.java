package ru.fizteh.fivt.students.sautin1.multifilemap.filemap;

/**
 * Database table which maps string to string.
 * Created by sautin1 on 10/11/14.
 */
public class StringTable extends GeneralTable<String> implements DiffTable {

    public StringTable(String name, boolean autoCommit) {
        super(name, autoCommit);
    }

    public StringTable(String name) {
        this(name, true);
    }
}
