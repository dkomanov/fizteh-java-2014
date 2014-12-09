package ru.fizteh.fivt.students.irina_karatsapova.junit.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.MyTableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.Table;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProviderFactory;

import java.io.File;

import static org.junit.Assert.*;

public class MyTableProviderTest {

    TemporaryFolder tempFolder = new TemporaryFolder();
    String tableName = "table";
    TableProvider provider;

    @Before
    public void setUp() throws Exception {
        tempFolder.create();
        File providerDir = tempFolder.newFolder();
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(providerDir.toString());
    }

    @After
    public void tearDown() throws Exception {
        tempFolder.delete();
    }

    @Test
    public void testCreateTable() throws Exception {
        assertNotNull(provider.createTable(tableName));
        assertNull(provider.createTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTable() throws Exception {
        provider.createTable(null);
    }

    @Test
    public void testGetTable() throws Exception {
        assertNull(provider.getTable(tableName));
        Table table = provider.createTable(tableName);
        assertEquals(table, provider.getTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
        provider.getTable(null);
    }

    @Test
    public void testRemoveTable() throws Exception {
        provider.createTable(tableName);
        assertNotNull(provider.getTable(tableName));
        provider.removeTable(tableName);
        assertNull(provider.getTable(tableName));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingTable() throws Exception {
        provider.removeTable(tableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullTable() throws Exception {
        provider.removeTable(null);
    }
}
