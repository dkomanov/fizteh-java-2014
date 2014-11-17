package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.Map;

public interface TestAction {
    void perform(Table table, Map<String, String> backMap, Map<String, String> lastCommittedMap);
}
