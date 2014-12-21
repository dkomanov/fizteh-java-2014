package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.TableSystem.DatabaseTableRow;

import java.util.ArrayList;
import java.util.List;

public class StoreableTest {
    Storeable storeable;

    @Before
    public void setUp() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        columnTypes.add(String.class);
        storeable = new DatabaseTableRow(columnTypes);
    }

    @After
    public void cleanUp() throws Exception {
        storeable = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNlValueShouldFail() {
        storeable.setColumnAt(1, "    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putEmptyValueShouldFail() {
        storeable.setColumnAt(1, "");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void putValueOutOfBound() {
        storeable.setColumnAt(3, null);
    }

    @Test
    public void putNullValueShouldPass() {
        storeable.setColumnAt(0, null);
        storeable.setColumnAt(1, null);
    }

    @Test(expected = ColumnFormatException.class)
    public void putIncorrectType1ShouldFail() {
        storeable.setColumnAt(0, "adasda");
    }

    @Test(expected = ColumnFormatException.class)
    public void putIncorrectType2ShouldFail() {
        storeable.setColumnAt(1, 2);
    }

    @Test(expected = ColumnFormatException.class)
    public void putIncorrectType3ShouldFail() {
        storeable.setColumnAt(1, new OtherClass());
    }

    @Test
    public void putCorrectValueShouldPass() throws Exception {
        storeable.setColumnAt(1, "String");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getIntAtShouldFail() {
        storeable.getIntAt(3);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getLongAtShouldFail() {
        storeable.getLongAt(3);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getFloatAtShouldFail() {
        storeable.getFloatAt(3);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getDoubleAtShouldFail() {
        storeable.getDoubleAt(3);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getBooleanAtShouldFail() {
        storeable.getBooleanAt(3);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getStringAtShouldFail() {
        storeable.getStringAt(3);
    }

    @Test (expected = ColumnFormatException.class)
    public void getIntAtShouldFail2() {
        storeable.getIntAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void getLongAtShouldFail2() {
        storeable.getLongAt(0);
    }

    @Test (expected = ColumnFormatException.class)
    public void getFloatAtShouldFail2() {
        storeable.getFloatAt(0);
    }

    @Test (expected = ColumnFormatException.class)
    public void getDoubleAtShouldFail2() {
        storeable.getDoubleAt(0);
    }

    @Test (expected = ColumnFormatException.class)
    public void getBooleanAtShouldFail2() {
        storeable.getBooleanAt(0);
    }

    @Test (expected = ColumnFormatException.class)
    public void getStringAtShouldFail2() {
        storeable.getStringAt(0);
    }

    class OtherClass {};
}

