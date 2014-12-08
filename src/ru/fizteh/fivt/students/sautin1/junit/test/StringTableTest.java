package ru.fizteh.fivt.students.sautin1.junit.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sautin1.junit.StringTableProviderFactory;
import ru.fizteh.fivt.students.sautin1.junit.filemap.StringTable;
import ru.fizteh.fivt.students.sautin1.junit.filemap.StringTableProvider;
import ru.fizteh.fivt.students.sautin1.junit.shell.FileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class StringTableTest {
    Path testDir;
    StringTableProviderFactory factory;
    StringTableProvider provider;
    String tableName;
    String existingDirectoryName;
    int entryQuantity;
    String keyPrefix;
    String valuePrefix;
    StringTable table;

    @Before
    public void setUp() throws Exception {
        testDir = Paths.get("").resolve("test");
        factory = new StringTableProviderFactory();
        provider = factory.create(testDir.toString());
        tableName = "testTable";
        existingDirectoryName = "existingDir";
        keyPrefix = "key";
        valuePrefix = "value";
        entryQuantity = 100;

        table = provider.createTable(tableName);
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
        String firstValue = valuePrefix + "New";
        String answer = table.put(firstKey, firstValue);
        assertNull(answer);

        // overwrite recently added entries
        String secondValue = valuePrefix + "Overwritten";
        answer = table.put(firstKey, secondValue);
        assertEquals(answer, firstValue);

        // overwrite already overwritten entry
        String thirdValue = valuePrefix + "DoubleOverwritten";
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
        table.put("", "value");
    }

    @Test
    public void testGet() throws Exception {
        // get newly added entry
        String firstKey = keyPrefix + "New";
        String firstValue = valuePrefix + "New";
        table.put(firstKey, firstValue);
        String answer = table.get(firstKey);
        assertEquals(answer, firstValue);

        // get overwritten new entry
        String secondValue = valuePrefix + "Overwritten";
        table.put(firstKey, secondValue);
        answer = table.get(firstKey);
        assertEquals(answer, secondValue);

        // get double overwritten entry
        String thirdValue = valuePrefix + "DoubleOverwritten";
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
        String firstValue = valuePrefix + "1";
        String secondKey = keyPrefix + "2";
        String secondValue = valuePrefix + "2";
        String thirdKey = keyPrefix + "3";
        String thirdValue = valuePrefix + "3";
        String fourthKey = keyPrefix + "4";
        String fourthValue = valuePrefix + "4";
        table.put(firstKey, firstValue);
        table.put(secondKey, secondValue);
        table.put(thirdKey, thirdValue);
        table.put(fourthKey, fourthValue);
        assertEquals(table.size(), 4);
        String answer = table.remove(firstKey);
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
        String firstValue = valuePrefix + "1";
        String secondKey = keyPrefix + "2";
        String secondValue = valuePrefix + "2";
        String thirdKey = keyPrefix + "3";
        String thirdValue = valuePrefix + "3";
        String fourthKey = keyPrefix + "4";
        String fourthValue = valuePrefix + "4";
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
        String firstValue = valuePrefix + "1";
        String secondKey = keyPrefix + "2";
        String secondValue = valuePrefix + "2";
        String thirdKey = keyPrefix + "3";
        String thirdValue = valuePrefix + "3";
        String fourthKey = keyPrefix + "4";
        String fourthValue = valuePrefix + "4";
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
        String firstValue = valuePrefix + "1";
        String secondKey = keyPrefix + "2";
        String secondValue = valuePrefix + "2";
        String thirdKey = keyPrefix + "3";
        String thirdValue = valuePrefix + "3";
        String fourthKey = keyPrefix + "4";
        String fourthValue = valuePrefix + "4";
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
    public void testStressTest() throws Exception {
        Map<String, String> entryMap = new HashMap<>();
        for (int entryIndex = 0; entryIndex < entryQuantity; ++entryIndex) {
            String key = keyPrefix + entryIndex;
            String value = valuePrefix + entryIndex;
            entryMap.put(key, value);
            table.put(key, value);
            assertEquals(table.size(), entryIndex + 1);
            assertEquals(table.diffCount(), entryIndex + 1);
        }
        table.commit();
        assertEquals(table.diffCount(), 0);

        int overwrittenQuantity = 0;
        for (int entryIndex = 0; entryIndex < entryQuantity; entryIndex += 2) {
            String key = keyPrefix + entryIndex;
            String oldValue = entryMap.get(key);
            String newValue = oldValue + entryIndex + "new";
            String tableOldValue = table.put(key, newValue);
            ++overwrittenQuantity;
            assertEquals(oldValue, tableOldValue);
            assertEquals(table.diffCount(), overwrittenQuantity);
        }
        table.rollback();
        assertEquals(table.diffCount(), 0);

        for (int entryIndex = 0; entryIndex < entryQuantity; ++entryIndex) {
            String key = keyPrefix + entryIndex;
            assertEquals(table.get(key), entryMap.get(key));
        }

        Set<String> tableKeySet = new HashSet<>(table.list());
        assertEquals(entryMap.keySet(), tableKeySet);

        int removeQuantity = 0;
        for (int entryIndex = 1; entryIndex < entryQuantity; entryIndex += 2) {
            String key = keyPrefix + entryIndex;
            String oldValue = entryMap.remove(key);
            String tableOldValue = table.remove(key);
            assertEquals(oldValue, tableOldValue);
            ++removeQuantity;
            assertEquals(table.size(), entryMap.size());
            assertEquals(table.diffCount(), removeQuantity);
        }
        int diffCount = table.rollback();
        assertEquals(diffCount, removeQuantity);
        assertEquals(table.diffCount(), 0);

        for (Map.Entry<String, String> entry : entryMap.entrySet()) {
            assertEquals(table.remove(entry.getKey()), entry.getValue());
        }
    }
}
