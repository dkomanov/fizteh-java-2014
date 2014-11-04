package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableProviderFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MultiFileTableProviderTest {
    String root = "/home/norrius/Apps/fizteh-java-2014/test/junit";
    String[] names = {"test-db", "null", "database-01", "*[::]<>|\\?"};
    String otherName = "*()doesn't match any other name";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetTableNull() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        exception.expect(IllegalArgumentException.class);
        d.getTable(null);
    }

    @Test
    public void testGetTableNonExistent() throws Exception {
        TableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        for (String name : names) {
            List<Class<?>> classes = new ArrayList<>();
            classes.add(String.class);
            d.createTable(name, classes);
            assertNull(d.getTable(otherName));
        }
    }

    @Test
    public void testGetTable() throws Exception {
        TableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        for (String name : names) {
            d.createTable(name, null);
            assertNotNull(d.getTable(name));
        }
    }

    @Test
    public void testCreateTableNull() throws Exception {
        TableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        exception.expect(IllegalArgumentException.class);
        d.createTable(null, null);
    }

    @Test
    public void testCreateTableExisting() throws Exception {
        TableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        for (String name : names) {
            d.createTable(name, null);
            assertNull(d.createTable(name, null));
        }
    }

    @Test
    public void testCreateTable() throws Exception {
        TableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        for (String name : names) {
            d.createTable(name, null);
            assertNotNull(d.getTable(name));
        }
    }

    @Test
    public void testRemoveTableNull() throws Exception {
        TableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        exception.expect(IllegalArgumentException.class);
        d.removeTable(null);
    }

    @Test
    public void testRemoveTableNonExistent() throws Exception {
        TableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        exception.expect(IllegalStateException.class);
        d.removeTable(otherName);
    }

    @Test
    public void testRemoveTable() throws Exception {
        TableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        for (String name : names) {
            d.createTable(name, null);
            d.removeTable(name);
        }
    }
}
