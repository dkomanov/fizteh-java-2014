package ru.fizteh.fivt.students.VasilevKirill.Storeable.JUnitTests;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.MyStorable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StoreableTest {
    private static Storeable stor;

    static {
        List<Class<?>> typeList = new ArrayList<>();
        typeList.add(Integer.class);
        typeList.add(Long.class);
        typeList.add(Byte.class);
        typeList.add(Float.class);
        typeList.add(Double.class);
        typeList.add(Boolean.class);
        typeList.add(String.class);
        stor = new MyStorable(typeList);
    }

    @Test
    public void testSetColumnAt() throws Exception {
        try {
            stor.setColumnAt(-1, 1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        stor.setColumnAt(0, null);
        assertNull(stor.getColumnAt(0));
        try {
            stor.setColumnAt(0, "string");
            assertTrue(false);
        } catch (ColumnFormatException e) {
            assertTrue(true);
        }
        stor.setColumnAt(1, 123L);
        assertEquals(123L, stor.getColumnAt(1));
    }

    @Test
    public void testGetColumnAt() throws Exception {
        try {
            stor.getColumnAt(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        try {
            stor.getColumnAt(10);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        stor.setColumnAt(1, 123L);
        assertEquals(123L, stor.getColumnAt(1));
    }

    @Test
    public void testGetIntAt() throws Exception {
        try {
            stor.getIntAt(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        try {
            stor.getIntAt(2);
            assertTrue(false);
        } catch (ColumnFormatException e) {
            assertTrue(true);
        }
        stor.setColumnAt(0, null);
        assertNull(stor.getIntAt(0));
        stor.setColumnAt(0, 123);
        assertEquals(123, (int) stor.getIntAt(0));
    }

    @Test
    public void testGetLongAt() throws Exception {
        try {
            stor.getLongAt(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        try {
            stor.getLongAt(2);
            assertTrue(false);
        } catch (ColumnFormatException e) {
            assertTrue(true);
        }
        stor.setColumnAt(1, null);
        assertNull(stor.getLongAt(1));
        stor.setColumnAt(1, 123L);
        assertEquals(123L, (long) stor.getLongAt(1));
    }

    @Test
    public void testGetByteAt() throws Exception {
        try {
            stor.getByteAt(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        try {
            stor.getByteAt(1);
            assertTrue(false);
        } catch (ColumnFormatException e) {
            assertTrue(true);
        }
        stor.setColumnAt(2, null);
        assertNull(stor.getByteAt(2));
        stor.setColumnAt(2, (byte) 123);
        assertEquals(123, (byte) stor.getByteAt(2));
    }

    @Test
    public void testGetFloatAt() throws Exception {
        try {
            stor.getFloatAt(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        try {
            stor.getFloatAt(2);
            assertTrue(false);
        } catch (ColumnFormatException e) {
            assertTrue(true);
        }
        stor.setColumnAt(3, null);
        assertNull(stor.getFloatAt(3));
        stor.setColumnAt(3, 123.45f);
        assertEquals(123.45f, stor.getFloatAt(3), 0.01);
    }

    @Test
    public void testGetDoubleAt() throws Exception {
        try {
            stor.getDoubleAt(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        try {
            stor.getDoubleAt(2);
            assertTrue(false);
        } catch (ColumnFormatException e) {
            assertTrue(true);
        }
        stor.setColumnAt(4, null);
        assertNull(stor.getDoubleAt(4));
        stor.setColumnAt(4, 123.45D);
        assertEquals(123.45D,  stor.getDoubleAt(4), 0.01);
    }

    @Test
    public void testGetBooleanAt() throws Exception {
        try {
            stor.getBooleanAt(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        try {
            stor.getBooleanAt(2);
            assertTrue(false);
        } catch (ColumnFormatException e) {
            assertTrue(true);
        }
        stor.setColumnAt(5, null);
        assertNull(stor.getBooleanAt(5));
        stor.setColumnAt(5, true);
        assertTrue(stor.getBooleanAt(5));
    }

    @Test
    public void testGetStringAt() throws Exception {
        try {
            stor.getStringAt(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        try {
            stor.getStringAt(2);
            assertTrue(false);
        } catch (ColumnFormatException e) {
            assertTrue(true);
        }
        stor.setColumnAt(6, null);
        assertNull(stor.getStringAt(6));
        stor.setColumnAt(6, "123");
        assertEquals("123", stor.getStringAt(6));
    }
}
