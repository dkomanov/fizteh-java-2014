package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Tests;

import org.junit.Assert;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.CurrentStoreable;

import java.util.ArrayList;
import java.util.List;

public class CurrentStoreableTest {
    CurrentStoreable storeable;

    private void initFullStoreable() {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        list.add(Long.class);
        list.add(Byte.class);
        list.add(Float.class);
        list.add(Double.class);
        list.add(Boolean.class);
        list.add(String.class);
        storeable = new CurrentStoreable(list);
    }

    private void initIntStoreable() {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        storeable = new CurrentStoreable(list);
    }

    private void initLongStoreable() {
        List<Class<?>> list = new ArrayList<>();
        list.add(Long.class);
        storeable = new CurrentStoreable(list);
    }

    @Test
    public void correctConstructor() {
        initFullStoreable();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setColumnOutOfBounds() {
        initIntStoreable();
        storeable.setColumnAt(1, 100);
    }

    @Test(expected = ColumnFormatException.class)
    public void setColumnInvalidFormat() {
        initIntStoreable();
        storeable.setColumnAt(0, Long.valueOf(1));
    }

    @Test
    public void setGetColumn() {
        initFullStoreable();
        storeable.setColumnAt(0, Integer.valueOf(1));
        storeable.setColumnAt(1, Long.valueOf(2));
        storeable.setColumnAt(2, Byte.valueOf((byte) 3));
        storeable.setColumnAt(3, Float.valueOf((float) 4.0));
        storeable.setColumnAt(4, Double.valueOf(5.0));
        storeable.setColumnAt(5, true);
        storeable.setColumnAt(6, "six");

        Assert.assertTrue((storeable.getColumnAt(0)).equals(1));
        Assert.assertTrue((storeable.getColumnAt(1)).equals((long) 2));
        Assert.assertTrue((storeable.getColumnAt(2)).equals((byte) 3));
        Assert.assertTrue((storeable.getColumnAt(3)).equals((float) 4.0));
        Assert.assertTrue((storeable.getColumnAt(4)).equals(5.0));
        Assert.assertTrue((storeable.getColumnAt(5)).equals(true));
        Assert.assertTrue((storeable.getColumnAt(6)).equals("six"));

        Assert.assertEquals(Integer.valueOf(1), storeable.getIntAt(0));
        Assert.assertEquals(Long.valueOf(2), storeable.getLongAt(1));
        Assert.assertEquals(Byte.valueOf((byte) 3), storeable.getByteAt(2));
        Assert.assertEquals(Float.valueOf((float) 4.0), storeable.getFloatAt(3));
        Assert.assertEquals(Double.valueOf(5.0), storeable.getDoubleAt(4));
        Assert.assertEquals(true, storeable.getBooleanAt(5));
        Assert.assertEquals("six", storeable.getStringAt(6));
    }

    @Test
    public void nullGet() {
        initFullStoreable();
        Assert.assertNull((storeable.getColumnAt(0)));
        Assert.assertNull((storeable.getColumnAt(1)));
        Assert.assertNull((storeable.getColumnAt(2)));
        Assert.assertNull((storeable.getColumnAt(3)));
        Assert.assertNull((storeable.getColumnAt(4)));
        Assert.assertNull((storeable.getColumnAt(5)));

        Assert.assertNull(storeable.getIntAt(0));
        Assert.assertNull(storeable.getLongAt(1));
        Assert.assertNull(storeable.getByteAt(2));
        Assert.assertNull(storeable.getFloatAt(3));
        Assert.assertNull(storeable.getDoubleAt(4));
        Assert.assertNull(storeable.getBooleanAt(5));
    }


    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnOutOfBounds() {
        initIntStoreable();
        storeable.getColumnAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void integerOutOfBounds() {
        initIntStoreable();
        storeable.getIntAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void longOutOfBounds() {
        initIntStoreable();
        storeable.getLongAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void byteOutOfBounds() {
        initIntStoreable();
        storeable.getByteAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void floatOutOfBounds() {
        initIntStoreable();
        storeable.getFloatAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void doubleOutOfBounds() {
        initIntStoreable();
        storeable.getDoubleAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void booleanOutOfBounds() {
        initIntStoreable();
        storeable.getBooleanAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void stringOutOfBounds() {
        initIntStoreable();
        storeable.getStringAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void integerColumnInvalidFormat() {
        initLongStoreable();
        storeable.getIntAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void longColumnInvalidFormat() {
        initIntStoreable();
        storeable.getLongAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void byteColumnInvalidFormat() {
        initIntStoreable();
        storeable.getByteAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void floatColumnInvalidFormat() {
        initIntStoreable();
        storeable.getFloatAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void doubleColumnInvalidFormat() {
        initIntStoreable();
        storeable.getDoubleAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void booleanColumnInvalidFormat() {
        initIntStoreable();
        storeable.getBooleanAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void stringColumnInvalidFormat() {
        initIntStoreable();
        storeable.getStringAt(0);
    }
}
