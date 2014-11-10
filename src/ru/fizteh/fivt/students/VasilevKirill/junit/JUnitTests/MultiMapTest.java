package ru.fizteh.fivt.students.VasilevKirill.junit.JUnitTests;

import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.junit.MyTableProviderFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class MultiMapTest {
    private static String path;
    private static TableProvider dataBase;

    static {
        try {
            path = new File("").getCanonicalPath();
            dataBase = new MyTableProviderFactory().create(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void testCreateTable() throws Exception {
        dataBase.createTable("First");
        assertEquals("First", dataBase.getTable("First").getName());
        try {
            dataBase.createTable(null);
            assertFalse(true);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        dataBase.removeTable("First");
    }

    @Test
    public void testGetTable() throws Exception {
        dataBase.createTable("First");
        try {
            dataBase.getTable(null);
            assertFalse(true);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        dataBase.removeTable("First");
    }

    @Test
    public void testRemoveTable() throws Exception {
        dataBase.createTable("First");
        dataBase.removeTable("First");
        assertNull(dataBase.getTable("First"));
        try {
            dataBase.removeTable(null);
            assertFalse(true);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        try {
            dataBase.removeTable("First");
            assertFalse(true);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }
}
