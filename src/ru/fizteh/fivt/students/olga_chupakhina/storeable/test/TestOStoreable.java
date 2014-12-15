package ru.fizteh.fivt.students.olga_chupakhina.storeable.test;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.olga_chupakhina.storeable.OStoreable;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class TestOStoreable {
    private OStoreable test;
    private List<Class<?>> signature;
    private List<Object> values;

    @Before
    public void setUp() {
        values = new ArrayList<>();
        values.add(1);
        values.add("1");
        values.add(true);
        values.add(1.0f);
        signature = new ArrayList<>();
        signature.add(Integer.class);
        signature.add(String.class);
        signature.add(Boolean.class);
        signature.add(Float.class);
        test = new OStoreable(values, signature);
    }

    @Test
    public final void testSetColumnAt() throws Exception {
        test.setColumnAt(0, 1);
        assertEquals(1, test.getColumnAt(0));
    }

    @Test
    public final void testGetColumnAt() throws Exception {
        assertEquals(1, test.getColumnAt(0));
    }

    @Test
    public final void testGetIntAt() throws Exception {
        assertEquals(new Integer(1), test.getIntAt(0));
    }

    @Test
    public final void testGetStringAt() throws Exception {
        assertEquals(new String("1"), test.getStringAt(1));
    }

    @Test
    public final void testGetBoolAt() throws Exception {
        assertEquals(true, test.getBooleanAt(2));
    }

    @Test
    public final void testGetFloatAt() throws Exception {
        assertEquals(new Float(1.0f), test.getFloatAt(3));
    }

    @Test(expected = ColumnFormatException.class)
    public final void testGetWrongDoubleAt() throws Exception {
        test.getDoubleAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public final void testGetWrongNumOfColumn() throws Exception {
        test.getDoubleAt(4);
    }
}
