package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

public class DbTableTest {
    private Map<String, String> testKeysValues = new HashMap<>();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private File tableDir;
    private File tempFile;

    private DbTable tm;

    @Before
    public void setUp() throws Exception {
        testKeysValues.put("key", "value");
        testKeysValues.put("mazafakka", "yo");
        testKeysValues.put("12345", "!)@J D! =!_@O  as \"|}");
        tempFile = tempFolder.newFile();
        tableDir = tempFolder.newFolder();
        tm = new DbTable(tableDir);
    }

    @Test
    public void listEmptyTable() {
        List<String> keys = tm.list();
        assertTrue(keys.size() == 0);
    }

    @Test
    public void listAfterPutCorrectness() {
        tm.put("key", testKeysValues.get("key"));
        tm.put("12345", testKeysValues.get("12345"));
        List<String> actual = tm.list();
        Set<String> actualSet = new HashSet<>();
        actualSet.addAll(actual);
        Set<String> expected = new HashSet<>();
        expected.add("key");
        expected.add("12345");
        assertEquals(expected, actualSet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableWithWrongTableFile() {
        new DbTable(tempFile);
    }

    @Test
    public void putCommitAndCheckReturnedValueByCommit() {
        tm.put("key", testKeysValues.get("key"));
        tm.put("mazafakka", testKeysValues.get("mazafakka"));
        tm.put("12345", testKeysValues.get("12345"));
        int actual = tm.commit();
        assertEquals(3, actual);
    }

    @Test
    public void putCommitLoadTableAgainAndCheckValues() {
        tm.put("key", testKeysValues.get("key"));
        tm.put("mazafakka", testKeysValues.get("mazafakka"));
        tm.put("12345", testKeysValues.get("12345"));
        tm.commit();
        DbTable newTm = new DbTable(tableDir);
        List<String> actualList = newTm.list();
        Set<String> actualSet = new HashSet<>();
        actualSet.addAll(actualList);
        Set<String> expectedSet = new HashSet<>();
        expectedSet.add("key");
        expectedSet.add("mazafakka");
        expectedSet.add("12345");
        assertEquals(expectedSet, actualSet);
    }

    @Test
    public void secondCommitMustReturnZero() {
        tm.put("key", testKeysValues.get("key"));
        tm.put("mazafakka", testKeysValues.get("mazafakka"));
        tm.put("12345", testKeysValues.get("12345"));
        tm.commit();
        int actual = tm.commit();
        assertTrue(actual == 0);
    }

    @Test
    public void doublePutWithDifferentValues() {
        tm.put("key", testKeysValues.get("key"));
        tm.put("key", "NEW VALUE");
        assertEquals("NEW VALUE", tm.get("key"));
    }

    @Test
    public void putRemoveAndPutAgainTheSameKey() {
        tm.put("key", testKeysValues.get("key"));
        tm.put("mazafakka", testKeysValues.get("mazafakka"));
        tm.put("12345", testKeysValues.get("12345"));
        tm.remove("mazafakka");
        tm.put("mazafakka", "mazafakka?");
        assertEquals("mazafakka?", tm.get("mazafakka"));
    }

    @Test
    public void fillingEmptyTableAndRollbackMustCleansTable() {
        tm.put("key", testKeysValues.get("key"));
        tm.put("mazafakka", testKeysValues.get("mazafakka"));
        tm.put("12345", testKeysValues.get("12345"));
        tm.rollback();
        List<String> actualList = tm.list();
        assertTrue(actualList.size() == 0);
        assertNull(tm.get("key"));
    }

    @Test
    public void testingSizeInvariantAfterSomeOperations() {
        tm.put("key", testKeysValues.get("key"));
        tm.put("mazafakka", testKeysValues.get("mazafakka"));
        tm.put("12345", testKeysValues.get("12345"));
        assertTrue(tm.size() == 3);
        tm.remove("12345");
        assertTrue(tm.size() == 2);
        tm.commit();
        assertTrue(tm.size() == 2);
        assertTrue(new DbTable(tableDir).size() == 2);
    }

    @Test
    public void sizeMustIncrementAfterPut() {
        assertTrue(tm.size() == 0);
        tm.put("key", testKeysValues.get("key"));
        assertTrue(tm.size() == 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putInvalidKey() {
        tm.put(null, "m??");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInvalidKey() {
        tm.get(null);
    }

    @Test
    public void getUnexistedKey() {
        assertNull(tm.get("asdoi312d9023dj20923jd"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeInvalidKey() {
        tm.remove(null);
    }
}
