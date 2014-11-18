/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

/**
 *
 * @author shakarim
 */
public class DataBaseProviderTest {

    private TableProvider instance;
    private static final String TEST_DIRECTORY = System.getProperty("java.io.tmpdir");
    private final int tableCount = 25;
    private Path dbPath;

    @Before
    public void setUp() throws IOException {
        dbPath = Paths.get(TEST_DIRECTORY, "test.db.dir");
        if (!Files.exists(dbPath)) {
            Files.createDirectory(dbPath);
        }
        for (int order = 0; order < tableCount; ++order) {
            Files.createDirectory(dbPath.resolve("table" + order));
        }
        instance = new DataBaseProviderFactory().create(dbPath.toString());
    }

    @After
    public void tearDown() throws IOException {
        String[] list = dbPath.toFile().list();
        for (String file : list) {
            Files.delete(dbPath.resolve(file));
        }
        Files.delete(dbPath);
    }

    @Test
    public void testGetTable() {
        for (int order = 0; order < tableCount; order++) {
            String name = "table" + order;
            Table result = instance.getTable(name);
            assertNotNull(result);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithInvalidName() {
        instance.getTable("new/table");
    }

    @Test
    public void testGetTableIfNotExists() {
        for (int order = 0; order < tableCount; order++) {
            String name = "таблица" + order;
            Table result = instance.getTable(name);
            assertNull(result);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableIfNameIsNull() {
        instance.getTable(null);
    }

    @Test
    public void testCreateTable() {
        for (int order = 0; order < tableCount; order++) {
            String name = "таблица" + order;
            Table result = instance.createTable(name);
            assertNotNull(result);
        }
    }

    @Test
    public void testCreateTableIfExists() {
        String name = "table" + 0;
        Table result = instance.createTable(name);
        assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCalledWithNullArgument() {
        instance.createTable(null);
    }

    @Test
    public void testRemoveTable() {
        for (int order = 0; order < tableCount; order++) {
            String name = "table" + order;
            assertNotNull(instance.getTable(name));
            instance.removeTable(name);
            assertNull(instance.getTable(name));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveCalledWithNullArgument() {
        instance.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableIfNotExists() {
        String name = "таблица";
        instance.removeTable(name);
    }

    @Test
    public void testListTables() {
        List<String> expResult = new ArrayList<>();
        List<String> result = ((DataBaseProvider) instance).listTables();

        for (int order = 0; order < tableCount; order++) {
            String name = "table" + order;
            expResult.add(name);
        }

        Collections.sort(expResult);
        Collections.sort(result);

        assertEquals(expResult, result);
    }

    /**
     * Test of load method, of class DataBaseProvider.
     * I called it testConstructor because load is private and Constructor
     * uses load.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testContsructor() {
        try {
            Files.createFile(dbPath.resolve("nondirectory.dat"));
        } catch (IOException e) {
            // Nothing.
        }
        new DataBaseProviderFactory().create(dbPath.toString());
    }
}
