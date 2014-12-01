package ru.fizteh.fivt.students.VasilevKirill.junit.JUnitTests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.junit.MyTableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

public class TableTest {
    private static String path;
    private static TableProvider dataBase;

    @BeforeClass
    public static void beforeClass() {
        try {
            //path = new File("").getCanonicalPath();//
            path = Files.createTempDirectory("database").toString();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        dataBase = new MyTableProviderFactory().create(path);
        dataBase.createTable("First");
        dataBase.createTable("Second");
    }

    @AfterClass
    public static void afterClass() {
        dataBase.removeTable("First");
        dataBase.removeTable("Second");
    }

    @Test
    public void testGetName() throws Exception {
        //String path = new File("").getCanonicalPath();
        Table resultTable = dataBase.getTable("First");
        assertFalse(resultTable == null);
        assertEquals("First", resultTable.getName());
    }

    @Test
    public void testGet() throws Exception {
        Table resultTable = dataBase.getTable("First");
        assertFalse(resultTable == null);
        resultTable.put("1", "value1");
        assertEquals("value1", resultTable.get("1"));
        assertNull(resultTable.get("2"));
        try {
            resultTable.get(null);
            assertEquals(1, 2);
        } catch (IllegalArgumentException e) {
            assertEquals(1, 1);
        }
        resultTable.remove("1");
    }

    @Test
    public void testPut() throws Exception {
        Table resultTable = dataBase.getTable("First");
        assertFalse(resultTable == null);
        try {
            resultTable.put(null, null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testRemove() throws Exception {
        Table resultTable = dataBase.getTable("First");
        assertFalse(resultTable == null);
        resultTable.put("1", "value1");
        assertEquals("value1", resultTable.remove("1"));
        assertEquals(null, resultTable.remove("2"));
        try {
            resultTable.remove(null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSize() throws Exception {
        Table resultTable = dataBase.getTable("First");
        assertFalse(resultTable == null);
        resultTable.put("1", "value1");
        resultTable.put("2", "value2");
        assertEquals(2, resultTable.size());
        resultTable.remove("2");
        assertEquals(1, resultTable.size());
        resultTable.remove("1");
        resultTable.remove("2");
        resultTable.commit();
    }

    @Test
    public void testCommit() throws Exception {
        Table resultTable = dataBase.getTable("First");
        assertFalse(resultTable == null);
        resultTable.put("1", "value1");
        resultTable.put("2", "value2");
        assertEquals(2, resultTable.commit());
        resultTable.remove("1");
        resultTable.remove("2");
        resultTable.commit();
    }

    @Test
    public void testRollback() throws Exception {
        Table resultTable = dataBase.getTable("Second");
        assertFalse(resultTable == null);
        resultTable.put("1", "value1");
        assertEquals(1, resultTable.rollback());
    }

    @Test
    public void testList() throws Exception {
        Table resultTable = dataBase.getTable("First");
        assertFalse(resultTable == null);
        resultTable.put("1", "value1");
        resultTable.put("2", "value2");
        resultTable.commit();
        List<String> keys = resultTable.list();
        assertEquals("1", keys.get(0));
        assertEquals("2", keys.get(1));
    }

}
