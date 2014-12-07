package ru.fizteh.fivt.students.irina_karatsapova.junit.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.MyTableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.Table;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.TableException;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MyTableTest {

    TemporaryFolder tempFolder = new TemporaryFolder();
    Table table;
    String oneMoreTableName = "one-more-table-name";
    String[] goodTableNames = {"table", "name", "123", "db № 4 with spaces", "null", "1.a"};
    String[] badTableNames = {"..", "s//ym//b//ols", "enter/n"};
    TableProvider provider;

    @Before
    public void setUp() throws Exception {
        tempFolder.create();
        File providerDir = tempFolder.newFolder();
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(providerDir.toString());
        table = provider.createTable(oneMoreTableName);
    }

    @After
    public void tearDown() throws Exception {
        tempFolder.delete();
    }

    @Test
    public void testGoodTableGetName() throws Exception {
        for (String tableName : goodTableNames) {
            Table goodTable = provider.createTable(tableName);
            assertEquals(tableName, goodTable.getName());
        }
    }

    @Test
    public void testBadTableGetName() throws Exception {
        int exceptionsNumber = 0;
        for (String tableName : badTableNames) {
            try {
                Table badTable = provider.createTable(tableName);
                badTable.getName();
            } catch (TableException e) {
                exceptionsNumber++;
            }
        }
        assertEquals(badTableNames.length, exceptionsNumber);
    }

    @Test
    public void testGet() throws Exception {
        table.put("1", "q");
        table.put("2", "w");
        assertEquals("q", table.get("1"));
        table.put("1", "e");
        assertEquals("e", table.get("1"));
        assertEquals("w", table.get("2"));
        table.remove("1");
        assertNull(table.get("1"));
        assertNull(table.get("3"));
        table.put("1", "q");
        assertEquals("q", table.get("1"));
        table.rollback();
    }

    @Test
    public void testPut() throws Exception {
        assertNull(table.put("1", "q"));
        assertEquals("q", table.put("1", "w"));
        table.put("2", "q");
        table.remove("2");
        assertNull(table.put("2", "r"));
        assertEquals("w", table.get("1"));
        assertEquals("w", table.put("1", "t"));
        table.rollback();
    }

    @Test
    public void testRemove() throws Exception {
        assertNull(table.remove("1"));
        table.put("1", "q");
        assertEquals("q", table.remove("1"));
        assertNull(table.remove("1"));
        table.put("2", "f");
        table.put("2", "g");
        assertEquals("g", table.remove("2"));
        table.rollback();
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, table.size());
        table.put("1", "q");
        assertEquals(1, table.size());
        table.put("2", "r");
        table.put("3", "q");
        assertEquals(3, table.size());
        table.remove("2");
        assertEquals(2, table.size());
        table.remove("4");
        assertEquals(2, table.size());
        table.rollback();
    }

    @Test
    public void testCommit() throws Exception {
        table.put("1", "q");
        assertEquals(1, table.commit());
        assertEquals(1, table.size());
        table.put("1", "r");
        table.remove("1");
        assertEquals(2, table.commit());
        table.put("1", "g");
        table.put("2", "r");
        table.put("3", "g");
        table.put("3", "d");
        assertEquals(4, table.commit());
        table.get("1");
        table.get("4");
        table.remove("2");
        assertEquals(1, table.commit());

        Table sameTable = provider.getTable(oneMoreTableName);
        assertEquals(2, sameTable.size());
        assertEquals("g", sameTable.get("1"));
        assertEquals("d", sameTable.get("3"));
        assertNull(sameTable.get("2"));
        assertNull(sameTable.get("4"));
    }

    @Test
    public void testRollback() throws Exception {
        table.put("1", "q");
        assertEquals(1, table.rollback());
        assertEquals(0, table.size());
        table.put("1", "r");
        table.remove("1");
        assertEquals(2, table.rollback());
        assertEquals(0, table.size());
        table.put("1", "g");
        table.put("2", "r");
        table.put("3", "g");
        table.put("3", "d");
        assertEquals(4, table.rollback());
        table.get("1");
        table.get("4");
        table.remove("2");
        assertEquals(0, table.rollback());
        assertEquals(0, table.size());
        assertNull(table.get("1"));
        assertNull(table.get("3"));
    }

    @Test
    public void testChangesNumber() throws Exception {
        assertEquals(0, table.changesNumber());
        table.put("1", "q");
        assertEquals(1, table.changesNumber());
        table.commit();
        assertEquals(0, table.changesNumber());
        table.put("1", "r");
        table.remove("1");
        table.put("2", "r");
        assertEquals(3, table.changesNumber());
        table.rollback();
        assertEquals(0, table.changesNumber());
        table.get("2");
        table.get("1");
        table.size();
        assertEquals(0, table.changesNumber());
    }
}
