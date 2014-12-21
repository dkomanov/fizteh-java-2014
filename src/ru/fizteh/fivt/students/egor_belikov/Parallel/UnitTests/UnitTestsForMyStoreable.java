package ru.fizteh.fivt.students.egor_belikov.Parallel.UnitTests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.egor_belikov.Parallel.MyStoreable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UnitTestsForMyStoreable {
    private MyStoreable testingStoreable;
    private List<Class<?>> signature;
    private List<Object> valuesList;

    @Before
    public void setUp() {
        valuesList = new ArrayList<>();
        valuesList.add(1);
        valuesList.add("1");
        valuesList.add(true);
        valuesList.add(1.0f);
        signature = new ArrayList<>();
        signature.add(Integer.class);
        signature.add(String.class);
        signature.add(Boolean.class);
        signature.add(Float.class);
        testingStoreable = new MyStoreable(valuesList, signature);
    }

    @Test
    public final void testSetColumnAt() throws Exception {
        testingStoreable.setColumnAt(0, 1);
        assertEquals(1, testingStoreable.getColumnAt(0));
    }

    @Test
    public final void testGetColumnAt() throws Exception {
        assertEquals(1, testingStoreable.getColumnAt(0));
    }

    @Test
    public final void testGetIntAt() throws Exception {
        assertEquals(new Integer(1), testingStoreable.getIntAt(0));
    }

    @Test
    public final void testGetStringAt() throws Exception {
        assertEquals("1", testingStoreable.getStringAt(1));
    }

    @Test
    public final void testGetBoolAt() throws Exception {
        assertEquals(true, testingStoreable.getBooleanAt(2));
    }

    @Test
    public final void testGetFloatAt() throws Exception {
        assertEquals(new Float(1.0f), testingStoreable.getFloatAt(3));
    }

    @Test(expected = ColumnFormatException.class)
    public final void testGetWrongDoubleAt() throws Exception {
        testingStoreable.getDoubleAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public final void testGetWrongNumOfColumn() throws Exception {
        testingStoreable.getDoubleAt(4);
    }
}
