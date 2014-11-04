package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;

import static org.junit.Assert.*;

public class DataBaseTableProviderTest {
    private DataBaseTableProvider database;

    @Before
    public void initDatabase() {
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        database = factory.create("D:\\test\\test");
    }

    @Test
    public void testCreate() {

        try {
            Table table = database.createTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {
            assertFalse(false);
        }

        Table table = database.createTable("table");
        assertNotNull(table);
        Table table2 = database.createTable("table");
        assertNull(table2);
        database.removeTable("table");

    }

    @Test
    public void testDrop() {
        try {
            database.removeTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {
            assertTrue(true);
        } catch (IllegalStateException ise) {
            assertTrue(false);
        }


        for (int i = 0; i < 50; ++i) {
            database.createTable(Integer.toString(i));
        }

        for (int i = 51; i < 100; ++i) {
            try {
                database.removeTable(Integer.toString(i));
                assertTrue(false);
            } catch (IllegalStateException ise) {
                assertTrue(true);
            }
        }

        for (int i = 0; i < 25; ++i) {
            try {
                database.removeTable(Integer.toString(i));
            } catch (Exception e) {
                assertTrue(false);
            }
        }

        for (int i = 0; i < 25; ++i) {
            Table table = database.createTable(Integer.toString(i));
            assertNotNull(table);
        }

        //Release tables

        for (int i = 0; i < 50; ++i) {
            database.removeTable(Integer.toString(i));
        }
    }



}
