package ru.fizteh.fivt.students.sautin1.parallel.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.parallel.shell.FileUtils;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.TableRow;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableTable;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableTableProviderFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by sautin1 on 12/12/14.
 */
public class StoreableTableTest {
    private Path testDir;
    private StoreableTableProviderFactory factory;
    private StoreableTableProvider provider;
    private String tableName;
    private String testString;
    private String existingDirectoryName;
    private String keyPrefix;
    private List<Class<?>> valueTypes;
    private int entryQuantity;
    private StoreableTable table;

    @Before
    public void setUp() throws Exception {
        testDir = Paths.get("").resolve("test");
        factory = new StoreableTableProviderFactory();
        provider = factory.create(testDir.toString());
        tableName = "testTable";
        existingDirectoryName = "existingDir";
        keyPrefix = "key";
        testString = "testString";
        entryQuantity = 100;
        valueTypes = new ArrayList<>();
        valueTypes.add(Integer.class);
        valueTypes.add(Boolean.class);
        valueTypes.add(String.class);
        table = provider.createTable(tableName, valueTypes);
    }

    @After
    public void tearDown() throws Exception {
        try {
            provider.removeTable(tableName);
        } catch (Exception e) {
            // haven't created such table
        }
        FileUtils.clearDirectory(testDir);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(table.getName(), tableName);
    }

    @Test
    public void testPut() throws Exception {
        // add completely new entry
        String firstKey = keyPrefix + "New";
        TableRow firstValue = new TableRow(valueTypes);
        firstValue.setColumnAt(0, Integer.MAX_VALUE);
        firstValue.setColumnAt(1, false);
        firstValue.setColumnAt(2, "");
        Storeable answer = table.put(firstKey, firstValue);
        assertNull(answer);

        // overwrite recently added entries
        Storeable secondValue = new TableRow(firstValue);
        secondValue.setColumnAt(1, true);
        answer = table.put(firstKey, secondValue);
        assertTrue(answer.equals(firstValue));

        // overwrite already overwritten entry
        TableRow thirdValue = new TableRow(firstValue);
        answer = table.put(firstKey, thirdValue);
        assertEquals(answer, secondValue);

        // add recently deleted entry
        table.remove(firstKey);
        assertEquals(table.size(), 0);
        answer = table.put(firstKey, firstValue);
        assertNull(answer);

        // overwrite existing old entry
        table.commit();
        answer = table.put(firstKey, secondValue);
        assertEquals(answer, firstValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() throws Exception {
        table.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmpty() throws Exception {
        TableRow firstValue = new TableRow(valueTypes);
        firstValue.setColumnAt(0, Integer.MAX_VALUE);
        firstValue.setColumnAt(1, false);
        firstValue.setColumnAt(2, "");
        table.put("", firstValue);
    }

    @Test
    public void testGet() throws Exception {
        // get newly added entry
        String firstKey = keyPrefix + "New";
        TableRow firstValue = new TableRow(valueTypes);
        firstValue.setColumnAt(0, Integer.MAX_VALUE);
        firstValue.setColumnAt(1, false);
        firstValue.setColumnAt(2, "");
        table.put(firstKey, firstValue);
        Storeable answer = table.get(firstKey);
        assertEquals(answer, firstValue);

        // get overwritten new entry
        Storeable secondValue = new TableRow(firstValue);
        secondValue.setColumnAt(1, true);
        table.put(firstKey, secondValue);
        answer = table.get(firstKey);
        assertEquals(answer, secondValue);

        // get double overwritten entry
        TableRow thirdValue = new TableRow(firstValue);
        table.put(firstKey, thirdValue);
        answer = table.get(firstKey);
        assertEquals(answer, thirdValue);

        // try to get recently deleted entry
        table.remove(firstKey);
        assertEquals(table.size(), 0);
        answer = table.get(firstKey);
        assertNull(answer);
        table.put(firstKey, firstValue);
        answer = table.get(firstKey);
        assertEquals(answer, firstValue);

        // get overwritten old entry
        table.commit();
        table.put(firstKey, secondValue);
        answer = table.get(firstKey);
        assertEquals(answer, secondValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmpty() throws Exception {
        table.get("");
    }

    @Test
    public void testRemove() throws Exception {
        String firstKey = keyPrefix + "1";
        TableRow firstValue = new TableRow(valueTypes);
        firstValue.setColumnAt(0, 1);
        firstValue.setColumnAt(1, false);
        firstValue.setColumnAt(2, "1");

        String secondKey = keyPrefix + "2";
        TableRow secondValue = new TableRow(valueTypes);
        secondValue.setColumnAt(0, 2);
        secondValue.setColumnAt(1, false);
        secondValue.setColumnAt(2, "2");

        String thirdKey = keyPrefix + "3";
        TableRow thirdValue = new TableRow(valueTypes);
        thirdValue.setColumnAt(0, 3);
        thirdValue.setColumnAt(1, false);
        thirdValue.setColumnAt(2, "3");

        String fourthKey = keyPrefix + "4";
        TableRow fourthValue = new TableRow(valueTypes);
        fourthValue.setColumnAt(0, 4);
        fourthValue.setColumnAt(1, true);
        fourthValue.setColumnAt(2, "4");
        table.put(firstKey, firstValue);
        table.put(secondKey, secondValue);
        table.put(thirdKey, thirdValue);
        table.put(fourthKey, fourthValue);
        assertEquals(table.size(), 4);
        Storeable answer = table.remove(firstKey);
        assertEquals(table.size(), 3);
        assertEquals(answer, firstValue);

        answer = table.remove(secondKey);
        assertEquals(table.size(), 2);
        assertEquals(answer, secondValue);

        answer = table.remove(thirdKey);
        assertEquals(table.size(), 1);
        assertEquals(answer, thirdValue);

        answer = table.remove(fourthKey);
        assertEquals(table.size(), 0);
        assertEquals(answer, fourthValue);

        answer = table.remove(firstKey);
        assertEquals(table.size(), 0);
        assertNull(answer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmpty() throws Exception {
        table.remove("");
    }

    @Test
    public void testCommit() throws Exception {
        String firstKey = keyPrefix + "1";
        TableRow firstValue = new TableRow(valueTypes);
        firstValue.setColumnAt(0, 1);
        firstValue.setColumnAt(1, false);
        firstValue.setColumnAt(2, "1");

        String secondKey = keyPrefix + "2";
        TableRow secondValue = new TableRow(valueTypes);
        secondValue.setColumnAt(0, 2);
        secondValue.setColumnAt(1, false);
        secondValue.setColumnAt(2, "2");

        String thirdKey = keyPrefix + "3";
        TableRow thirdValue = new TableRow(valueTypes);
        thirdValue.setColumnAt(0, 3);
        thirdValue.setColumnAt(1, false);
        thirdValue.setColumnAt(2, "3");

        String fourthKey = keyPrefix + "4";
        TableRow fourthValue = new TableRow(valueTypes);
        fourthValue.setColumnAt(0, 4);
        fourthValue.setColumnAt(1, true);
        fourthValue.setColumnAt(2, "4");

        table.put(firstKey, firstValue);
        table.put(secondKey, secondValue);
        table.put(thirdKey, thirdValue);
        table.put(fourthKey, fourthValue);
        int answer = table.commit();
        assertEquals(answer, 4);
        table.remove(fourthKey);
        table.put(firstKey, secondValue);
        table.put(fourthKey, fourthValue);
        answer = table.commit();
        assertEquals(answer, 1);
    }

    @Test
    public void testRollback() throws Exception {
        String firstKey = keyPrefix + "1";
        TableRow firstValue = new TableRow(valueTypes);
        firstValue.setColumnAt(0, 1);
        firstValue.setColumnAt(1, false);
        firstValue.setColumnAt(2, "1");

        String secondKey = keyPrefix + "2";
        TableRow secondValue = new TableRow(valueTypes);
        secondValue.setColumnAt(0, 2);
        secondValue.setColumnAt(1, false);
        secondValue.setColumnAt(2, "2");

        String thirdKey = keyPrefix + "3";
        TableRow thirdValue = new TableRow(valueTypes);
        thirdValue.setColumnAt(0, 3);
        thirdValue.setColumnAt(1, false);
        thirdValue.setColumnAt(2, "3");

        String fourthKey = keyPrefix + "4";
        TableRow fourthValue = new TableRow(valueTypes);
        fourthValue.setColumnAt(0, 4);
        fourthValue.setColumnAt(1, true);
        fourthValue.setColumnAt(2, "4");

        table.put(firstKey, firstValue);
        table.put(secondKey, secondValue);
        table.put(thirdKey, thirdValue);
        table.put(fourthKey, fourthValue);

        int answer = table.rollback();
        assertEquals(answer, 4);
        table.put(firstKey, firstValue);
        table.put(fourthKey, fourthValue);
        table.remove(fourthKey);
        table.put(firstKey, secondValue);
        answer = table.rollback();
        assertEquals(answer, 1);
    }

    @Test
    public void testList() throws Exception {
        String firstKey = keyPrefix + "1";
        TableRow firstValue = new TableRow(valueTypes);
        firstValue.setColumnAt(0, 1);
        firstValue.setColumnAt(1, false);
        firstValue.setColumnAt(2, "1");

        String secondKey = keyPrefix + "2";
        TableRow secondValue = new TableRow(valueTypes);
        secondValue.setColumnAt(0, 2);
        secondValue.setColumnAt(1, false);
        secondValue.setColumnAt(2, "2");

        String thirdKey = keyPrefix + "3";
        TableRow thirdValue = new TableRow(valueTypes);
        thirdValue.setColumnAt(0, 3);
        thirdValue.setColumnAt(1, false);
        thirdValue.setColumnAt(2, "3");

        String fourthKey = keyPrefix + "4";
        TableRow fourthValue = new TableRow(valueTypes);
        fourthValue.setColumnAt(0, 4);
        fourthValue.setColumnAt(1, true);
        fourthValue.setColumnAt(2, "4");

        List<String> actualKeyList = new ArrayList<>();
        table.put(firstKey, firstValue);
        table.put(secondKey, secondValue);
        table.put(thirdKey, thirdValue);
        table.put(fourthKey, fourthValue);
        actualKeyList.add(firstKey);
        actualKeyList.add(secondKey);
        actualKeyList.add(thirdKey);
        actualKeyList.add(fourthKey);
        List<String> tableKeyList = table.list();
        assertEquals(tableKeyList, actualKeyList);
    }

    @Test
    public void getColumnsCount() {
        assertEquals(table.getColumnsCount(), valueTypes.size());
    }

    @Test
    public void getColumnType() {
        for (int typeIndex = 0; typeIndex < valueTypes.size(); ++typeIndex) {
            assertEquals(table.getColumnType(typeIndex), valueTypes.get(typeIndex));
        }
    }

    @Test
    public void getColumnTypes() {
        assertTrue(Objects.deepEquals(table.getColumnTypes(), valueTypes));
    }

    @Test
    public void testStressTest() throws Exception {
        Map<String, TableRow> entryMap = new HashMap<>();
        for (int entryIndex = 0; entryIndex < entryQuantity; ++entryIndex) {
            String key = keyPrefix + entryIndex;
            TableRow value = new TableRow(valueTypes);
            value.setColumnAt(0, entryIndex);
            value.setColumnAt(1, true);
            value.setColumnAt(2, Integer.toString(entryIndex));
            entryMap.put(key, value);
            table.put(key, value);
            assertEquals(table.size(), entryIndex + 1);
            assertEquals(table.getNumberOfUncommittedChanges(), entryIndex + 1);
        }
        table.commit();
        assertEquals(table.getNumberOfUncommittedChanges(), 0);

        int overwrittenQuantity = 0;
        for (int entryIndex = 0; entryIndex < entryQuantity; entryIndex += 2) {
            String key = keyPrefix + entryIndex;
            TableRow oldValue = entryMap.get(key);
            TableRow newValue = new TableRow(oldValue);
            newValue.setColumnAt(0, oldValue.getIntAt(0) + 1);
            newValue.setColumnAt(2, Integer.toString(oldValue.getIntAt(0) + 1));
            Storeable tableOldValue = table.put(key, newValue);
            ++overwrittenQuantity;
            assertEquals(oldValue, tableOldValue);
            assertEquals(table.getNumberOfUncommittedChanges(), overwrittenQuantity);
        }
        table.rollback();
        assertEquals(table.getNumberOfUncommittedChanges(), 0);

        for (int entryIndex = 0; entryIndex < entryQuantity; ++entryIndex) {
            String key = keyPrefix + entryIndex;
            assertEquals(table.get(key), entryMap.get(key));
        }

        Set<String> tableKeySet = new HashSet<>(table.list());
        assertEquals(entryMap.keySet(), tableKeySet);

        int removeQuantity = 0;
        for (int entryIndex = 1; entryIndex < entryQuantity; entryIndex += 2) {
            String key = keyPrefix + entryIndex;
            TableRow oldValue = entryMap.remove(key);
            Storeable tableOldValue = table.remove(key);
            assertEquals(oldValue, tableOldValue);
            ++removeQuantity;
            assertEquals(table.size(), entryMap.size());
            assertEquals(table.getNumberOfUncommittedChanges(), removeQuantity);
        }
        int diffCount = table.rollback();
        assertEquals(diffCount, removeQuantity);
        assertEquals(table.getNumberOfUncommittedChanges(), 0);

        for (Map.Entry<String, TableRow> entry : entryMap.entrySet()) {
            assertEquals(table.remove(entry.getKey()), entry.getValue());
        }
    }
}
