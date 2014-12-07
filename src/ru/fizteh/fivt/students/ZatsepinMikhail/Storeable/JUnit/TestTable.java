package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.JUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.MultiFileHashMap.MFileHashMapFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestTable {
    String key;
    Storeable value;
    Storeable newValue;
    String providerDirectory;
    String tableName;
    String newTableName;

    Table testTable;
    Table newTestTable;
    TableProvider provider;
    TableProviderFactory factory;

    static List<Class<?>> typeList;
    static List<Class<?>> newTypeList;


    @BeforeClass
    public static void setUpBeforeClass() {
        typeList = new ArrayList<>();
        typeList.add(Integer.class);
        typeList.add(String.class);
        typeList.add(Boolean.class);

        newTypeList = new ArrayList<>();
        newTypeList.add(Double.class);
        newTypeList.add(Byte.class);
        newTypeList.add(Float.class);
    }

    @Before
    public void setUp() {
        key = "key";
        providerDirectory = Paths.get("").resolve("provider").toString();
        tableName = "testTable";
        newTableName = "newTestTable";
        factory = new MFileHashMapFactory();
        try {
            provider = factory.create(providerDirectory);
            testTable = provider.createTable(tableName, typeList);
            newTestTable = provider.createTable(newTableName, newTypeList);
        } catch (IOException e) {
            //suppress
        }
        value = provider.createFor(testTable);
        newValue = provider.createFor(testTable);

        value.setColumnAt(0, 100);
        value.setColumnAt(1, "qwerty");
        value.setColumnAt(2, true);

        newValue.setColumnAt(0, 585);
        newValue.setColumnAt(1, "newQwerty");
        newValue.setColumnAt(2, false);
    }

    @After
    public void tearDown() {
        try {
            provider.removeTable(tableName);
            provider.removeTable(newTableName);
        } catch (IOException e) {
            //suppress
        }
    }

    @Test
    public void testGetName() throws Exception {
        assertTrue(testTable.getName().equals(tableName));
    }

    @Test
    public void testGet() throws Exception {
        assertNull(testTable.get(key));
        testTable.put(key, value);
        assertTrue(testTable.get(key).equals(value));
        testTable.put(key, newValue);
        assertTrue(testTable.get(key).equals(newValue));
        testTable.remove(key);
        assertNull(testTable.get(key));
        testTable.rollback();

        testTable.put(key, value);
        testTable.commit();
        testTable.put(key, newValue);
        assertTrue(testTable.get(key).equals(newValue));
        testTable.remove(key);
        assertNull(testTable.get(key));
    }

    @Test
    public void testRemove() throws Exception {
        assertNull(testTable.remove(key));
        testTable.put(key, value);
        assertTrue(testTable.remove(key).equals(value));

        testTable.rollback();
        testTable.put(key, value);
        assertTrue(testTable.remove(key).equals(value));
        testTable.put(key, value);
        testTable.commit();
        assertTrue(testTable.remove(key).equals(value));
        assertNull(testTable.remove(key));
    }

    @Test
    public void testPutCorrectValue() throws Exception {
        assertNull(testTable.put(key, value));
        assertTrue(testTable.put(key, value).equals(value));
        assertTrue(testTable.get(key).equals(value));
        assertTrue(testTable.put(key, newValue).equals(value));
        assertTrue(testTable.get(key).equals(newValue));
        testTable.remove(key);
        assertNull(testTable.put(key, value));

        String keyForCommit = "keyCM";
        Storeable valueForCommit = ((FileMap) testTable).getTableProvider().createFor(testTable);
        valueForCommit.setColumnAt(0, 999);
        valueForCommit.setColumnAt(1, "commit-value");
        valueForCommit.setColumnAt(2, false);

        int size = 5;
        for (int i = 0; i < size; ++i) {
            valueForCommit.setColumnAt(0, 999 + i);
            assertNull(testTable.put(keyForCommit + i, valueForCommit));
        }
        testTable.commit();
        for (int i = 0; i < size; ++i) {
            valueForCommit.setColumnAt(0, 999 + i);
            assertTrue(testTable.get(keyForCommit + i).equals(valueForCommit));
        }

        Storeable freshValue = ((FileMap) testTable).getTableProvider().createFor(testTable);
        freshValue.setColumnAt(0, 123456);
        freshValue.setColumnAt(1, "FRESH!!!");
        freshValue.setColumnAt(2, true);

        testTable.rollback();
        testTable.remove(keyForCommit + 1);

        valueForCommit.setColumnAt(0, 1000);
        assertNull(testTable.put(keyForCommit + 1, valueForCommit));
        valueForCommit.setColumnAt(0, 1001);
        assertTrue(testTable.put(keyForCommit + 2, freshValue).equals(valueForCommit));

        Storeable changedFreshValue = freshValue;
        changedFreshValue.setColumnAt(1, "CHANGED");
        assertTrue(testTable.put(keyForCommit + 2, changedFreshValue).equals(freshValue));
    }

    @Test
    public void testSize() throws Exception {
        int size = 5;
        for (int i = 0; i < size; ++i) {
            value.setColumnAt(0, i);
            testTable.put(key + i, value);
        }
        assertTrue(testTable.size() == size);
        testTable.remove(key + 0);
        assertTrue(testTable.size() == size - 1);
    }

    @Test
    public void testRollback() throws Exception {
        int size = 5;
        for (int i = 0; i < size; ++i) {
            value.setColumnAt(0, i);
            testTable.put(key + i, value);
        }
        testTable.remove(key + 0);
        testTable.remove(key + 2);
        testTable.put(key + 1, newValue);
        assertTrue(testTable.rollback() == size - 2);
        testTable.put(key, value);
        assertTrue(testTable.rollback() == 1);
        testTable.put(key, value);
        testTable.commit();
        assertTrue(testTable.rollback() == 0);
    }

    @Test
    public void testCommit() throws Exception {
        int size = 5;
        for (int i = 0; i < size; ++i) {
            value.setColumnAt(0, i);
            testTable.put(key + i, value);
        }
        testTable.remove(key + 0);
        testTable.remove(key + 2);
        testTable.put(key + 1, newValue);
        assertTrue(testTable.commit() == size - 2);
        assertTrue(testTable.rollback() == 0);
        testTable.put(key, newValue);
        testTable.remove(key);
        assertTrue(testTable.commit() == 0);
        newValue.setColumnAt(0, 111111);
        testTable.put(key + 1, newValue);
        assertTrue(testTable.commit() == 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullInput() {
        testTable.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullInput() {
        testTable.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullInput() {
        testTable.remove(null);
    }

    @Test(expected = ColumnFormatException.class)
    public void testPutIllegalColumn() {
        Storeable newValueForCheck = provider.createFor(newTestTable);
        newValueForCheck.setColumnAt(0, 4.4);
        newValueForCheck.setColumnAt(1, (byte) 0);
        newValueForCheck.setColumnAt(2, (float) 8.8);
        testTable.put("simpleKey", newValueForCheck);
    }

    @Test
    public void testGetColumnsCount() {
        assertTrue(testTable.getColumnsCount() == 3);
    }

    @Test
    public void testGetColumnType() {
        assertTrue(testTable.getColumnType(0).equals(Integer.class));
        boolean exceptionWas = false;
        try {
            testTable.getColumnType(9);
        } catch (IndexOutOfBoundsException e) {
            exceptionWas = true;
        }
        assertTrue(exceptionWas);
    }
}
