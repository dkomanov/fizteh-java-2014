package ru.fizteh.fivt.students.moskupols.storeable;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class StringBackedStructuredTableProviderTest {
    private TableProvider provider;
    private StringBackedTableProviderFactory providerFactory;
    private String providerDirectory;

    @Before
    public void setUp() throws Exception {
        providerFactory = new StringBackedTableProviderFactory();
        providerDirectory = Files.createTempDirectory("ProviderTest").toAbsolutePath().toString();
        provider = providerFactory.create(providerDirectory);
    }

    @Test
    public void testCreateTwice() throws Exception {
        assertNull(provider.getTable("tab"));

        Table t = provider.createTable("tab", Arrays.asList(Integer.class, String.class));
        assertNotNull(t);

        Table t2 = provider.createTable("tab", Arrays.asList(String.class));
        assertNull(t2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBothNull() throws Exception {
        provider.createTable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNameNull() throws Exception {
        provider.createTable(null, Arrays.asList(Integer.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTypesNull() throws Exception {
        provider.createTable("some", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTrashName() throws Exception {
        provider.createTable("asdf/gr", Arrays.asList(Integer.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        provider.getTable(null);
    }

    @Test
    public void testRemove() throws Exception {
        Table t = provider.createTable("tab", Arrays.asList(Integer.class));
        t.put("0", provider.createFor(t));
        t.commit();

        provider.removeTable("tab");

        assertNull(provider.getTable("tab"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        provider.removeTable(null);
    }

    @Test()
    public void testListNames() throws Exception {
        assertEquals(new ArrayList<String>(), provider.getTableNames());

        final TableProvider another = providerFactory.create(providerDirectory);

        another.createTable("tab", Arrays.asList(Integer.class));
        assertEquals(Arrays.asList("tab"), provider.getTableNames());

        another.removeTable("tab");
        assertEquals(new ArrayList<String>(), provider.getTableNames());
    }
}
