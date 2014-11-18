package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.TableRow;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.TableRowSerializer;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.gen.TableRowGenerator;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.Assert.*;

public class TableRowSerializerTest {
    static Random random = new Random();
    private static TableRowSerializer serializer;
    private static DataBaseTableProvider provider;
    private DataBaseTable table;
    private ArrayList<Class<?>> signature;

    @BeforeClass
    public static void init() {
        try {
            provider = new DataBaseTableProvider("D:\\test\\database3");
        } catch (Exception e) {
            assertTrue(false);
        }
        serializer = new TableRowSerializer();
    }


    public void assertRows(Storeable expected, Storeable actual) {
        ArrayList<Object> expectedList = new ArrayList<>();
        ArrayList<Object> actualList = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            expectedList.add(expected.getColumnAt(i));
            actualList.add(actual.getColumnAt(i));
        }
        assertEquals(expectedList, actualList);
    }

    @Before
    public void generateTable() {
        String tableName = UUID.randomUUID().toString();
        signature = TableRowGenerator.generateSignature();
        try {
            table = (DataBaseTable) provider.createTable(tableName, signature);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @After
    public void releaseTable() {
        provider.removeTable(table.getName());
    }

    @Test
    public void testSerialize() {
        try {
            provider.deserialize(table, "[,,]");
            assertTrue(false);
        } catch (ParseException pe) {
            assertTrue(true);
        }
        try {
            provider.deserialize(table, "[,]");
            assertTrue(false);
        } catch (ParseException pe) {
            assertTrue(true);
        }

        try {
            provider.deserialize(table, "[]");
            assertTrue(false);
        } catch (ParseException pe) {
            assertTrue(true);
        }

        ArrayList<Object> values = TableRowGenerator.generateValues(signature);
        Storeable row = new TableRow(values);
        try {
            assertRows(row, provider.deserialize(table, provider.serialize(table, row)));
        } catch (ParseException pe) {
            assertTrue(false);
        }
    }

    @Test
    public void testGet() {
        ArrayList<Storeable> values = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            values.add(provider.createFor(table, TableRowGenerator.generateValues(signature)));
        }
        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), values.get(i));
        }
        for (int i = 0; i < 100; ++i) {
            assertEquals(values.get(i), table.get(Integer.toString(i)));
        }

        for (int i = 101; i < 200; ++i) {
            assertNull(table.get(Integer.toString(i)));
        }
    }

    @Test
    public void testPut() {
        ArrayList<Storeable> values = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            values.add(provider.createFor(table, TableRowGenerator.generateValues(signature)));
        }
        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), values.get(i));
        }

        for (int i = 0; i < 100; ++i) {
            assertEquals(table.put(Integer.toString(i), TableRowGenerator.generateRow(provider, table, signature)),
                    values.get(i));
        }

        try {
            table.put(null, TableRowGenerator.generateRow(provider, table, signature));
            assertFalse(true);
        } catch (IllegalArgumentException iae) {
            assertTrue(true);
        }

        try {
            table.put("haha", null);
            assertFalse(true);
        } catch (IllegalArgumentException iae) {
            assertTrue(true);
        }
    }

    @Test
    public void testList() {
        ArrayList<String> keyList = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), TableRowGenerator.generateRow(provider, table, signature));
            keyList.add(Integer.toString(i));
        }
        ArrayList<String> tableList = new ArrayList<>(table.list());
        tableList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        keyList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        assertEquals(tableList, keyList);
    }

    @Test
    public void testRemove() {
        ArrayList<Storeable> values = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            values.add(TableRowGenerator.generateRow(provider, table, signature));
        }
        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), values.get(i));
        }

        for (int i = 0; i < 100; ++i) {
            assertEquals(values.get(i),
                    table.remove(Integer.toString(i)));
        }

        for (int i = 0; i < 100; ++i) {
            assertNull(table.remove(Integer.toString(i)));
        }

        try {
            table.remove(null);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {
            assertTrue(true);
        }
    }

    @Test
    public void testSize() {
        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), TableRowGenerator.generateRow(provider, table, signature));
        }

        assertEquals(table.size(), 100);
        for (int i = 50; i < 1000; ++i) {
            table.put(Integer.toString(i), TableRowGenerator.generateRow(provider, table, signature));
        }
        assertEquals(table.size(), 1000);
        try {
            table.commit();
        } catch (IOException ioe) {
            assertTrue(false);
        }
        for (int i = 0; i < 500; ++i) {
            table.remove(Integer.toString(i));
        }

        assertEquals(table.size(), 500);

        table.rollback();

        assertEquals(table.size(), 1000);
    }

    @Test
    public void testCommitRollback() {
        HashMap<String, TableRow> values = new HashMap<>();
        for (int i = 0; i < 100; ++i) {

            values.put(Integer.toString(i), TableRowGenerator.generateRow(provider, table, signature));
            table.put(Integer.toString(i), values.get(Integer.toString(i)));
        }
        assertEquals(table.size(), 100);
        assertEquals(table.rollback(), 100);
        assertEquals(table.size(), 0);

        for (int i = 0; i < 100; ++i) {
            values.put(Integer.toString(i), TableRowGenerator.generateRow(provider, table, signature));
            table.put(Integer.toString(i), values.get(Integer.toString(i)));
        }
        try {
            assertEquals(table.commit(), 100);
        } catch (IOException ioe) {
            assertEquals(table.size(), 100);
        }

        DataBaseTable loadedTable = null;
        try {
            loadedTable = new DataBaseTable(provider.getDataBaseDirectory(), table.getName(),
                    serializer, new ReentrantReadWriteLock(true));
        } catch (Exception e) {
            assertTrue(false);
        }
        ArrayList<String> list = new ArrayList<>(loadedTable.list());
        for (String key : list) {
            assertRows(loadedTable.get(key), values.get(key));
        }

    }

}
