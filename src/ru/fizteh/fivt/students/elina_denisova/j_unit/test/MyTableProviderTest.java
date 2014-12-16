package ru.fizteh.fivt.students.elina_denisova.j_unit.test;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.elina_denisova.j_unit.MyTableProviderFactory;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MyTableProviderTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public TableProvider provider;

    @Before
    public void initProvider() throws IOException {
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder().getAbsolutePath());
    }

    @Test (expected = IllegalArgumentException.class)
    public void getNullTable() {
        provider.getTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
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

    @Test (expected = IllegalStateException.class)
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