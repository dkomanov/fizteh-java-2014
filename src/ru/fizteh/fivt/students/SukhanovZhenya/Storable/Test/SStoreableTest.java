package ru.fizteh.fivt.students.SukhanovZhenya.Storable.Test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.SukhanovZhenya.Storable.SStoreable;

import java.util.ArrayList;
import java.util.List;

public class SStoreableTest {

    Storeable storeable;

    @Before
    public void setUp() throws Exception {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        list.add(Long.class);
        list.add(Byte.class);
        list.add(Float.class);
        list.add(Double.class);
        list.add(Boolean.class);
        list.add(String.class);

        List<Object> objects = new ArrayList<>();
        objects.add(null);
        objects.add(null);
        objects.add(null);
        objects.add(null);
        objects.add(null);
        objects.add(null);
        objects.add(null);

        storeable = new SStoreable(objects, list);
    }
    @Test
    public void tmp() {
        Assert.assertEquals(0, 0);
    }


    @Test
    public void testGetIntAt() throws Exception {
        Assert.assertNull(storeable.getIntAt(0));
    }

    @Test
    public void testGetLongAt() throws Exception {
        Assert.assertNull(storeable.getLongAt(1));
    }

    @Test
    public void testGetByteAt() throws Exception {
        Assert.assertNull(storeable.getByteAt(2));
    }

    @Test
    public void testGetFloatAt() throws Exception {
        Assert.assertNull(storeable.getFloatAt(3));
    }

    @Test
    public void testGetDoubleAt() throws Exception {
        Assert.assertNull(storeable.getDoubleAt(4));
    }

    @Test
    public void testGetBooleanAt() throws Exception {
        Assert.assertNull(storeable.getBooleanAt(5));
    }

    @Test
    public void testGetStringAt() throws Exception {
        Assert.assertNull(storeable.getStringAt(6));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetColumnAtIndexException() throws Exception {
        storeable.setColumnAt(100, Long.valueOf(1));
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetColumnAtColumnException() throws Exception {
        storeable.setColumnAt(0, String.valueOf("test"));
    }

    @Test
    public void testSetColumnAt() throws Exception {
        storeable.setColumnAt(0, Integer.valueOf(1));
        Assert.assertEquals(Integer.valueOf(1), storeable.getIntAt(0));

        storeable.setColumnAt(1, Long.valueOf(1));
        Assert.assertEquals(Long.valueOf(1), storeable.getLongAt(1));

        storeable.setColumnAt(2, Byte.valueOf((byte) 2));
        Assert.assertEquals(Byte.valueOf((byte) 2), storeable.getByteAt(2));

        storeable.setColumnAt(3, Float.valueOf(3));
        Assert.assertEquals(Float.valueOf(3), storeable.getFloatAt(3));

        storeable.setColumnAt(4, Double.valueOf(4));
        Assert.assertEquals(Double.valueOf(4), storeable.getDoubleAt(4));

        storeable.setColumnAt(5, Boolean.valueOf(true));
        Assert.assertEquals(Boolean.valueOf(true), storeable.getBooleanAt(5));

        storeable.setColumnAt(6, String.valueOf("6"));
        Assert.assertEquals(String.valueOf("6"), storeable.getStringAt(6));
    }

}
