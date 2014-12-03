package ru.fizteh.fivt.students.ryad0m.junit.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.ryad0m.junit.BadFormatException;
import ru.fizteh.fivt.students.ryad0m.junit.MyTableProviderFactory;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class MyTableProviderTest {
    @Rule
    public TemporaryFolder myTmpFolder = new TemporaryFolder();

    public TableProvider provider;

    @Before
    public void initProvider() throws IOException, BadFormatException {
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(myTmpFolder.newFolder("db").getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTable() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTable() throws IOException, BadFormatException {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTable() {
        provider.removeTable(null);
    }

    @Test
    public void createAndGetTable() throws IOException, BadFormatException {
        provider.createTable("newTable");
        assertNull(provider.getTable("notExistingTable"));
        assertNotNull(provider.getTable("newTable"));
    }

    @Test(expected = IllegalStateException.class)
    public void removeNotExistingTable() {
        provider.removeTable("notExistingTable");
    }

    @Test
    public void createAndRemoveTable() throws IOException, BadFormatException {
        assertNotNull(provider.createTable("newTable"));
        assertNotNull(provider.getTable("newTable"));
        provider.removeTable("newTable");
        assertNull(provider.getTable("newTable"));
    }

    @Test
    public void doubleTableCreation() throws IOException, BadFormatException {
        assertNotNull(provider.createTable("newTable"));
        assertNull(provider.createTable("newTable"));
    }
}
