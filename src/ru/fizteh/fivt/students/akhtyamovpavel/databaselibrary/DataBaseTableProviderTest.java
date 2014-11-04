package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DataBaseTableProviderTest {
    private DataBaseTableProvider database;
    private DataBaseTableProviderFactory factory;

    @Before
    public void initDatabase() {
        factory = new DataBaseTableProviderFactory();
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

    @Test
    public void testCreatingDatabase() {
        try {
            Files.createFile(Paths.get("D:\\test\\lol.dir"));
        } catch (IOException ioe) {
            assertTrue(false);
        }
        try {
            DataBaseTableProvider database1 = factory.create("D:\\test\\lol.dir", true);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            Files.delete(Paths.get("D:\\test\\lol.dir"));
        } catch (IOException ioe) {
            assertTrue(false);
        }
    }

    @Test
    public void testShowTables() {
        Table notExistsTable = database.getTable("notExists");
        assertNull(notExistsTable);

        try {
            database.getTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {
            assertFalse(false);
        }

        for (int i = 0; i < 100; ++i) {
            Table table = database.createTable(Integer.toString(i));
        }

        for (int i = 0; i < 100; ++i) {
            Table table = database.getTable(Integer.toString(i));
            for (int j = 0; j < 50; ++j) {
                table.put(Integer.toString(j), "значение");
            }
            try {
                if (i < 99) {
                    table = database.getTable(Integer.toString(i + 1));
                    assertTrue(false);
                }
            } catch (IllegalArgumentException iae) {
                assertTrue(true);
            }
            table.commit();
            try {
                if (i < 99) {
                    table = database.getTable(Integer.toString(i + 1));
                }
            } catch (IllegalArgumentException iae) {
                assertTrue(false);
            }
        }

        for (int i = 0; i < 100; ++i) {
            database.removeTable(Integer.toString(i));
        }
    }

    @Test
    public void openDatabase() {
        for (int i = 0; i < 100; ++i) {
            database.createTable(Integer.toString(i));
            Table table = database.getTable(Integer.toString(i));
            for (int j = 0; j < 50; ++j) {
                table.put(Integer.toString(i), "value");
            }
            table.commit();
        }

        try {
            DataBaseTableProvider normalDatabase = factory.create("D:\\test\\test", true);
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            Files.createFile(Paths.get("D:\\test\\test\\lol.dir"));
            DataBaseTableProvider brokenDatabase = new DataBaseTableProvider("D:\\test\\test", true);
        } catch (IOException ioe) {
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        } finally {
            try {
                Files.delete(Paths.get("D:\\test\\test\\lol.dir"));
            } catch (IOException ioe) {
                assertTrue(false);
            }
        }

        try {
            Files.createDirectory(Paths.get("D:\\test\\test\\1\\16.dir"));
            DataBaseTableProvider brokenDatabase = new DataBaseTableProvider("D:\\test\\test", true);
        } catch (IOException ioe) {
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        } finally {
            try {
                Files.delete(Paths.get("D:\\test\\test\\1\\16.dir"));
            } catch (IOException ioe) {
                assertTrue(false);
            }
        }

        for (int i = 0; i < 100; ++i) {
            database.removeTable(Integer.toString(i));
        }


    }


}
