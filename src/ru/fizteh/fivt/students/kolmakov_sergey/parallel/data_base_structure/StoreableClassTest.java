package ru.fizteh.fivt.students.kolmakov_sergey.parallel.data_base_structure;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kolmakov_sergey.parallel.data_base_exceptions.DatabaseCorruptedException;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoreableClassTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DataBaseTestDirectory");
    private TableProvider provider = new TableManager(testDir.toString());
    private Storeable megaStoreable;
    private Table tableWhichHoldsAllTypes;

    private final List<Object> listWhichHoldsAllValues
            = Arrays.asList(1, (long) 2, (byte) 3, 7.62f, 3.14, false, "string");

    private List listWhichHoldsAllTypes = new ArrayList<>(Arrays.asList(Integer.class, Long.class, Byte.class,
            Float.class, Double.class, Boolean.class, String.class));

    @Before
    public void setUp() throws DatabaseCorruptedException {
        testDir.toFile().mkdir();

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
        deleteRecursively(testDir.toFile());
    }

    private static void deleteRecursively(File directory) {
        if (directory.isDirectory()) {
            try {
                for (File currentFile : directory.listFiles()) {
                    deleteRecursively(currentFile);
                }
            } catch (NullPointerException e) {
                System.out.println("Error while recursive deleting directory.");
            }
            directory.delete();
        } else {
            directory.delete();
        }
    }
}
