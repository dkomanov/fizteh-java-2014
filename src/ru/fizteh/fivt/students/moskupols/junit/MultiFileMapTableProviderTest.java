package ru.fizteh.fivt.students.moskupols.junit;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;

import java.nio.file.Files;

import static org.junit.Assert.*;

public class MultiFileMapTableProviderTest {

    private MultiFileMapTableProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new MultiFileMapTableProvider(Files.createTempDirectory("ProviderTest"));
    }

    @Test
    public void testCreateGet() {
        assertNull(provider.getTable("tab"));

        Table t = provider.createTable("tab");
        assertNotNull(t);

        t.put("0", "a");
        t.put("1", "b");
        t.put("2", "c");
        t.commit();

        Table t2 = provider.getTable("tab");
        assertNotNull(t2);
        assertEquals(t.list(), t2.list());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTrash() throws Exception {
        provider.createTable("asdf/gr");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        provider.getTable(null);
    }

    @Test
    public void testRemove() throws Exception {
        Table t = provider.createTable("tab");
        t.put("0", "a");
        t.commit();

        provider.removeTable("tab");

        assertNull(provider.getTable("tab"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        provider.removeTable(null);
    }
}
