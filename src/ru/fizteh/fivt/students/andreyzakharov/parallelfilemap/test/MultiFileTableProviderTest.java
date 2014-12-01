package ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.test;

import org.junit.*;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.MultiFileTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MultiFileTableProviderTest {
    String root = "test/junit-parallel";
    String[] names = {"test-db-provider", "null", "database-01", "*[::]<>|\\?"};
    String otherName = "*()doesn't match any other name";
    static List<Class<?>> signature;

    MultiFileTableProviderFactory f;
    TableProvider d;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUp() {
        signature = Arrays.asList(Integer.class, String.class);
    }

    @Before
    public void preRun() {
        f = new MultiFileTableProviderFactory();
        d = f.create(root);
    }

    @After
    public void postRun() {
        for (String name : names) {
            try {
                d.removeTable(name);
            } catch (IllegalStateException | IOException e) {
                //
            }
        }
    }

    @Test
    public void testGetTableNull() throws Exception {
        exception.expect(IllegalArgumentException.class);
        d.getTable(null);
    }

    @Test
    public void testGetTableNonExistent() throws Exception {
        for (String name : names) {
            List<Class<?>> classes = new ArrayList<>();
            classes.add(String.class);
            d.createTable(name, classes);
            assertNull(d.getTable(otherName));
        }
    }

    @Test
    public void testGetTable() throws Exception {
        for (String name : names) {
            d.createTable(name, signature);
            assertNotNull(d.getTable(name));
        }
    }

    @Test
    public void testCreateTableNull() throws Exception {
        exception.expect(IllegalArgumentException.class);
        d.createTable(null, null);
    }

    @Test
    public void testCreateTableExisting() throws Exception {
        for (String name : names) {
            d.createTable(name, signature);
            assertNull(d.createTable(name, signature));
        }
    }

    @Test
    public void testCreateTable() throws Exception {
        for (String name : names) {
            d.createTable(name, signature);
            assertNotNull(d.getTable(name));
        }
    }

    @Test
    public void testRemoveTableNull() throws Exception {
        exception.expect(IllegalArgumentException.class);
        d.removeTable(null);
    }

    @Test
    public void testRemoveTableNonExistent() throws Exception {
        exception.expect(IllegalStateException.class);
        d.removeTable(otherName);
    }

    @Test
    public void testRemoveTable() throws Exception {
        for (String name : names) {
            d.createTable(name, signature);
            d.removeTable(name);
        }
    }
}
