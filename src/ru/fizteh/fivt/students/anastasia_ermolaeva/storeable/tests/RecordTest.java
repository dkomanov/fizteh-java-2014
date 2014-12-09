package ru.fizteh.fivt.students.anastasia_ermolaeva.storeable.tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.anastasia_ermolaeva.storeable.Record;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RecordTest {
    private List<Class<?>> valuesTypes = new ArrayList<>();
    private final int testInt = 128;
    private final long testLong = Long.MAX_VALUE;
    private final byte testByte = (byte) 100;
    private final float testFloat = (float) 5.5;
    private final double testDouble = Double.MAX_VALUE;
    private final boolean testBoolean = true;
    private final String testString = "string";

    /*
    * Tests on setter.
     */

    /*
    * Set null tests.
     */
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

    /*
    * Invalid index tests.
     */
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

    /*
    Int tests.
     */
    @Test
    public void testSetIntToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testInt);

        assertEquals(test.getColumnAt(0), testInt);

        assertEquals((Object) test.getIntAt(0), testInt);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetLongToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testLong);
    }

    @Test
    public void testSetByteToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testByte);

        assertEquals(test.getColumnAt(0), (int) testByte);

        assertEquals((Object) test.getIntAt(0), (int) testByte);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testFloat);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToInt() {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testString);
    }

    /*
    * Long tests.
     */
    @Test
    public void testSetIntToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testInt);

        assertEquals(test.getColumnAt(0), (long) testInt);

        assertEquals((Object) test.getLongAt(0), (long) testInt);
    }

    @Test
    public void testSetLongToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testLong);

        assertEquals(test.getColumnAt(0), testLong);

        assertEquals((Object) test.getLongAt(0), testLong);
    }

    @Test
    public void testSetByteToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testByte);

        assertEquals(test.getColumnAt(0), (long) testByte);

        assertEquals((Object) test.getLongAt(0), (long) testByte);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testFloat);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToLong() {
        valuesTypes.add(Long.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testString);
    }

    /*
    * Byte tests.
     */
    @Test(expected = ColumnFormatException.class)
    public void testSetIntToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testInt);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetLongToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testLong);
    }

    @Test
    public void testSetByteToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testByte);

        assertEquals(test.getColumnAt(0), testByte);

        assertEquals((Object) test.getByteAt(0), testByte);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testFloat);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToByte() {
        valuesTypes.add(Byte.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testString);
    }

    /*
    * Float tests.
     */
    @Test
    public void testSetIntToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testInt);

        assertEquals(test.getColumnAt(0), (float) testInt);

        assertEquals((Object) test.getFloatAt(0), (float) testInt);
    }

    @Test
    public void testSetLongToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testLong);

        assertEquals(test.getColumnAt(0), (float) testLong);

        assertEquals((Object) test.getFloatAt(0), (float) testLong);
    }

    @Test
    public void testSetByteToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testByte);

        assertEquals(test.getColumnAt(0), (float) testByte);

        assertEquals((Object) test.getFloatAt(0), (float) testByte);
    }

    @Test
    public void testSetFloatToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testFloat);

        assertEquals(test.getColumnAt(0), testFloat);

        assertEquals((Object) test.getFloatAt(0), testFloat);
    }

    @Test
    public void testSetDoubleToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testDouble);

        assertEquals(test.getColumnAt(0), (float) testDouble);

        assertEquals((Object) test.getFloatAt(0), (float) testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToFloat() {
        valuesTypes.add(Float.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testString);
    }

    /*
    * Double tests.
     */
    @Test
    public void testSetIntToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testInt);

        assertEquals(test.getColumnAt(0), (double) testInt);

        assertEquals((Object) test.getDoubleAt(0), (double) testInt);
    }

    @Test
    public void testSetLongToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testLong);

        assertEquals(test.getColumnAt(0), (double) testLong);

        assertEquals((Object) test.getDoubleAt(0), (double) testLong);
    }

    @Test
    public void testSetByteToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testByte);

        assertEquals(test.getColumnAt(0), (double) testByte);

        assertEquals((Object) test.getDoubleAt(0), (double) testByte);
    }

    @Test
    public void testSetFloatToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testFloat);

        assertEquals(test.getColumnAt(0), (double) testFloat);

        assertEquals((Object) test.getDoubleAt(0), (double) testFloat);
    }

    @Test
    public void testSetDoubleToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testDouble);

        assertEquals(test.getColumnAt(0), testDouble);

        assertEquals((Object) test.getDoubleAt(0), testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToDouble() {
        valuesTypes.add(Double.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testString);
    }

    /*
    * Boolean tests.
     */
    @Test(expected = ColumnFormatException.class)
    public void testSetIntToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testInt);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetLongToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testLong);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetByteToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testByte);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testFloat);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testDouble);
    }

    @Test
    public void testSetBooleanToBoolean() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testBoolean);

        assertEquals(test.getColumnAt(0), testBoolean);

        assertEquals(test.getBooleanAt(0), testBoolean);
    }


    @Test(expected = ColumnFormatException.class)
    public void testSetStringToBooleanFirst() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testString);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToBooleanSecond() {
        valuesTypes.add(Boolean.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, "\"true\"");
    }

    /*
    * String tests.
     */
    @Test(expected = ColumnFormatException.class)
    public void testSetIntToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testInt);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetLongToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testLong);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetByteToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testByte);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testFloat);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testBoolean);
    }

    @Test
    public void testSetStringToString() {
        valuesTypes.add(String.class);
        Storeable test = new Record(valuesTypes);
        test.setColumnAt(0, testString);

        assertEquals(test.getColumnAt(0), testString);

        assertEquals(test.getStringAt(0), testString);
    }

    /*
    * Test on getters.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIntAtWrongIndex() throws Exception {
        valuesTypes.add(Integer.class);
        Storeable test = new Record(valuesTypes);
        test.getIntAt(1);
    }
}