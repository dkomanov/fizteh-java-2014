package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;

import java.util.Map;

public interface TestAction {
    void perform(Table table, Map<String, String> backMap, Map<String, String> lastCommittedMap)
            throws DatabaseException;
}
