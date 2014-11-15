package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.test;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.TableRow;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TableRowTest {

    @Test
    public void testNullRow() {
        ArrayList<Object> values = new ArrayList<>();
        TableRow row = new TableRow(values);
        for (int i = 0; i < 5; ++i) {
            try {
                row.setColumnAt(i, 5);
                assertTrue(false);
            } catch (IndexOutOfBoundsException ibe) {
                assertTrue(true);
            }
        }
    }

    @Test
    public void testSetNull() {
        ArrayList<Object> values = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            values.add(null);
        }
        TableRow row = new TableRow(values);
        for (int i = 0; i < 5; ++i) {
            try {
                assertNull(row.getColumnAt(i));
                row.getBooleanAt(i);
                assertTrue(false);
            } catch (ColumnFormatException cfe) {
                assertTrue(true);
            }
        }

    }

    @Test
    public void testGet() {
        ArrayList<Object> values = new ArrayList<>();
        values.add(true);
        values.add(null);
        values.add(Byte.parseByte("1"));
        values.add(Float.parseFloat("1e5"));
        values.add(1000000000L);
        values.add(7);
        values.add("Hello");
        TableRow row = new TableRow(values);
        for (int i = 0; i < values.size(); ++i) {
            assertEquals(values.get(i), row.getColumnAt(i));
        }

        assertEquals(values.get(0), row.getBooleanAt(0));
        assertEquals(values.get(1), row.getColumnAt(1));
        assertEquals(values.get(2), row.getByteAt(2));
        assertEquals(values.get(3), row.getFloatAt(3));
        assertEquals(values.get(4), row.getLongAt(4));
        assertEquals(values.get(5), row.getIntAt(5));
        assertEquals(values.get(6), row.getStringAt(6));

        try {
            row.getStringAt(2);
            assertTrue(false);
        } catch (ColumnFormatException cfe) {
            assertTrue(true);
        }

        try {
            row.getBooleanAt(4);
            assertTrue(false);
        } catch (ColumnFormatException cfe) {
            assertTrue(true);
        }

        try {
            row.getColumnAt(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ibe) {
            assertTrue(true);
        }

        try {
            row.getDoubleAt(5);
            assertTrue(false);
        } catch (ColumnFormatException cfe) {
            assertTrue(true);
        }
    }

    @Test
    public void testSet() {
        ArrayList<Object> values = new ArrayList<>();
        values.add(true);
        values.add(Byte.parseByte("1"));
        values.add(Float.parseFloat("1e5"));
        values.add(Double.parseDouble("1e-5"));
        values.add(1000000000L);
        values.add(7);
        values.add("Hello");
        TableRow row = new TableRow(values);
        row.setColumnAt(0, false);
        row.setColumnAt(1, Byte.parseByte("0"));
        row.setColumnAt(4, 1000000L);
        values.set(0, false);
        values.set(1, Byte.parseByte("0"));
        values.set(4, 1000000L);
        for (int i = 0; i < 7; ++i) {
            assertEquals(values.get(i), row.getColumnAt(i));
        }

        try {
            row.setColumnAt(1, "Hi");
            assertTrue(false);
        } catch (ColumnFormatException cfe) {
            assertTrue(true);
        }

        try {
            row.setColumnAt(-1, null);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ibe) {
            assertTrue(true);
        }
    }
}

