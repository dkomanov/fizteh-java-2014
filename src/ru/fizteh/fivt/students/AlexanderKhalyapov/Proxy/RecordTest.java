package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RecordTest {
    private List<Class<?>> valuesTypes = new ArrayList<>();
    private static final int TEST_INT = 128;
    private static final long TEST_LONG = Long.MAX_VALUE;
    private static final byte TEST_BYTE = (byte) 100;
    private static final float TEST_FLOAT = (float) 5.5;
    private static final double TEST_DOUBLE = Double.MAX_VALUE;
    private static final boolean TEST_BOOLEAN = true;
    private static final String TEST_STRING = "string";

    @Test(expected = ColumnFormatException.class)
    public void testSetNullAtIntColumn() throws Exception {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, null);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetNullAtLongColumn() throws Exception {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, null);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetNullAtByteColumn() throws Exception {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, null);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetNullAtFloatColumn() throws Exception {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, null);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetNullAtDoubleColumn() throws Exception {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, null);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetNullAtBooleanColumn() throws Exception {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, null);
    }

    @Test
    public void testSetNullAtStringColumn() throws Exception {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, null);

        assertNull(test.getColumnAt(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetWithNegativeColumnIndex() throws Exception {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(-1, 5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetWithInvalidColumnIndex() throws Exception {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(1, 5);
    }

    @Test
    public void testSetIntToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_INT);

        assertEquals(test.getColumnAt(0), TEST_INT);

        assertEquals((Object) test.getIntAt(0), TEST_INT);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetLongToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_LONG);
    }

    @Test
    public void testSetByteToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BYTE);

        assertEquals(test.getColumnAt(0), (int) TEST_BYTE);

        assertEquals((Object) test.getIntAt(0), (int) TEST_BYTE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_FLOAT);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_DOUBLE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BOOLEAN);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_STRING);
    }

    @Test
    public void testSetIntToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_INT);

        assertEquals(test.getColumnAt(0), (long) TEST_INT);

        assertEquals((Object) test.getLongAt(0), (long) TEST_INT);
    }

    @Test
    public void testSetLongToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_LONG);

        assertEquals(test.getColumnAt(0), TEST_LONG);

        assertEquals((Object) test.getLongAt(0), TEST_LONG);
    }

    @Test
    public void testSetByteToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BYTE);

        assertEquals(test.getColumnAt(0), (long) TEST_BYTE);

        assertEquals((Object) test.getLongAt(0), (long) TEST_BYTE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_FLOAT);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_DOUBLE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BOOLEAN);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_STRING);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetIntToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_INT);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetLongToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_LONG);
    }

    @Test
    public void testSetByteToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BYTE);

        assertEquals(test.getColumnAt(0), TEST_BYTE);

        assertEquals((Object) test.getByteAt(0), TEST_BYTE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_FLOAT);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_DOUBLE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BOOLEAN);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_STRING);
    }

    @Test
    public void testSetIntToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_INT);

        assertEquals(test.getColumnAt(0), (float) TEST_INT);

        assertEquals((Object) test.getFloatAt(0), (float) TEST_INT);
    }

    @Test
    public void testSetLongToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_LONG);

        assertEquals(test.getColumnAt(0), (float) TEST_LONG);

        assertEquals((Object) test.getFloatAt(0), (float) TEST_LONG);
    }

    @Test
    public void testSetByteToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BYTE);

        assertEquals(test.getColumnAt(0), (float) TEST_BYTE);

        assertEquals((Object) test.getFloatAt(0), (float) TEST_BYTE);
    }

    @Test
    public void testSetFloatToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_FLOAT);

        assertEquals(test.getColumnAt(0), TEST_FLOAT);

        assertEquals((Object) test.getFloatAt(0), TEST_FLOAT);
    }

    @Test
    public void testSetDoubleToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_DOUBLE);

        assertEquals(test.getColumnAt(0), (float) TEST_DOUBLE);

        assertEquals((Object) test.getFloatAt(0), (float) TEST_DOUBLE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BOOLEAN);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_STRING);
    }

    @Test
    public void testSetIntToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_INT);

        assertEquals(test.getColumnAt(0), (double) TEST_INT);

        assertEquals((Object) test.getDoubleAt(0), (double) TEST_INT);
    }

    @Test
    public void testSetLongToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_LONG);

        assertEquals(test.getColumnAt(0), (double) TEST_LONG);

        assertEquals((Object) test.getDoubleAt(0), (double) TEST_LONG);
    }

    @Test
    public void testSetByteToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BYTE);

        assertEquals(test.getColumnAt(0), (double) TEST_BYTE);

        assertEquals((Object) test.getDoubleAt(0), (double) TEST_BYTE);
    }

    @Test
    public void testSetFloatToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_FLOAT);

        assertEquals(test.getColumnAt(0), (double) TEST_FLOAT);

        assertEquals((Object) test.getDoubleAt(0), (double) TEST_FLOAT);
    }

    @Test
    public void testSetDoubleToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_DOUBLE);

        assertEquals(test.getColumnAt(0), TEST_DOUBLE);

        assertEquals((Object) test.getDoubleAt(0), TEST_DOUBLE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BOOLEAN);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_STRING);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetIntToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_INT);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetLongToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_LONG);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetByteToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BYTE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_FLOAT);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_DOUBLE);
    }

    @Test
    public void testSetBooleanToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BOOLEAN);

        assertEquals(test.getColumnAt(0), TEST_BOOLEAN);

        assertEquals(test.getBooleanAt(0), TEST_BOOLEAN);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToBooleanFirst() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_STRING);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToBooleanSecond() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, "\"true\"");
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetIntToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_INT);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetLongToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_LONG);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetByteToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BYTE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_FLOAT);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_DOUBLE);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_BOOLEAN);
    }

    @Test
    public void testSetStringToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, TEST_STRING);

        assertEquals(test.getColumnAt(0), TEST_STRING);

        assertEquals(test.getStringAt(0), TEST_STRING);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIntAtWrongIndex() throws Exception {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.getIntAt(1);
    }

    @Test
    public void testReturnsCorrectStringCallToString() {
        valuesTypes.add(Integer.class);
        valuesTypes.add(Integer.class);
        valuesTypes.add(String.class);
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, 1);
        test.setColumnAt(1, 2);
        test.setColumnAt(2, null);
        test.setColumnAt(3, TEST_STRING);

        String expected = "Record[1,2,,string]";
        String result = test.toString();
        assertEquals(expected, result);
    }
}
