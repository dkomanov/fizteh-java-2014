package ru.fizteh.fivt.students.lukina.Tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.lukina.DataBase.DBaseProviderFactory;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DBaseRowTest {
    @Rule
    public TemporaryFolder root = new TemporaryFolder();
    Storeable row;

    @Before
    public void init() throws IOException {
        @SuppressWarnings("resource")
        DBaseProviderFactory fact = new DBaseProviderFactory();
        TableProvider prov = null;
        prov = fact.create(root.newFolder().toString());
        ArrayList<Class<?>> columnTypes = new ArrayList<>();
        columnTypes.add(int.class);
        columnTypes.add(long.class);
        columnTypes.add(byte.class);
        columnTypes.add(float.class);
        columnTypes.add(double.class);
        columnTypes.add(boolean.class);
        columnTypes.add(String.class);
        Table table = null;
        table = prov.createTable("testTable", columnTypes);
        row = prov.createFor(table);
    }

    @Test
    public void setColumnAtTestInt() {
        row.setColumnAt(0, 1);
        assertSame(row.getColumnAt(0), 1);
    }

    @Test
    public void setColumnAtTestString() {
        String test = "qwerty";
        row.setColumnAt(6, test);
        assertSame(row.getColumnAt(6), "qwerty");
    }

    @Test(expected = ColumnFormatException.class)
    public void setColumnAtIncorrectFormat() {
        String test = "Test";
        row.setColumnAt(0, test);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setColumnAtNegativeIndex() {
        row.setColumnAt(-1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setColumnAtBigIndex() {
        row.setColumnAt(34, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnAtBigIndex() {
        row.getColumnAt(35);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnAtNegativeIndex() {
        row.getColumnAt(-5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIntAtIndexLess() {
        row.getIntAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIntAtIndexMore() {
        row.getIntAt(111);
    }

    @Test(expected = ColumnFormatException.class)
    public void getIntAtIndexColumnFormat() {
        row.getStringAt(0);
    }

    @Test
    public void getIntAtTest() {
        int a = 12;
        row.setColumnAt(0, a);
        assertSame(row.getIntAt(0), 12);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getLongAtIndexLess() {
        row.getIntAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getLongAtIndexMore() {
        row.getIntAt(111);
    }

    @Test(expected = ColumnFormatException.class)
    public void getLongAtIndexColumnFormat() {
        row.getStringAt(1);
    }

    @Test
    public void getLongAtTest() {
        long a = 12;
        row.setColumnAt(1, a);
        assertSame(row.getLongAt(1), a);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteAtIndexLess() {
        row.getIntAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteAtIndexMore() {
        row.getIntAt(111);
    }

    @Test(expected = ColumnFormatException.class)
    public void getByteAtIndexColumnFormat() {
        row.getStringAt(0);
    }

    @Test
    public void getByteAtTest() {
        byte a = 12;
        row.setColumnAt(2, a);
        assertSame(row.getByteAt(2), a);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFloatAtIndexLess() {
        row.getIntAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFloatAtIndexMore() {
        row.getIntAt(111);
    }

    @Test(expected = ColumnFormatException.class)
    public void getFloatAtIndexColumnFormat() {
        row.getStringAt(3);
    }

    @Test
    public void getFloatAtTest() {
        float a = 12.1f;
        row.setColumnAt(3, a);
        assertTrue(row.getFloatAt(3).equals(((Double) 12.1).floatValue()));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getDoubleAtIndexLess() {
        row.getIntAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getDoubleAtIndexMore() {
        row.getIntAt(111);
    }

    @Test(expected = ColumnFormatException.class)
    public void getDoubleAtIndexColumnFormat() {
        row.getStringAt(4);
    }

    @Test
    public void getDoubleAtTest() {
        double a = 12;
        row.setColumnAt(4, a);
        assertTrue(row.getDoubleAt(4).equals(
                (Double) ((Integer) 12).doubleValue()));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getBooleanAtIndexLess() {
        row.getIntAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getBooleanAtIndexMore() {
        row.getIntAt(111);
    }

    @Test(expected = ColumnFormatException.class)
    public void getBooleanAtIndexColumnFormat() {
        row.getStringAt(5);
    }

    @Test
    public void getBooleanAtTest() {
        boolean a = true;
        row.setColumnAt(5, a);
        assertSame(row.getBooleanAt(5), true);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getStringAtIndexLess() {
        row.getIntAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getStringAtIndexMore() {
        row.getIntAt(111);
    }

    @Test(expected = ColumnFormatException.class)
    public void getStringAtIndexColumnFormat() {
        row.getIntAt(6);
    }

    @Test
    public void getStringAtTest() {
        String a = "12.1";
        row.setColumnAt(6, a);
        assertSame(row.getStringAt(6), "12.1");
    }

}
