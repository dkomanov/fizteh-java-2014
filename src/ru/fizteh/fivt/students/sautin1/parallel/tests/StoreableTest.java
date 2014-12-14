package ru.fizteh.fivt.students.sautin1.parallel.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.TableRow;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by sautin1 on 12/12/14.
 */
public class StoreableTest {
    private List<Class<?>> valueTypes;
    private Storeable row;

    @Before
    public void setUp() throws Exception {
        valueTypes = new ArrayList<>();
        valueTypes.add(Integer.class);
        valueTypes.add(Long.class);
        valueTypes.add(Byte.class);
        valueTypes.add(Float.class);
        valueTypes.add(Double.class);
        valueTypes.add(Boolean.class);
        valueTypes.add(String.class);
        row = new TableRow(valueTypes);
    }

    @Test
    public void testSetColumn() throws Exception {
        Object[] values = new Object[] {1, 2_000_000_000_000_000_000L, (byte) 124, 3.14f, 5.415e-5,
                false, "TestString"};
        for (int valueIndex = 0; valueIndex < values.length; ++valueIndex) {
            row.setColumnAt(valueIndex, values[valueIndex]);
        }
        // can be null
        for (int valueIndex = 0; valueIndex < values.length; ++valueIndex) {
            row.setColumnAt(valueIndex, null);
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetColumnIndexException() throws Exception {
        row.setColumnAt(valueTypes.size(), null);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetColumnFormatException() throws Exception {
        row.setColumnAt(0, "1");
    }

    @Test
    public void testGetColumnAt() throws Exception {
        Object[] values = new Object[] {1, 2_000_000_000_000_000_000L, (byte) 124, 3.14f, 5.415e-5, false,
                "TestString"};
        for (int valueIndex = 0; valueIndex < values.length; ++valueIndex) {
            row.setColumnAt(valueIndex, values[valueIndex]);
            assertEquals(row.getColumnAt(valueIndex), values[valueIndex]);
        }
        // can be null
        row.setColumnAt(values.length - 1, null);
        assertNull(row.getColumnAt(values.length - 1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetColumnIndexException() throws Exception {
        row.getColumnAt(valueTypes.size());
    }

    @Test
    public void testGetIntAt() throws Exception {
        int value = (int) (Math.random() * Integer.MAX_VALUE);
        row.setColumnAt(0, value);
        assertTrue(row.getIntAt(0).equals(value));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIntAtIndexException() throws Exception {
        row.getIntAt(valueTypes.size());
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetIntAtFormatException() throws Exception {
        row.getIntAt(1);
    }

    @Test
    public void testGetLongAt() throws Exception {
        long value = (long) (Math.random() * Long.MAX_VALUE);
        row.setColumnAt(1, value);
        assertTrue(row.getLongAt(1).equals(value));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetLongAtIndexException() throws Exception {
        row.getIntAt(valueTypes.size());
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetLongAtFormatException() throws Exception {
        row.getIntAt(2);
    }

    @Test
    public void testGetByteAt() throws Exception {
        byte value = (byte) (Math.random() * Byte.MAX_VALUE);
        row.setColumnAt(2, value);
        assertTrue(row.getByteAt(2).equals(value));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetByteAtIndexException() throws Exception {
        row.getByteAt(valueTypes.size());
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetByteAtFormatException() throws Exception {
        row.getByteAt(3);
    }

    @Test
    public void testGetFloatAt() throws Exception {
        float value = (float) (Math.random() * Float.MAX_VALUE);
        row.setColumnAt(3, value);
        assertTrue(row.getFloatAt(3).equals(value));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetFloatAtIndexException() throws Exception {
        row.getFloatAt(valueTypes.size());
    }
    @Test(expected = ColumnFormatException.class)
    public void testGetFloatAtFormatException() throws Exception {
        row.getFloatAt(4);
    }

    @Test
    public void testGetDoubleAt() throws Exception {
        double value = (double) (Math.random() * Double.MAX_VALUE);
        row.setColumnAt(4, value);
        assertTrue(row.getDoubleAt(4).equals(value));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetDoubleAtIndexException() throws Exception {
        row.getDoubleAt(valueTypes.size());
    }
    @Test(expected = ColumnFormatException.class)
    public void testGetDoubleAtFormatException() throws Exception {
        row.getDoubleAt(5);
    }

    @Test
    public void testGetBooleanAt() throws Exception {
        boolean value = (Math.random() <= 0.5);
        row.setColumnAt(5, value);
        assertTrue(row.getBooleanAt(5).equals(value));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBooleanAtIndexException() throws Exception {
        row.getBooleanAt(valueTypes.size());
    }
    @Test(expected = ColumnFormatException.class)
    public void testGetBooleanAtFormatException() throws Exception {
        row.getBooleanAt(6);
    }

    @Test
    public void testGetStringAt() throws Exception {
        String value = "testString";
        row.setColumnAt(6, value);
        assertTrue(row.getStringAt(6).equals(value));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetStringAtIndexException() throws Exception {
        row.getStringAt(valueTypes.size());
    }
    @Test(expected = ColumnFormatException.class)
    public void testGetStringAtFormatException() throws Exception {
        row.getStringAt(0);
    }
}
