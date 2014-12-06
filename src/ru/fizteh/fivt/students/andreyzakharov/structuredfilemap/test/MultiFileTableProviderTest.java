package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.test;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MultiFileTableProviderTest {
    String[] names = {"test-db-provider", "null", "database-01", "*[::]<>|\\?"};
    String otherName = "*()doesn't match any other name";
    static List<Class<?>> signature;

    MultiFileTableProviderFactory f;
    TableProvider d;

    @BeforeClass
    public static void setUp() {
        signature = Arrays.asList(Integer.class, String.class);
    }

    @Before
    public void preRun() {
        f = new MultiFileTableProviderFactory();
        d = f.create(MultiFileTableTest.rootName);
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

    @AfterClass
    public static void tearDown() {
        try {
            Files.deleteIfExists(Paths.get(MultiFileTableTest.rootName));
        } catch (IOException e) {
            //
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
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

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws Exception {
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

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws Exception {
        d.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableNonExistent() throws Exception {
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
