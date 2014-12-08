package ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_structure;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_exceptions.DatabaseCorruptedException;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.util.DirectoryKiller;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StoreableClassTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DataBaseTestDirectory");
    private List<Class<?>> listWhichHoldsAllTypes = new ArrayList<>();
    private TableProvider provider = new TableManager(testDir.toString());
    private List<Object> listWhichHoldsAllValues = new ArrayList<>();
    private Storeable megaStoreable;
    private Table tableWhichHoldsAllTypes;

    @Before
    public void setUp() throws DatabaseCorruptedException {
        testDir.toFile().mkdir();

        listWhichHoldsAllValues.add(1); // int = 0
        listWhichHoldsAllValues.add((long) 2); // long = 1
        listWhichHoldsAllValues.add((byte) 3); // byte = 2
        listWhichHoldsAllValues.add(7.62f); // float = 3
        listWhichHoldsAllValues.add(3.14); // double = 4
        listWhichHoldsAllValues.add(false); // boolean = 5
        listWhichHoldsAllValues.add("string"); // string = 6

        listWhichHoldsAllTypes.add(Integer.class);
        listWhichHoldsAllTypes.add(Long.class);
        listWhichHoldsAllTypes.add(Byte.class);
        listWhichHoldsAllTypes.add(Float.class);
        listWhichHoldsAllTypes.add(Double.class);
        listWhichHoldsAllTypes.add(Boolean.class);
        listWhichHoldsAllTypes.add(String.class);

        tableWhichHoldsAllTypes = new TableClass(testDir, "table name", provider, listWhichHoldsAllTypes);
        megaStoreable = new StoreableClass(tableWhichHoldsAllTypes, listWhichHoldsAllValues);
    }

    @Test
    public void testGetColumnAt() throws Exception {
        assertEquals(megaStoreable.getColumnAt(0), 1);
    }

    @Test
    public void testSetColumnAt() throws Exception {
        megaStoreable.setColumnAt(0, 2);
        assertEquals(megaStoreable.getColumnAt(0), 2);
    }

    @Test (expected = ColumnFormatException.class)
    public void testThrowColumnFormatExceptionWhileGetting() throws Exception {
        megaStoreable.getIntAt(3);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testThrowIndexOutOfBoundExceptionWhileGetting() throws Exception {
        megaStoreable.getIntAt(42);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testThrowIndexOutOfBoundExceptionWhileConstructing() throws Exception {
        listWhichHoldsAllTypes.add(String.class);
        new StoreableClass(tableWhichHoldsAllTypes, listWhichHoldsAllTypes);
    }

    @Test (expected = ColumnFormatException.class)
    public void testThrowColumnFormatExceptionWhileConstructing() throws Exception {
        listWhichHoldsAllTypes.remove(listWhichHoldsAllTypes.size() - 1);
        listWhichHoldsAllTypes.add(Integer.class);
        new StoreableClass(tableWhichHoldsAllTypes, listWhichHoldsAllTypes);
    }


    @Test
    public void testGetIntAt() throws Exception {
        assertEquals(megaStoreable.getIntAt(0), (Integer) (int) 1);
    }

    @Test
    public void testGetLongAt() throws Exception {
        assertEquals(megaStoreable.getLongAt(1), (Long) (long) 2);
    }

    @Test
    public void testGetByteAt() throws Exception {
        assertEquals(megaStoreable.getByteAt(2), (Byte) (byte) 3);
    }

    @Test
    public void testGetFloatAt() throws Exception {
        assertEquals(megaStoreable.getFloatAt(3), (Float) 7.62f);
    }

    @Test
    public void testGetDoubleAt() throws Exception {
        assertEquals(megaStoreable.getDoubleAt(4), (Double) 3.14);
    }

    @Test
    public void testGetBooleanAt() throws Exception {
        assertEquals(megaStoreable.getBooleanAt(5), false);
    }

    @Test
    public void testGetStringAt() throws Exception {
        assertEquals(megaStoreable.getStringAt(6), "string");
    }

    @After
    public void tearDown() {
        DirectoryKiller.delete(testDir.toFile());
    }
}
