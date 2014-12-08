package ru.fizteh.fivt.students.sautin1.junit.filemap;

/**
 * Class stores data which represents the current state of string database.
 * It is passed through shell and commands.
 * Created by sautin1 on 10/12/14.
 */
public class StringDatabaseState extends DatabaseState<String, StringTable> {
    public StringDatabaseState(StringTableProvider tableProvider) {
        super(tableProvider);
    }
}
