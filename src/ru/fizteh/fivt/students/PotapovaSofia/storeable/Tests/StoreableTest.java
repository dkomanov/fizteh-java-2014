package ru.fizteh.fivt.students.PotapovaSofia.storeable.Tests;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.DataBase.StoreableImpl;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class StoreableTest {
    private List<Class<?>> types = new ArrayList<>();
    private final int testInt = 12;
    private final long testLong = Long.MAX_VALUE;
    private final byte testByte = (byte) 100100;
    private final float testFloat = (float) 12.5;
    private final double testDouble = Double.MAX_VALUE;
    private final boolean testBoolean = true;
    private final String testString = "value";

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetWithNegativeColumnIndex() throws Exception {
        types.add(Integer.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(-1, 5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetWithInvalidColumnIndex() throws Exception {
        types.add(Integer.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(1, 5);
    }

    @Test
    public void testSetIntToInt() {
        types.add(Integer.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testInt);
        assertEquals(test.getColumnAt(0), testInt);
        assertEquals((Object) test.getIntAt(0), testInt);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetLongToInt() {
        types.add(Integer.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testLong);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToInt() {
        types.add(Integer.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testFloat);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToInt() {
        types.add(Integer.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToInt() {
        types.add(Integer.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToInt() {
        types.add(Integer.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testString);
    }

    @Test
    public void testSetLongToLong() {
        types.add(Long.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testLong);
        assertEquals(test.getColumnAt(0), testLong);
        assertEquals((Object) test.getLongAt(0), testLong);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetFloatToLong() {
        types.add(Long.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testFloat);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToLong() {
        types.add(Long.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToLong() {
        types.add(Long.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToLong() {
        types.add(Long.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testString);
    }

    @Test
    public void testSetByteToByte() {
        types.add(Byte.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testByte);
        assertEquals(test.getColumnAt(0), testByte);
        assertEquals((Object) test.getByteAt(0), testByte);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetIntToByte() {
        types.add(Byte.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testInt);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToByte() {
        types.add(Byte.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToByte() {
        types.add(Byte.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testString);
    }

    @Test
    public void testSetFloatToFloat() {
        types.add(Float.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testFloat);
        assertEquals(test.getColumnAt(0), testFloat);
        assertEquals((Object) test.getFloatAt(0), testFloat);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetIntToFloat() {
        types.add(Float.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testInt);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetByteToFloat() {
        types.add(Float.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testByte);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToFloat() {
        types.add(Float.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToFloat() {
        types.add(Float.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToFloat() {
        types.add(Float.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testString);
    }

    @Test
    public void testSetDoubleToDouble() {
        types.add(Double.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testDouble);
        assertEquals(test.getColumnAt(0), testDouble);
        assertEquals((Object) test.getDoubleAt(0), testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetByteToDouble() {
        types.add(Double.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testByte);
        assertEquals(test.getColumnAt(0), (double) testByte);
        assertEquals((Object) test.getDoubleAt(0), (double) testByte);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToDouble() {
        types.add(Double.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToDouble() {
        types.add(Double.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testString);
    }

    @Test
    public void testSetBooleanToBoolean() {
        types.add(Boolean.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testBoolean);
        assertEquals(test.getColumnAt(0), testBoolean);
        assertEquals(test.getBooleanAt(0), testBoolean);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetIntToBoolean() {
        types.add(Boolean.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testInt);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetByteToBoolean() {
        types.add(Boolean.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testByte);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToBoolean() {
        types.add(Boolean.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetStringToBoolean() {
        types.add(Boolean.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testString);
    }

    @Test
    public void testSetStringToString() {
        types.add(String.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testString);
        assertEquals(test.getColumnAt(0), testString);
        assertEquals(test.getStringAt(0), testString);
    }


    @Test(expected = ColumnFormatException.class)
    public void testSetIntToString() {
        types.add(String.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testInt);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetByteToString() {
        types.add(String.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testByte);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetDoubleToString() {
        types.add(String.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testDouble);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetBooleanToString() {
        types.add(String.class);
        Storeable test = new StoreableImpl(types);
        test.setColumnAt(0, testBoolean);
    }
}
