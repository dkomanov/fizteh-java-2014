package ru.fizteh.fivt.students.dnovikov.junit;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.TableNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DataBaseProviderTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    public DataBaseProvider provider;

    @Before
    public void setUp() throws IOException {
        TableProviderFactory factory = new DataBaseProviderFactory();
        provider = (DataBaseProvider) factory.create(tmpFolder.newFolder().getAbsolutePath());
    }

    @Test
    public void testLoadFromEmptyDirectory() throws IOException {
        provider.loadTables();
        assertEquals(0, provider.showTable().size());
    }
    @Test
    public void initializationNewDirectory() throws IOException {
        new DataBaseProvider(tmpFolder.getRoot().toString());
    }

    @Test(expected = LoadOrSaveException.class)
    public void initializationExistingDirectoryThrowsException() throws IOException {
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
    public void createNullTableThrowsException() throws IOException {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTableThrowsException() {
        provider.removeTable(null);
    }

    @Test
    public void createAndGetTable() throws IOException {
        provider.createTable("newTable");
        assertNull(provider.getTable("notExistingTable"));
        assertNotNull(provider.getTable("newTable"));
    }

    @Test(expected = TableNotFoundException.class)
    public void removeNotExistingTableThrowsException() {
        provider.removeTable("notExistingTable");
    }


    @Test
    public void createAndRemoveTable() throws IOException {
        assertNotNull(provider.createTable("newTable"));
        assertNotNull(provider.getTable("newTable"));
        provider.removeTable("newTable");
        assertNull(provider.getTable("newTable"));
    }

    @Test
    public void testCurrentTable() throws IOException {
        DataBaseTable table = (DataBaseTable) provider.createTable("table");
        provider.setCurrentTable(table);
        assertEquals(table, provider.getCurrentTable());
    }

    @Test
    public void removeCurrentTable() throws IOException {
        DataBaseTable table = (DataBaseTable) provider.createTable("newTable");
        provider.setCurrentTable(table);
        provider.removeTable("newTable");
        assertNull(provider.getTable("newTable"));
    }

    @Test
    public void creationExistingTable() throws IOException {
        assertNotNull(provider.createTable("newTable"));
        assertNull(provider.createTable("newTable"));
    }

    @Test
    public void testShowTable() throws IOException {
        provider.createTable("table1");
        provider.createTable("table2");
        provider.setCurrentTable(provider.getTable("table1"));
        provider.getCurrentTable().put("key", "value");
        List<Pair<String, Integer>> expected = new ArrayList<>();
        expected.add(new Pair<>("table1", 1));
        expected.add(new Pair<>("table2", 0));
        assertTrue(provider.showTable().containsAll(expected));
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        provider.createTable("table1");
        provider.createTable("table2");
        DataBaseTable table1 = (DataBaseTable) provider.getTable("table1");
        provider.setCurrentTable(table1);
        table1.put("key", "value");
        table1.commit();
        provider.saveTable();
        provider.loadTables();
        provider.setCurrentTable(provider.getTable("table1"));
        assertEquals("value", provider.getCurrentTable().get("key"));
        provider.setCurrentTable(provider.getTable("table2"));
        assertEquals(0, provider.getCurrentTable().size());
    }
}
