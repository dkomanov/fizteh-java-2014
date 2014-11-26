package ru.fizteh.fivt.students.torunova.storeable.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.torunova.storeable.StoreableType;

import static org.junit.Assert.*;

public class StoreableTest {
    StoreableType storeable;
    @Before
    public void setUp() throws Exception {
        storeable = new StoreableType(String.class,
                Integer.class, Long.class, Float.class,
                Double.class, Byte.class, Boolean.class);
    }
    @Test
    public void testSetColumnAt() throws Exception {
        storeable.setColumnAt(0, "Some string");
        assertEquals("Some string", storeable.getColumnAt(0));
    }
    @Test(expected = ColumnFormatException.class)
    public void testSetColumnAtWithException() throws Exception {
        storeable.setColumnAt(0, 12);
    }

    @Test
    public void testGetColumnAt() throws Exception {
        storeable.setColumnAt(1, 42);
        assertEquals(42, storeable.getColumnAt(1));
    }

    @Test
    public void testGetIntAt() throws Exception {
        storeable.setColumnAt(1, 42);
        assertEquals(42, (int) storeable.getIntAt(1));
    }
    @Test(expected = ColumnFormatException.class)
    public void testGetIntAtWithException() throws Exception {
        storeable.getIntAt(2);
    }

    @Test
    public void testGetLongAt() throws Exception {
        storeable.setColumnAt(2, 42L);
        assertEquals(42, (long) storeable.getLongAt(2));
    }
    @Test(expected = ColumnFormatException.class)
    public void testGetLongAtWithException() throws Exception {
        storeable.getLongAt(4);
    }

    @Test
    public void testGetByteAt() throws Exception {
        storeable.setColumnAt(5, (byte) 42);
        assertEquals(42, (byte) storeable.getByteAt(5));
    }
    @Test(expected = ColumnFormatException.class)
    public void testGetByteAtWithException() throws Exception {
        storeable.getByteAt(0);
    }

    @Test
    public void testGetFloatAt() throws Exception {
        storeable.setColumnAt(3, 1.42f);
        assertEquals(1.42, (float) storeable.getFloatAt(3), 0.001);
    }
    @Test(expected = ColumnFormatException.class)
    public void testGetFloatAtWithException() throws Exception {
        storeable.getFloatAt(5);
    }

    @Test
    public void testGetDoubleAt() throws Exception {
        storeable.setColumnAt(4, 1.42);
        assertEquals(1.42, (double) storeable.getDoubleAt(4), 0.001);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetDoubleAtWithException() throws Exception {
        storeable.getDoubleAt(5);
    }

    @Test
    public void testGetBooleanAt() throws Exception {
        storeable.setColumnAt(6, false);
        assertEquals(false, storeable.getBooleanAt(6));
    }
    @Test(expected = ColumnFormatException.class)
    public void testGetBooleanAtWithException() throws Exception {
        storeable.getBooleanAt(5);
    }

    @Test
    public void testGetStringAt() throws Exception {
        storeable.setColumnAt(0, "Some string");
        assertEquals("Some string", storeable.getStringAt(0));
    }
    @Test(expected = ColumnFormatException.class)
    public void testGetStringAtWithException() throws Exception {
        storeable.getStringAt(5);
    }
}
