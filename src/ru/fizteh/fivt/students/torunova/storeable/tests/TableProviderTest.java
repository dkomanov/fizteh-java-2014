package ru.fizteh.fivt.students.torunova.storeable.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.torunova.storeable.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.storeable.StoreableType;
import ru.fizteh.fivt.students.torunova.storeable.TableWrapper;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TableProviderTest {
    DatabaseWrapper db;
    File testDirectory;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Before
    public void setUp() throws Exception {
        testDirectory = folder.newFolder("db");
        db = new DatabaseWrapper(testDirectory.getAbsolutePath());
    }
    @Test
    public void testGetTable() throws Exception {
        db.createTable("table", Arrays.asList(String.class, Integer.class, Boolean.class));
        File table = new File(testDirectory, "table");
        TableWrapper t = new TableWrapper(table.getAbsolutePath(), db,
                String.class, Integer.class, Boolean.class);
        assertEquals(t, db.getTable("table"));
    }

    @Test
    public void testCreateNotExistingTable() throws Exception {
        File table = new File(testDirectory, "table");
        TableWrapper t = new TableWrapper(table.getAbsolutePath(), db,
                String.class, Integer.class, Boolean.class);
        assertEquals(t, db.createTable("table", Arrays.asList(String.class, Integer.class, Boolean.class)));
    }

    @Test
    public void testCreateExistingTable() throws Exception {
        db.createTable("table", Arrays.asList(String.class, Integer.class, Boolean.class));
        assertEquals(null, db.createTable("table", Arrays.asList(String.class, Integer.class, Boolean.class)));
    }

    @Test
    public void testRemoveExistingTable() throws Exception {
        db.createTable("table", Arrays.asList(String.class, Integer.class, Boolean.class));
        db.removeTable("table");
        assertNotEquals(null, db.createTable("table", Arrays.asList(String.class,
                Integer.class, Boolean.class)));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingTable() throws Exception {
        db.removeTable("table");
    }

    @Test
    public void testDeserialize() throws Exception {
        TableWrapper table = (TableWrapper) db.createTable("table", Arrays.asList(
                String.class, Integer.class, Boolean.class));
        StoreableType value = (StoreableType) db.createFor(table, Arrays.asList("String value", 42, false));
        assertEquals(value, db.deserialize(table, "[\"String value\", 42, false]"));
    }

    @Test
    public void testSerialize() throws Exception {
        TableWrapper table = (TableWrapper) db.createTable("table", Arrays.asList(
                String.class, Integer.class, Boolean.class));
        StoreableType value = (StoreableType) db.createFor(table, Arrays.asList("String value", 42, false));
        assertEquals("[\"String value\", 42, false]", db.serialize(table, value));
    }

    @Test
    public void testCreateFor() throws Exception {
        TableWrapper table = (TableWrapper) db.createTable("table", Arrays.asList(
                String.class, Integer.class, Boolean.class));
        StoreableType value = new StoreableType(String.class, Integer.class, Boolean.class);
        value.setColumnAt(0, "Some string");
        value.setColumnAt(1, 42);
        value.setColumnAt(2, false);
        assertEquals(value, db.createFor(table, Arrays.asList("Some string", 42, false)));
    }

    @Test
    public void testCreateFor1() throws Exception {
        TableWrapper table = (TableWrapper) db.createTable("table", Arrays.asList(
                String.class, Integer.class, Boolean.class));
        StoreableType value = new StoreableType(String.class, Integer.class, Boolean.class);
        assertEquals(value, db.createFor(table));
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
