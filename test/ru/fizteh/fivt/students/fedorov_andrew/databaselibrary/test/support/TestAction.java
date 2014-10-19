package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import java.util.Map;

import ru.fizteh.fivt.storage.strings.Table;

public interface TestAction {
    public void perform(Table table, Map<String, String> backMap,
	    Map<String, String> lastCommittedMap);
}