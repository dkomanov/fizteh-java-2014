package ru.fizteh.fivt.students.dnovikov.storeable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.TableNotFoundException;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DataBaseProviderTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private TableProviderFactory factory;
    private TableProvider db;
    private File testDirectory;

    public DataBaseProviderTest(TableProviderFactory factory) {
        this.factory = factory;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        new DataBaseProviderFactory()
                }
        });
    }

    @Before
    public void setUp() throws Exception {
        testDirectory = folder.newFolder("db");
        db = factory.create(testDirectory.getAbsolutePath());
    }

    @Test
    public void testGetTable() throws Exception {
        db.createTable("name", Arrays.asList(String.class));
        assertNotNull(db.getTable("name"));
    }

    @Test
    public void testCreateNonExistentTable() throws Exception {
        assertNotNull(db.createTable("table", Arrays.asList(String.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNullName() throws Exception {
        db.createTable(null, Arrays.asList(String.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNullList() throws Exception {
        db.createTable("abc", null);
    }

    @Test
    public void testCreateExistingTable() throws Exception {
        db.createTable("table", Arrays.asList(String.class, Integer.class, Boolean.class));
        assertNull(db.createTable("table", Arrays.asList(String.class, Integer.class, Boolean.class)));
    }

    @Test
    public void testRemoveExistingTable() throws Exception {
        db.createTable("table", Arrays.asList(String.class, Integer.class, Boolean.class));
        db.removeTable("table");
        assertNull(db.getTable("table"));
        assertNotNull(db.createTable("table", Arrays.asList(String.class,
                Integer.class, Boolean.class)));
    }

    @Test(expected = TableNotFoundException.class)
    public void testRemoveNotExistentTable() throws Exception {
        db.removeTable("table");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        db.removeTable(null);
    }

    @Test
    public void testSerializeDeserialize() throws Exception {
        Table table = db.createTable("table", Arrays.asList(
                String.class, Integer.class, Boolean.class, Long.class, Double.class, Float.class, Byte.class));
        List<Object> expected = Arrays.asList("abacaba", Integer.MIN_VALUE, true,
                Long.MAX_VALUE, 3.14, 2.71828f, (byte) 128);
        Storeable original = db.createFor(table, expected);
        String serialized = db.serialize(table, original);
        Storeable deserialized = db.deserialize(table, serialized);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), deserialized.getColumnAt(i));
        }

    }

    @Test
    public void testCreateFor() throws Exception {
        Table table = db.createTable("table", Arrays.asList(
                String.class, Integer.class, Boolean.class, Long.class, Double.class, Float.class, Byte.class));
        List<Object> values = Arrays.asList("abacaba", Integer.MIN_VALUE, true,
                Long.MAX_VALUE, 3.14, 2.71828f, (byte) 128);
        Storeable storeable = db.createFor(table, values);
        for (int i = 0; i < values.size(); i++) {
            assertEquals(values.get(i), storeable.getColumnAt(i));
        }
    }

    @Test
    public void testCreateForEmpty() throws Exception {
        Table table = db.createTable("table", Arrays.asList(
                String.class, Integer.class, Boolean.class, Long.class, Double.class, Float.class, Byte.class));
        Storeable actual = db.createFor(table);
        try {
            actual.setColumnAt(0, new String());
            actual.setColumnAt(1, new Integer(0));
            actual.setColumnAt(2, new Boolean(false));
            actual.setColumnAt(3, new Long(0));
            actual.setColumnAt(4, new Double(0));
            actual.setColumnAt(5, new Float(0));
            actual.setColumnAt(6, new Byte((byte) 0));
        } catch (ColumnFormatException e) {
            fail("Caught ColumnFormatException where not expected: " + e);
        }
    }

    @Test
    public void testGetTableNames() throws Exception {
        db.createTable("table 1", Arrays.asList(String.class, Integer.class, Boolean.class));
        db.createTable("table 2", Arrays.asList(Double.class, String.class, Long.class));
        List<String> expected = Arrays.asList("table 1", "table 2");
        List<String> real = db.getTableNames();
        Collections.sort(expected);
        Collections.sort(real);
        assertEquals(expected, real);
    }
}
