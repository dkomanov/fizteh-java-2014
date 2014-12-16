package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.*;

public class DbTableTest {
    private Map<String, String> testKeysValues = new HashMap<>();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private File dbDir;
    private File tempFile;

    private List<Class<?>> signature;
    private TableProvider db;
    private Table tm;

    private void addKeysToTable(Table tm) throws ParseException {
        tm.put("key", db.deserialize(tm, testKeysValues.get("key")));
        tm.put("mazafakka", db.deserialize(tm, testKeysValues.get("mazafakka")));
        tm.put("12345", db.deserialize(tm, testKeysValues.get("12345")));
    }

    @Before
    public void setUp() throws Exception {
        testKeysValues.put("key", "[\"value\"]");
        testKeysValues.put("mazafakka", "[\"yo\"]");
        testKeysValues.put("12345", "[\"!)@J D! =!_@O  as |}\"]");
        tempFile = tempFolder.newFile();
        dbDir = tempFolder.newFolder();
        db = new DbTableProvider(dbDir.toPath());
        signature = new LinkedList<>();
        signature.add(String.class);
        tm = db.createTable("table", signature);
    }

    @Test
    public void listEmptyTable() {
        List<String> keys = tm.list();
        assertTrue(keys.size() == 0);
    }

    @Test
    public void listAfterPutCorrectness() throws ParseException {
        addKeysToTable(tm);
        List<String> actual = tm.list();
        Set<String> actualSet = new HashSet<>();
        actualSet.addAll(actual);
        Set<String> expected = new HashSet<>();
        expected.add("key");
        expected.add("12345");
        expected.add("mazafakka");
        assertEquals(expected, actualSet);
    }

    @Test
    public void checkTableColumnTypesCorrectnessAfterCreate() {
        assertTrue(tm.getColumnsCount() == 1);
        assertEquals(tm.getColumnType(0), String.class);
    }

    @Test
    public void putCommitAndCheckReturnedValueByCommit() throws ParseException, IOException {
        addKeysToTable(tm);
        int actual = tm.commit();
        assertEquals(3, actual);
    }

    @Test
    public void putCommitLoadTableAgainAndCheckValues() throws ParseException, IOException {
        addKeysToTable(tm);
        tm.commit();
        Table newTm = new DbTableProvider(dbDir.toPath()).getTable("table");
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
    public void secondCommitMustReturnZero() throws ParseException, IOException {
        addKeysToTable(tm);
        tm.commit();
        int actual = tm.commit();
        assertTrue(actual == 0);
    }

    @Test
    public void doublePutWithDifferentValues() throws ParseException {
        tm.put("key", db.deserialize(tm, testKeysValues.get("key")));
        tm.put("key", db.deserialize(tm, "[\"NEW VALUE\"]"));
        assertEquals("[\"NEW VALUE\"]", db.serialize(tm, tm.get("key")));
    }

    @Test
    public void putRemoveAndPutAgainTheSameKey() throws ParseException {
        addKeysToTable(tm);
        tm.remove("mazafakka");
        tm.put("mazafakka", db.deserialize(tm, "[\"mazafakka?\"]"));
        assertEquals("[\"mazafakka?\"]", db.serialize(tm, tm.get("mazafakka")));
    }

    @Test
    public void fillingEmptyTableAndRollbackMustCleansTable() throws ParseException {
        addKeysToTable(tm);
        tm.rollback();
        List<String> actualList = tm.list();
        assertTrue(actualList.size() == 0);
        assertNull(tm.get("key"));
    }

    @Test
    public void testingSizeInvariantAfterSomeOperations() throws ParseException, IOException {
        addKeysToTable(tm);
        assertTrue(tm.size() == 3);
        tm.remove("12345");
        assertTrue(tm.size() == 2);
        tm.commit();
        assertTrue(tm.size() == 2);
        assertTrue(new DbTableProvider(dbDir.toPath()).getTable("table").size() == 2);
    }

    @Test
    public void sizeMustIncrementAfterPut() throws ParseException {
        assertTrue(tm.size() == 0);
        tm.put("key", db.deserialize(tm, testKeysValues.get("key")));
        assertTrue(tm.size() == 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putInvalidKey() throws ParseException {
        tm.put(null, db.deserialize(tm, "[\"hello\"]"));
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
