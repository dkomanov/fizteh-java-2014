package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class JUnitTableProviderTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public JUnitTableProvider provider;

    @Before
    public void initProvider() throws IOException {
        JUnitTableProviderFactory factory = new JUnitTableProviderFactory();
        provider = factory.create(tempFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTable() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTable() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTable() {
        provider.removeTable(null);
    }

    @Test
    public void createAndGetTable() {
        provider.createTable("newTable");
        assertNull(provider.getTable("notExistingTable"));
        assertNotNull(provider.getTable("newTable"));
    }

    @Test(expected = IllegalStateException.class)
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
