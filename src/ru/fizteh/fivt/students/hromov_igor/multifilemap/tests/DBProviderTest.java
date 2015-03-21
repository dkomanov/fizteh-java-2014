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
    public TemporaryFolder Folder = new TemporaryFolder();

    public TableProvider Provider;

    @Before
    public void initProvider() throws Exception {
        TableProviderFactory factory = new DBProviderFactory();
        Provider = factory.create(Folder.newFolder("test").getAbsolutePath());
    }

    @Test (expected = IllegalArgumentException.class)
    public void getNullTable() {
        Provider.getTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createNullTable() throws Exception{
        Provider.createTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void removeNullTable() throws Exception{
        Provider.removeTable(null);
    }

    @Test
    public void createAndGetTable() throws Exception{
        Provider.createTable("newTable");
        assertNull(Provider.getTable("notExistingTable"));
        assertNotNull(Provider.getTable("newTable"));
    }

    @Test (expected = IllegalStateException.class)
    public void removeNotExistingTable() throws Exception{
        Provider.removeTable("notExistingTable");
    }

    @Test
    public void createAndRemoveTable() throws Exception{
        Provider.createTable("newTable");
        assertNotNull(Provider.getTable("newTable"));
        Provider.removeTable("newTable");
        assertNull(Provider.getTable("newTable"));
    }


}