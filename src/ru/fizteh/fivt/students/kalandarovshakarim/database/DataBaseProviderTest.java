/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.database;

import ru.fizteh.fivt.students.kalandarovshakarim.database.*;
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
import ru.fizteh.fivt.students.kalandarovshakarim.database.DataBaseProvider;
import ru.fizteh.fivt.students.kalandarovshakarim.database.DataBaseProviderFactory;

/**
 *
 * @author shakarim
 */
public class DataBaseProviderTest {

    private TableProvider instance;
    private final String testDirestory = System.getProperty("java.io.tmpdir");
    private final int tableCount = 25;
    private Path dbPath;

    @Before
    public void setUp() throws IOException {
        dbPath = Paths.get(testDirestory, "test.db.dir");
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

    /**
     * Test of getTable method, of class DataBaseProvider.
     */
    @Test
    public void testGetTable() {
        System.out.println("getTable");
        for (int order = 0; order < tableCount; order++) {
            String name = "table" + order;
            Table result = instance.getTable(name);
            assertNotNull(result);
        }
    }

    /**
     * Test of getTable method, of class DataBaseProvider.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithInvalidName() {
        System.out.println("getTable with invalid name");
        instance.getTable("123////\\?");
    }

    /**
     * Test of getTable method, of class DataBaseProvider.
     */
    @Test
    public void testGetTableIfNotExists() {
        System.out.println("getTable if table not exists");
        for (int order = 0; order < tableCount; order++) {
            String name = "таблица" + order;
            Table result = instance.getTable(name);
            assertNull(result);
        }
    }

    /**
     * Test of getTable method, of class DataBaseProvider.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetTableIfNameIsNull() {
        System.out.println("getTable if name null");
        instance.getTable(null);
    }

    /**
     * Test of createTable method, of class DataBaseProvider.
     */
    @Test
    public void testCreateTable() {
        System.out.println("createTable if not exists");
        for (int order = 0; order < tableCount; order++) {
            String name = "таблица" + order;
            Table result = instance.createTable(name);
            assertNotNull(result);
        }
    }

    /**
     * Test of createTable method, of class DataBaseProvider.
     */
    @Test
    public void testCreateTableIfExists() {
        System.out.println("createTable if exists");
        String name = "table" + 0;
        Table result = instance.createTable(name);
        assertNull(result);
    }

    /**
     * Test of createTable method, of class DataBaseProvider.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() {
        System.out.println("createTable null");
        instance.createTable(null);
    }

    /**
     * Test of removeTable method, of class DataBaseProvider.
     */
    @Test
    public void testRemoveTable() {
        System.out.println("removeTable if exists");
        for (int order = 0; order < tableCount; order++) {
            String name = "table" + order;
            instance.removeTable(name);
        }
    }

    /**
     * Test of removeTable method, of class DataBaseProvider.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        System.out.println("removeTable null");
        instance.removeTable(null);
    }

    /**
     * Test of removeTable method, of class DataBaseProvider.
     */
    @Test(expected = IllegalStateException.class)
    public void testRemoveTableIfNotExists() {
        System.out.println("removeTable if not exists");
        String name = "таблица";
        instance.removeTable(name);
    }

    /**
     * Test of listTables method, of class DataBaseProvider.
     */
    @Test
    public void testListTables() {
        System.out.println("listTables");
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
        System.out.println("test load");
        try {
            Files.createFile(dbPath.resolve("nondirectory.dat"));
        } catch (IOException e) {
            // Nothing.
        }
        new DataBaseProviderFactory().create(dbPath.toString());
    }
}
