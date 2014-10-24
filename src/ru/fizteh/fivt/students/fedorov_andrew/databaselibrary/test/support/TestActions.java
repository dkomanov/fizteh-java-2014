package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import junit.framework.AssertionFailedError;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils.*;

public class TestActions {
    public static final TestAction PUT_NEW = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
                            Map<String, String> lastCommittedMap) {
            String key = randString(randInt(3, 10));
            String value = randString(randInt(3, 10));

            String oldValueTable = table.put(key, value);
            String oldValueMap = backMap.put(key, value);

            assertSynchronized(oldValueMap, oldValueTable);
        }
    };
    public static final TestAction PUT_OVERWRITE = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
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
    public static final TestAction GET_EXISTENT = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
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
    public static final TestAction GET_NOT_EXISTENT = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
                            Map<String, String> lastCommittedMap) {
            String key = randString(randInt(3, 10));

            String valueTable = table.get(key);
            String valueMap = backMap.get(key);

            assertSynchronized(valueMap, valueTable);
        }
    };
    public static final TestAction REMOVE_EXISTENT = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
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
    public static final TestAction REMOVE_NOT_EXISTENT = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
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
    public static final TestAction SIZE = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
                            Map<String, String> lastCommittedMap) {
            int sizeTable = table.size();
            int sizeMap = backMap.size();

            assertSynchronized(sizeMap, sizeTable);
        }
    };
    public static final TestAction LIST = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
                            Map<String, String> lastCommittedMap) {
            java.util.List<String> keysetTable = table.list();
            Set<String> keysetMap = backMap.keySet();

            assertSynchronized(keysetMap.size(), keysetTable.size());
            for (String keyTable : keysetTable) {
                if (!keysetMap.contains(keyTable)) {
                    throw new AssertionFailedError("Found key that must not present");
                }
            }
        }
    };
    public static final TestAction COMMIT = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
                            Map<String, String> lastCommittedMap) throws DatabaseException {
            int diffsMap = countDifferences(lastCommittedMap, backMap);
            int diffsTable = table.commit();

            lastCommittedMap.clear();
            lastCommittedMap.putAll(backMap);

            assertSynchronized(diffsMap, diffsTable);
        }
    };
    public static final TestAction ROLLBACK = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
                            Map<String, String> lastCommittedMap) {
            int diffsMap = countDifferences(backMap, lastCommittedMap);
            int diffsTable = table.rollback();

            backMap.clear();
            backMap.putAll(lastCommittedMap);

            assertSynchronized(diffsMap, diffsTable);
        }
    };
    public static final TestAction GLOBAL_CHECK = new TestAction() {
        @Override
        public void perform(Table table,
                            Map<String, String> backMap,
                            Map<String, String> lastCommittedMap) {
            assertSynchronized(backMap.size(), table.size());

            for (String key : backMap.keySet()) {
                String valueMap = backMap.get(key);
                String valueTable = table.get(key);
                assertSynchronized(valueMap, valueTable);
            }
        }
    };

    // forbid constructing
    private TestActions() {

    }

    private static <T> void assertSynchronized(T valueMap, T valueTable) {
        assertEquals("Map and Table old value not synchronized", valueMap, valueTable);
    }
}
