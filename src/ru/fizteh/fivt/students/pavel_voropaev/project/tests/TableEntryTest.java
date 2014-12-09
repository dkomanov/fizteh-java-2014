package ru.fizteh.fivt.students.pavel_voropaev.project.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.pavel_voropaev.project.database.TableEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TableEntryTest {
    private static final int INTEGER = 42;
    private static final long LONG = 1L << 42;
    private static final byte BYTE = (byte) 42;
    private static final float FLOAT = -0.42f;
    private static final double DOUBLE = 0.42;
    private static final boolean BOOLEAN = true;
    private static final String STRING = "42";
    private Storeable storeable;

    @Before
    public void setUp() throws IOException {
        storeable = new TableEntry(
                Arrays.asList(Integer.class, Long.class, Byte.class, Float.class, Double.class, Boolean.class,
                        String.class));
    }

    @Test
    public void getNulls() {
        storeable.setColumnAt(0, null);
        storeable.setColumnAt(1, null);
        storeable.setColumnAt(2, null);
        storeable.setColumnAt(3, null);
        storeable.setColumnAt(4, null);
        storeable.setColumnAt(5, null);
        storeable.setColumnAt(6, null);

        assertNull(storeable.getColumnAt(0));
        assertNull(storeable.getIntAt(0));
        assertNull(storeable.getLongAt(1));
        assertNull(storeable.getByteAt(2));
        assertNull(storeable.getFloatAt(3));
        assertNull(storeable.getDoubleAt(4));
        assertNull(storeable.getBooleanAt(5));
        assertNull(storeable.getStringAt(6));
    }

    @Test
    public void getIntegerAsObject() {
        storeable.setColumnAt(0, INTEGER);
        assertEquals(INTEGER, storeable.getColumnAt(0));
    }

    @Test
    public void getInteger() {
        storeable.setColumnAt(0, INTEGER);
        assertEquals(INTEGER, (Object) storeable.getIntAt(0));
    }

    @Test
    public void getLong() {
        storeable.setColumnAt(1, LONG);
        assertEquals(LONG, (long) storeable.getLongAt(1));
    }

    @Test
    public void getByte() {
        storeable.setColumnAt(2, BYTE);
        assertEquals(BYTE, (Object) storeable.getByteAt(2));
    }

    @Test
    public void getFloat() {
        storeable.setColumnAt(3, FLOAT);
        assertEquals(FLOAT, (Object) storeable.getFloatAt(3));
    }

    @Test
    public void getDouble() {
        storeable.setColumnAt(4, DOUBLE);
        assertEquals(DOUBLE, (Object) storeable.getDoubleAt(4));
    }

    @Test
    public void getBoolean() {
        storeable.setColumnAt(5, BOOLEAN);
        assertEquals(BOOLEAN, storeable.getBooleanAt(5));
    }

    @Test
    public void getString() {
        storeable.setColumnAt(6, STRING);
        assertEquals(STRING, storeable.getStringAt(6));
    }

    @Test(expected = ColumnFormatException.class)
    public void setColumnFormatException() {
        storeable.setColumnAt(0, STRING);
    }

    @Test(expected = ColumnFormatException.class)
    public void getColumnFormatException() {
        storeable.setColumnAt(0, INTEGER);
        storeable.getStringAt(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndexOutOfBounds() {
        Storeable storeable = new TableEntry(new ArrayList<>());
        storeable.setColumnAt(-1, new Object());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setColumnIndexOutOfBounds() {
        Storeable storeable = new TableEntry(new ArrayList<>());
        storeable.setColumnAt(1, new Object());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnIndexOutOfBounds() {
        Storeable storeable = new TableEntry(new ArrayList<>());
        storeable.getColumnAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIntIndexOutOfBounds() {
        Storeable storeable = new TableEntry(new ArrayList<>());
        storeable.getIntAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getLongIndexOutOfBounds() {
        Storeable storeable = new TableEntry(new ArrayList<>());
        storeable.getLongAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteIndexOutOfBounds() {
        Storeable storeable = new TableEntry(new ArrayList<>());
        storeable.getByteAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFloatIndexOutOfBounds() {
        Storeable storeable = new TableEntry(new ArrayList<>());
        storeable.getFloatAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getDoubleIndexOutOfBounds() {
        Storeable storeable = new TableEntry(new ArrayList<>());
        storeable.getDoubleAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getBooleanIndexOutOfBounds() {
        Storeable storeable = new TableEntry(new ArrayList<>());
        storeable.getBooleanAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getStringIndexOutOfBounds() {
        Storeable storeable = new TableEntry(new ArrayList<>());
        storeable.getStringAt(1);
    }

    @Test
    public void testEquals() {
        Storeable storeable2 = new TableEntry(
                Arrays.asList(Integer.class, Long.class, Byte.class, Float.class, Double.class, Boolean.class,
                        String.class));
        storeable.setColumnAt(0, INTEGER);
        storeable.setColumnAt(1, LONG);
        storeable.setColumnAt(2, BYTE);
        storeable.setColumnAt(3, FLOAT);
        storeable.setColumnAt(4, DOUBLE);
        storeable.setColumnAt(5, BOOLEAN);
        storeable.setColumnAt(6, STRING);
        storeable2.setColumnAt(0, INTEGER);
        storeable2.setColumnAt(1, LONG);
        storeable2.setColumnAt(2, BYTE);
        storeable2.setColumnAt(3, FLOAT);
        storeable2.setColumnAt(4, DOUBLE);
        storeable2.setColumnAt(5, BOOLEAN);
        storeable2.setColumnAt(6, STRING);
        assertTrue(storeable2.equals(storeable));
    }

    @Test
    public void testNotEqualsDifferentValue() {
        Storeable storeable2 = new TableEntry(
                Arrays.asList(Integer.class, Long.class, Byte.class, Float.class, Double.class, Boolean.class,
                        String.class));
        storeable.setColumnAt(0, INTEGER);
        storeable.setColumnAt(1, LONG);
        storeable.setColumnAt(2, BYTE);
        storeable.setColumnAt(3, FLOAT);
        storeable.setColumnAt(4, DOUBLE);
        storeable.setColumnAt(5, BOOLEAN);
        storeable.setColumnAt(6, STRING);
        storeable2.setColumnAt(0, INTEGER);
        storeable2.setColumnAt(1, LONG);
        storeable2.setColumnAt(2, BYTE);
        storeable2.setColumnAt(3, FLOAT);
        storeable2.setColumnAt(4, DOUBLE);
        storeable2.setColumnAt(5, BOOLEAN);
        storeable2.setColumnAt(6, "A bit different");
        assertFalse(storeable2.equals(storeable));
    }

    @Test
    public void testNotEqualsDifferentSignature() {
        Storeable storeable2 = new TableEntry(Arrays.asList(Integer.class));
        storeable.setColumnAt(0, INTEGER);
        storeable.setColumnAt(1, LONG);
        storeable.setColumnAt(2, BYTE);
        storeable.setColumnAt(3, FLOAT);
        storeable.setColumnAt(4, DOUBLE);
        storeable.setColumnAt(5, BOOLEAN);
        storeable.setColumnAt(6, STRING);
        storeable2.setColumnAt(0, INTEGER);
        assertFalse(storeable2.equals(storeable));
    }

}
