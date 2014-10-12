package ru.fizteh.fivt.students.sautin1.filemap;

/**
 * Created by sautin1 on 10/12/14.
 */
public class StringDatabaseState extends DatabaseState<String, StringTable> {
    public StringDatabaseState(StringTableProvider tableProvider) {
        super(tableProvider);
    }
}
