package ru.fizteh.fivt.students.sautin1.filemap;

/**
 * Database table which maps string to string.
 * Created by sautin1 on 10/11/14.
 */
public class StringTable extends GeneralTable<String> implements DiffTable {

    StringTable(String name, boolean autoCommit) {
        super(name, autoCommit);
    }

    StringTable(String name) {
        this(name, true);
    }
}
