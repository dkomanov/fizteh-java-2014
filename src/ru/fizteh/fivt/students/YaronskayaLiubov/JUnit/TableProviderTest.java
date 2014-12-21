package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.Parameterized;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.*;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luba_yaronskaya on 07.11.14.
 */
@RunWith(Parameterized.class)
public class TableProviderTest {
    private TableProviderFactory factory;
    private TableProvider provider;
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUpProvider() throws IOException {
        provider = factory.create(testFolder.newFolder().getCanonicalPath());
    }

    public TableProviderTest(TableProviderFactory factory) {
        this.factory = factory;

    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(new Object[][]{
                {new JUnitTableProviderFactory()}
        });

    }

    @Test
    public void testGetExistentTable() throws Exception {
        provider.createTable("new table");
        assertNotNull(provider.getTable("new table"));
    }

    @Test
    public void testGetNonexistentTable() throws Exception {
        assertNull(provider.getTable("nonexistent table"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableIllegalName() throws Exception {
        provider.getTable("");
    }

    @Test
    public void testCreateTableNew() throws Exception {
        assertNotNull(provider.createTable("new table"));
    }

    @Test
    public void testCreateTableExisting() throws Exception {
        provider.createTable("new table");
        assertNull(provider.createTable("new table"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws Exception {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableIllegalName() throws Exception {
        provider.createTable("");
    }

    @Test
    public void testRemoveExistentTable() {
        assertNotNull(provider.createTable("table"));
        provider.removeTable("table");
        assertNull(provider.getTable("table"));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNonexistentTable() throws Exception {
        provider.removeTable("nonexistent table");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() {
        provider.removeTable(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableIllegalName() throws Exception {
        provider.removeTable("");
    }
}

