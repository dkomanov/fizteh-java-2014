package ru.fizteh.fivt.students.moskupols.junit;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.moskupols.multifilehashmap.MultiFileMapImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class AdaptedMultiFileMapTableTest {

    KnownDiffTable table;
    private Path dirPath;

    @Before
    public void setUp() throws Exception {
        dirPath = Files.createTempDirectory("MFHashMapTest");
        table = new MultiFileMapTableAdaptor(new MultiFileMapImpl(dirPath));
    }

    void putSome(KnownDiffTable t, int howMuch) {
        for (int i = 0; i < howMuch; i++) {
            t.put(String.valueOf(i), Character.toString((char) ('a' + i)));
        }
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(dirPath.getFileName().toString(), table.getName());
    }

    @Test
    public void testGet() throws Exception {
        assertNull(table.get("0"));

        putSome(table, 20);

        assertNull(table.get("q"));
        assertEquals("a", table.get("0"));
        assertEquals("c", table.get("2"));
    }

    @Test
    public void testPut() throws Exception {
        assertNull(table.put("0", "a"));
        assertNull(table.put("1", "b"));
        assertEquals("b", table.put("1", "b2"));
        assertEquals("b2", table.put("1", "b3"));
    }

    @Test
    public void testRemove() throws Exception {
        assertNull(table.remove("0"));

        putSome(table, 20);

        assertEquals("a", table.remove("0"));
        assertEquals("d", table.remove("3"));

        assertNull(table.get("0"));
        assertNull(table.remove("3"));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, table.size());

        putSome(table, 10);
        assertEquals(10, table.size());

        table.remove("0");
        assertEquals(9, table.size());
        table.remove("1");
        assertEquals(8, table.size());
    }

    @Test
    public void testList() throws Exception {
        assertTrue(table.list().isEmpty());

        putSome(table, 30);

        Set<String> expectedKeys = new HashSet<>(30);
        for (int i = 0; i < 30; i++) {
            expectedKeys.add(String.valueOf(i));
        }
        assertEquals(expectedKeys, new HashSet<>(table.list()));
    }

    @Test
    public void testCommitRollback() throws Exception {
        assertEquals(0, table.diff());

        table.put("-1", ";");
        table.put("1", "!");
        assertEquals(2, table.diff());

        assertEquals(2, table.commit());
        assertEquals(0, table.diff());

        putSome(table, 20);
        assertEquals(20, table.diff());

        KnownDiffTable another = new MultiFileMapTableAdaptor(new MultiFileMapImpl(dirPath));
        assertEquals(";", another.get("-1"));
        assertNull(another.get("0"));
        assertEquals("!", another.get("1"));

        assertEquals(20, table.rollback());
        assertEquals(0, table.diff());

        assertEquals(";", table.get("-1"));
        assertNull(table.get("0"));
        assertEquals("!", table.get("1"));
    }
}
