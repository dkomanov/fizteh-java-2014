package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.DBTableProviderFactory;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class StoreableTest extends TestBase {
    private static final String TABLE_NAME = "table";
    private static TableProviderFactory factory;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private TableProvider provider;
    private Table table;
    private Storeable storeable;

    @BeforeClass
    public static void globalPrepare() {
        factory = new DBTableProviderFactory();
    }

    @Before
    public void prepare() throws IOException {
        provider = factory.create(DB_ROOT.toString());
        table = provider.createTable(
                TABLE_NAME, Arrays.asList(
                        String.class,
                        Integer.class,
                        Double.class,
                        Float.class,
                        Boolean.class,
                        Byte.class,
                        Long.class));
        storeable = provider.createFor(table);
    }

    @After
    public void cleanup() throws IOException {
        cleanDBRoot();
        provider = null;
        table = null;
    }

    @Test
    public void testPutStringToInt() {
        exception.expect(ColumnFormatException.class);
        exception.expectMessage(
                wrongTypeMatcherAndAllOf(
                        containsString("Expected instance of " + INT_TYPE),
                        containsString("got " + STRING_TYPE)));

        storeable.setColumnAt(1, "Hello");
    }

    @Test
    public void testPutDoubleToFloat() {
        exception.expect(ColumnFormatException.class);
        exception.expectMessage(
                wrongTypeMatcherAndAllOf(
                        containsString("Expected instance of " + FLOAT_TYPE),
                        containsString("got " + DOUBLE_TYPE)));

        storeable.setColumnAt(3, 1.2);
    }

    @Test
    public void getLongInsteadOfBoolean() {
        exception.expect(ColumnFormatException.class);
        exception.expectMessage(
                wrongTypeMatcherAndAllOf(
                        containsString("Expected instance of " + BOOLEAN_TYPE),
                        containsString("got " + LONG_TYPE)));

        storeable.getLongAt(4);
    }

    @Test
    public void testGetOnLargeIndex() {
        int index = table.getColumnsCount();

        exception.expect(IndexOutOfBoundsException.class);
        exception.expectMessage(
                "Column index expected to be from zero to {columns.length - 1}, but got: " + index);

        storeable.getColumnAt(index);
    }

    @Test
    public void testGetOnNegativeIndex() {
        int index = -1;

        exception.expect(IndexOutOfBoundsException.class);
        exception.expectMessage(
                "Column index expected to be from zero to {columns.length - 1}, but got: " + index);

        storeable.getColumnAt(index);
    }

    @Test
    public void testPutAndGet() {
        storeable.setColumnAt(0, "Word");
        storeable.setColumnAt(1, 200);
        storeable.setColumnAt(2, -0.5);
        storeable.setColumnAt(3, 3.14f);
        storeable.setColumnAt(4, false);
        storeable.setColumnAt(5, (byte) 12);
        storeable.setColumnAt(6, 1L << 48);

        assertEquals("Word", storeable.getStringAt(0));
        assertEquals(200, (int) storeable.getIntAt(1));
        assertEquals(-0.5, storeable.getDoubleAt(2), 0.0);
        assertEquals(3.14f, storeable.getFloatAt(3), 0.0f);
        assertEquals(false, storeable.getBooleanAt(4));
        assertEquals((byte) 12, (byte) storeable.getByteAt(5));
        assertEquals(1L << 48, (long) storeable.getLongAt(6));
    }

    @Test
    public void testPutAndGetNulls() {
        storeable.setColumnAt(0, null);
        storeable.setColumnAt(1, null);
        storeable.setColumnAt(2, null);
        storeable.setColumnAt(3, null);
        storeable.setColumnAt(4, null);
        storeable.setColumnAt(5, null);
        storeable.setColumnAt(6, null);

        assertNull(storeable.getStringAt(0));
        assertNull(storeable.getIntAt(1));
        assertNull(storeable.getDoubleAt(2));
        assertNull(storeable.getFloatAt(3));
        assertNull(storeable.getBooleanAt(4));
        assertNull(storeable.getByteAt(5));
        assertNull(storeable.getLongAt(6));

        assertNull(storeable.getColumnAt(0));
    }
}
