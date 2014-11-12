package ru.fizteh.fivt.students.dnovikov.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.TableNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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

    @Test
    public void initializationNewDirectory() {
        new DataBaseProvider(tmpFolder.getRoot().toString());
    }

    @Test(expected = LoadOrSaveException.class)
    public void initializationExistingDirectoryThrowsException() {
        new DataBaseProvider(tmpFolder.getRoot().toPath().resolve("test").toString());
    }

    @Test(expected = LoadOrSaveException.class)
    public void initializationNotDirectoryThrowsException() throws IOException {
        File tmpFile = tmpFolder.getRoot().toPath().resolve("test").toFile();
        tmpFile.createNewFile();
        new DataBaseProvider(tmpFile.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTableThrowsException() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableThrowsException() {
        provider.createTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void removeNullTableThrowsException() {
        provider.removeTable(null);
    }

    @Test
    public void createAndGetTable() {
        provider.createTable("newTable");
        assertNull(provider.getTable("notExistingTable"));
        assertNotNull(provider.getTable("newTable"));
    }

    @Test (expected = TableNotFoundException.class)
    public void removeNotExistingTableThrowsException() {
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
    public void creationExistingTable() {
        assertNotNull(provider.createTable("newTable"));
        assertNull(provider.createTable("newTable"));
    }

}