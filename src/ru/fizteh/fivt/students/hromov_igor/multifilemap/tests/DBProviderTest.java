package ru.fizteh.fivt.students.hromov_igor.multifilemap.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBProviderFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DBProviderTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public TableProvider provider;

    @Before
    public void initProvider() throws Exception {
        TableProviderFactory factory = new DBProviderFactory();
        provider = factory.create(folder.newFolder("test").getAbsolutePath());
    }

    @Test (expected = IllegalArgumentException.class)
    public void getNullTable() {
        provider.getTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createNullTable() throws Exception {
        provider.createTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void removeNullTable() throws Exception {
        provider.removeTable(null);
    }

    @Test
    public void createAndGetTable() throws Exception {
        provider.createTable("newTable");
        assertNull(provider.getTable("notExistingTable"));
        assertNotNull(provider.getTable("newTable"));
    }

    @Test (expected = IllegalStateException.class)
    public void removeNotExistingTable() throws Exception {
        provider.removeTable("notExistingTable");
    }

    @Test
    public void createAndRemoveTable() throws Exception {
        provider.createTable("newTable");
        assertNotNull(provider.getTable("newTable"));
        provider.removeTable("newTable");
        assertNull(provider.getTable("newTable"));
    }

}
