package ru.fizteh.fivt.students.alexpodkin.Tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.alexpodkin.Storeable.StoreableEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 25.11.14.
 */
public class StoreableEntryTest {
    StoreableEntry storeableEntry;

    public void init() {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        list.add(Long.class);
        list.add(Byte.class);
        list.add(Float.class);
        list.add(Double.class);
        list.add(Boolean.class);
        list.add(String.class);
        storeableEntry = new StoreableEntry(list);
    }

    @Test
    public void constructorTest() {
        init();
    }

    @Test(expected = ColumnFormatException.class)
    public void constructorExceptionTest() {
        List<Class<?>> list = new ArrayList<>();
        list.add(ArrayList.class);
        new StoreableEntry(list);
    }

    @Test(expected = ColumnFormatException.class)
    public void setColumnFormatExceptionTest() {
        init();
        storeableEntry.setColumnAt(4, Long.valueOf(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setIndexOfBoundsExceptionTest() {
        init();
        storeableEntry.setColumnAt(100, 1);
    }

    @Test
    public void setTest() {
        init();
        storeableEntry.setColumnAt(0, Integer.valueOf(1));
        storeableEntry.setColumnAt(1, Long.valueOf(2));
        storeableEntry.setColumnAt(2, Byte.valueOf((byte) 3));
        storeableEntry.setColumnAt(3, Float.valueOf((float) 4.0));
        storeableEntry.setColumnAt(4, Double.valueOf(5.0));
        storeableEntry.setColumnAt(5, true);
        storeableEntry.setColumnAt(6, "six");
        Assert.assertTrue((storeableEntry.getColumnAt(0)).equals(1));
        Assert.assertTrue((storeableEntry.getColumnAt(1)).equals((long) 2));
        Assert.assertTrue((storeableEntry.getColumnAt(2)).equals((byte) 3));
        Assert.assertTrue((storeableEntry.getColumnAt(3)).equals((float) 4.0));
        Assert.assertTrue((storeableEntry.getColumnAt(4)).equals(5.0));
        Assert.assertTrue((storeableEntry.getColumnAt(5)).equals(true));
        Assert.assertTrue((storeableEntry.getColumnAt(6)).equals("six"));
        Assert.assertEquals(Integer.valueOf(1), storeableEntry.getIntAt(0));
        Assert.assertEquals(Long.valueOf(2), storeableEntry.getLongAt(1));
        Assert.assertEquals(Byte.valueOf((byte) 3), storeableEntry.getByteAt(2));
        Assert.assertEquals(Float.valueOf((float) 4.0), storeableEntry.getFloatAt(3));
        Assert.assertEquals(Double.valueOf(5.0), storeableEntry.getDoubleAt(4));
        Assert.assertEquals(true, storeableEntry.getBooleanAt(5));
        Assert.assertEquals("six", storeableEntry.getStringAt(6));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWithBadIndexTest() {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        storeableEntry = new StoreableEntry(list);
        storeableEntry.getIntAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void getWithBadColumnTest() {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        storeableEntry = new StoreableEntry(list);
        storeableEntry.getBooleanAt(0);
    }
}
