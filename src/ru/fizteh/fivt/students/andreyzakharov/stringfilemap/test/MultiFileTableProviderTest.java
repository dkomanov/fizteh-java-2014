package ru.fizteh.fivt.students.andreyzakharov.stringfilemap.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.andreyzakharov.stringfilemap.MultiFileTableProviderFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MultiFileTableProviderTest {
    String root = "test/junit";
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
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        for (String name : names) {
            d.createTable(name);
            assertNull(d.getTable(otherName));
        }
    }

    @Test
    public void testGetTable() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        for (String name : names) {
            d.createTable(name);
            assertNotNull(d.getTable(name));
        }
    }

    @Test
    public void testCreateTableNull() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        exception.expect(IllegalArgumentException.class);
        d.createTable(null);
    }

    @Test
    public void testCreateTableExisting() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        for (String name : names) {
            d.createTable(name);
            assertNull(d.createTable(name));
        }
    }

    @Test
    public void testCreateTable() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        for (String name : names) {
            d.createTable(name);
            assertNotNull(d.getTable(name));
        }
    }

    @Test
    public void testRemoveTableNull() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        exception.expect(IllegalArgumentException.class);
        d.removeTable(null);
    }

    @Test
    public void testRemoveTableNonExistent() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        exception.expect(IllegalStateException.class);
        d.removeTable(otherName);
    }

    @Test
    public void testRemoveTable() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();
        TableProvider d = f.create(root);

        for (String name : names) {
            d.createTable(name);
            d.removeTable(name);
        }
    }
}
