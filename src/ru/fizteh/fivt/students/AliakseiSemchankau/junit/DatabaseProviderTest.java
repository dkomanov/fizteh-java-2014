package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.*;

import java.nio.file.Files;

import static org.junit.Assert.*;

public class DatabaseProviderTest {

    private DatabaseFactory dFactory;
    private DatabaseProvider dProvider;

    TemporaryFolder folder;
    String folderName;

    @Before
    public void initialization() {

        folder = new TemporaryFolder();
        folderName = folder.toString();
        dFactory = new DatabaseFactory();
        dProvider = dFactory.create(folderName);

       /* dFactory = new DatabaseFactory();
        dProvider = dFactory.create("C:\\JavaTests\\newTestingDatabase");*/
    }

   @Test
    public void testGetTable() {
        folder = new TemporaryFolder();
        folderName = folder.toString();
        dFactory = new DatabaseFactory();
        dProvider = dFactory.create(folderName);
        try {
            Table dTable = dProvider.getTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertTrue(true);
        }

        try {
            Table dTable = dProvider.getTable("nonexistingnTable");
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertTrue(true);
        }

    }

    @Test
    public void testTableCreate() {

        try {
            Table dTable = dProvider.createTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertFalse(false);
        }

        Table table1 = dProvider.createTable("testTable");
        assertNotNull(table1);
        Table table2 = dProvider.createTable("testTable");
        assertNull(table2);
        dProvider.removeTable("testTable");

    }

    @Test
    public void testTableDrop() {

        try {
            dProvider.removeTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertTrue(true);
        }


        for (int i = 0; i < 50; ++i) {
            try {
                String newTableName = Integer.toString(i);
                dProvider.removeTable(newTableName);
                assertTrue(false);
            } catch (IllegalStateException isexc) {
                assertTrue(true);
            }
        }

        for (int i = 0; i < 50; ++i) {
            String newTableName = Integer.toString(i);
            dProvider.createTable(newTableName);
        }

        for (int i = 50; i < 100; ++i) {
            try {
                String newTableName = Integer.toString(i);
                dProvider.removeTable(newTableName);
                assertTrue(false);
            } catch (IllegalStateException isexc) {
                assertTrue(true);
            }
        }

        for (int i = 0; i < 25; ++i) {
            String newTableName = Integer.toString(i);
            try {
                dProvider.removeTable(newTableName);
            } catch (Exception exc) {
                assertTrue(false);
            }
        }

        for (int i = 0; i < 25; ++i) {
            String newTableName = Integer.toString(i);
            Table dTable = dProvider.createTable(newTableName);
            assertNotNull(dTable);
        }

        for (int i = 0; i < 50; ++i) {
            String newTableName = Integer.toString(i);
            dProvider.removeTable(newTableName);
        }

    }

    @Test
    public void testReadWrite() {

        for (int i = 0; i < 10; ++i) {
            dProvider.createTable(Integer.toString(i));
            Table dTable = dProvider.getTable(Integer.toString(i));
            for (int j = 0; j < 100; ++j) {
                dTable.put(Integer.toString(j), Integer.toString(j + 100));
            }
            dTable.commit();
        }

        DatabaseProvider newdProvider = dFactory.create(folderName);

        for (int i = 0; i < 10; ++i) {
            Table dTable = newdProvider.getTable(Integer.toString(i));
            assertNotNull(dTable);
            for (int j = 0; j < 100; ++j) {
                assertEquals(dTable.get(Integer.toString(j)), Integer.toString(j + 100));
            }
            newdProvider.removeTable(Integer.toString(i));
        }

    }

}
