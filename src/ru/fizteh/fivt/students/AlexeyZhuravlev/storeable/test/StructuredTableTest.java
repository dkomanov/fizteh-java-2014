package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTableProviderFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StructuredTableTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public Table table;
    public TableProvider provider;
    public String dbDirPath;
    List<Class<?>> types;

    @Before
    public void initTable() throws IOException {
        TableProviderFactory factory = new StructuredTableProviderFactory();
        dbDirPath = tmpFolder.newFolder().getAbsolutePath();
        provider = factory.create(dbDirPath);
        Class<?>[] arrayTypes = {Integer.class, Long.class, Float.class, Double.class,
                                 Boolean.class, String.class, Byte.class};
        types = Arrays.asList(arrayTypes);
        table = provider.createTable("table", types);
    }

    @Test
    public void testGetName() {
        assertEquals("table", table.getName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void putNull() {
        table.put(null, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void getNull() {
        table.get(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void removeNull() {
        table.remove(null);
    }

    @Test
    public void testPutAndGet() {
        Object[] values = {5, 6L, 5.2f, 5.4, true, null, (Byte) (byte) 3};
        Storeable value = provider.createFor(table, Arrays.asList(values));
        assertNull(table.put("key", value));
        Storeable ret = table.get("key");
        assertEquals(ret.getByteAt(6), (Byte) (byte) 3);
        assertEquals(ret.getStringAt(5), null);
        assertEquals(ret.getColumnAt(1), 6L);
        value.setColumnAt(0, 2);
        Storeable old = table.put("key", value);
        assertEquals(old.getColumnAt(0), 5);
        assertEquals(table.get("key").getColumnAt(0), 2);
    }

    @Test
    public void testPutAndRemove() {
        Object[] values = {5, 6L, 5.2f, 5.4, true, null, (Byte) (byte) 3};
        Storeable value = provider.createFor(table, Arrays.asList(values));
        assertNull(table.put("key", value));
        Storeable old = table.remove("key");
        assertEquals(old.getColumnAt(0), 5);
        assertEquals(old.getColumnAt(1), 6L);
        assertEquals(old.getColumnAt(6), (byte) 3);
        assertNull(table.remove("key"));
    }

    @Test (expected = ColumnFormatException.class)
    public void testIncorrectPut() throws IOException {
        types.set(1, Boolean.class);
        Object[] values = {false, 6L, 5.2f, 5.4, true, null, (Byte) (byte) 3};
        Table anotherTable = provider.createTable("anotherTable", types);
        Storeable value = provider.createFor(anotherTable, Arrays.asList(values));
        table.put("key", value);
    }
}
