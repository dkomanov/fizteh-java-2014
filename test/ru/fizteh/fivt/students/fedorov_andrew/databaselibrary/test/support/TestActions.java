package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import static org.junit.Assert.*;
import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils.*;

import java.util.Map;
import java.util.Set;

import junit.framework.AssertionFailedError;
import ru.fizteh.fivt.storage.strings.Table;

public class TestActions {
    // forbid constructing
    private TestActions() {
	
    }

    private static <T> void assertSynchronized(T valueMap, T valueTable) {
	assertEquals("Map and Table old value not synchronized", valueMap,
		valueTable);
    }

    public final static TestAction PutNew = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    String key = randString(randInt(3, 10));
	    String value = randString(randInt(3, 10));

	    String oldValueTable = table.put(key, value);
	    String oldValueMap = backMap.put(key, value);

	    assertSynchronized(oldValueMap, oldValueTable);
	}
    };

    public final static TestAction PutOverwrite = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    String key = randElement(backMap.keySet());
	    if (key == null) {
		return;
	    }

	    String newValue = randString(randInt(3, 10));

	    String oldValueTable = table.put(key, newValue);
	    String oldValueMap = backMap.put(key, newValue);

	    assertSynchronized(oldValueMap, oldValueTable);
	}

    };

    public final static TestAction GetExistent = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    String key = randElement(backMap.keySet());
	    if (key == null) {
		return;
	    }

	    String valueTable = table.get(key);
	    String valueMap = backMap.get(key);

	    assertSynchronized(valueMap, valueTable);
	}
    };

    public final static TestAction GetNotExistent = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    String key = randString(randInt(3, 10));

	    String valueTable = table.get(key);
	    String valueMap = backMap.get(key);

	    assertSynchronized(valueMap, valueTable);
	}
    };

    public final static TestAction RemoveExistent = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    String key = randElement(backMap.keySet());
	    if (key == null) {
		return;
	    }

	    String oldValueTable = table.remove(key);
	    String oldValueMap = backMap.remove(key);

	    assertSynchronized(oldValueMap, oldValueTable);

	    String curValueTable = table.get(key);
	    String curValueMap = backMap.get(key);

	    assertNull("Removed value must be null", curValueTable);
	    assertNull("Removed value must be null", curValueMap);
	}
    };

    public final static TestAction RemoveNotExistent = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    String key = randString(randInt(3, 10));

	    String oldValueTable = table.remove(key);
	    String oldValueMap = backMap.remove(key);

	    assertSynchronized(oldValueMap, oldValueTable);

	    String curValueTable = table.get(key);
	    String curValueMap = backMap.get(key);

	    assertNull("Removed value must be null", curValueTable);
	    assertNull("Removed value must be null", curValueMap);
	}
    };

    public final static TestAction Size = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    int sizeTable = table.size();
	    int sizeMap = backMap.size();

	    assertSynchronized(sizeMap, sizeTable);
	}
    };

    public final static TestAction List = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    java.util.List<String> keysetTable = table.list();
	    Set<String> keysetMap = backMap.keySet();

	    assertSynchronized(keysetMap.size(), keysetTable.size());
	    for (String keyTable : keysetTable) {
		if (!keysetMap.contains(keyTable)) {
		    throw new AssertionFailedError(
			    "Found key that must not present");
		}
	    }
	}
    };

    public final static TestAction Commit = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    int diffsMap = countDifferences(lastCommittedMap, backMap);
	    int diffsTable = table.commit();

	    lastCommittedMap.clear();
	    lastCommittedMap.putAll(backMap);

	    assertSynchronized(diffsMap, diffsTable);
	}
    };

    public final static TestAction Rollback = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    int diffsMap = countDifferences(backMap, lastCommittedMap);
	    int diffsTable = table.rollback();

	    backMap.clear();
	    backMap.putAll(lastCommittedMap);

	    assertSynchronized(diffsMap, diffsTable);
	}
    };

    public final static TestAction GlobalCheck = new TestAction() {
	@Override
	public void perform(Table table, Map<String, String> backMap,
		Map<String, String> lastCommittedMap) {
	    assertSynchronized(backMap.size(), table.size());

	    for (String key : backMap.keySet()) {
		String valueMap = backMap.get(key);
		String valueTable = table.get(key);
		assertSynchronized(valueMap, valueTable);
	    }
	}
    };
}
