package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.fileshell.RemoveCommand;

import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.Assert.*;


public class DataBaseTableTest {

    private static DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
    private static DataBaseTableProvider database;

    @Before
    public void initDataBase() {
        database = factory.create("D:\\test\\test");
    }

    @Test
    public void testGet() {
        database.createTable("table");
        Table table1 = database.getTable("table");
        for (int i = 0; i < 100; ++i) {
            table1.put(Integer.toString(i), Integer.toString(i));
        }
        for (int i = 0; i < 100; ++i) {
            assertEquals(Integer.toString(i), table1.get(Integer.toString(i)));
        }

        for (int i = 101; i < 200; ++i) {
            assertNull(table1.get(Integer.toString(i)));
        }
    }

    @Test
    public void testPut() {
        database.createTable("table");
        Table table2 = database.getTable("table");
        for (int i = 0; i < 100; ++i) {
            assertEquals(table2.put(Integer.toString(i), Integer.toString(i)), Integer.toString(i));
        }

        for (int i = 0; i < 100; ++i) {
            assertEquals(table2.put(Integer.toString(i), Integer.toString(i + 200)), Integer.toString(i));
        }

        try {
            table2.put(null, "haha");
            assertFalse(true);
        } catch (IllegalArgumentException iae) {
            assertTrue(true);
        }

        try {
            table2.put("haha", null);
            assertFalse(true);
        } catch (IllegalArgumentException iae) {
            assertTrue(true);
        }
    }

    @Test
    public void testList() {
        database.createTable("table");
        Table table = database.getTable("table");
        ArrayList<String> keyList = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), Integer.toString(i));
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

    @After
    public void releaseTables() {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("-r");
        arguments.add("table");
        try {
            new RemoveCommand(database.getDataBaseDirectory()).executeCommand(arguments);
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
