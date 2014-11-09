package ru.fizteh.fivt.students.dnovikov.junit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.TableNotFoundException;
import java.io.IOException;
import static org.junit.Assert.*;
public class DataBaseProviderTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    public TableProvider provider;

    @Before
    public void setUp() throws IOException {
        TableProviderFactory factory = new DataBaseProviderFactory();
        provider = factory.create(tmpFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTable() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTable() {
        provider.createTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void removeNullTable() {
        provider.removeTable(null);
    }

    @Test
    public void createAndGetTable() {
        provider.createTable("newTable");
        assertNull(provider.getTable("notExistingTable"));
        assertNotNull(provider.getTable("newTable"));
    }

    @Test (expected = TableNotFoundException.class)
    public void removeNotExistingTable() {
        provider.removeTable("notExistingTable");
    }

    @Test
    public void createAndRemoveTable() {
        assertNotNull(provider.createTable("newTable"));
        assertNotNull(provider.getTable("newTable"));
        provider.removeTable("newTable");
        assertNull(provider.getTable("newTable"));
    }

    @Test
    public void doubleTableCreation() {
        assertNotNull(provider.createTable("newTable"));
        assertNull(provider.createTable("newTable"));
    }


}