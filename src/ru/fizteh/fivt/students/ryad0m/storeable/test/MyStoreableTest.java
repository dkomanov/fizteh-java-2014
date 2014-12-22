package ru.fizteh.fivt.students.ryad0m.storeable.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.ryad0m.storeable.MyTableProviderFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MyStoreableTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public Table table;
    Storeable storeable;
    List<Class<?>> types;

    @Before
    public void initTable() throws IOException {
        tmpFolder.create();
        TableProviderFactory factory = new MyTableProviderFactory();
        String dbDirPath = tmpFolder.newFolder("db").getAbsolutePath();
        TableProvider provider = factory.create(dbDirPath);
        Class<?>[] arrayTypes = {Integer.class, Long.class, Float.class, Double.class,
                Boolean.class, String.class, Byte.class};
        types = Arrays.asList(arrayTypes);
        table = provider.createTable("table", types);
        storeable = provider.createFor(table);
    }

    @After
    public void after() throws IOException {
        tmpFolder.delete();
    }

    @Test
    public void testSetAndGetColumns() {
        storeable.setColumnAt(0, 3);
        storeable.setColumnAt(1, 3L);
        storeable.setColumnAt(2, 3.2f);
        storeable.setColumnAt(3, 5.4);
        storeable.setColumnAt(4, true);
        storeable.setColumnAt(5, "hello");
        storeable.setColumnAt(6, (byte) 1);
        assertEquals(storeable.getIntAt(0), (Integer) 3);
        assertEquals(storeable.getLongAt(1), (Long) 3L);
        assertEquals(storeable.getFloatAt(2), (Float) 3.2f);
        assertEquals(storeable.getDoubleAt(3), (Double) 5.4);
        assertEquals((boolean) storeable.getBooleanAt(4), true);
        assertEquals(storeable.getStringAt(5), "hello");
        assertEquals(storeable.getByteAt(6), (Byte) (byte) 1);
        assertEquals(storeable.getColumnAt(4), true);
        storeable.setColumnAt(3, null);
        assertNull(storeable.getColumnAt(3));
        assertNull(storeable.getDoubleAt(3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOutOfBounds() {
        storeable.getColumnAt(7);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetIncorrectType() {
        storeable.getLongAt(0);
    }
}
