package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class TableManagerTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DataBaseTestDirectory");
    private final String directoryName = "test";
    private final Path directoryPath = testDir.resolve(directoryName);
    private final String tableName = "table";

    @Before
    public void setUp() {
        testDir.toFile().mkdir();
    }

    @Test
    public void testTableManagerConstructorUnexistentDirectory() {
        new TableManager(directoryPath.toString());
    }

    @Test
    public void testTableManagerConstructorExistentDirectory() {
        directoryPath.toFile().mkdir();
        new TableManager(directoryPath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerConstructorThrowsExceptionWhenArgumentIsFile() throws IOException {
        directoryPath.toFile().createNewFile();
        new TableManager(directoryPath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionInPathGetMethod() {
        new TableManager("\0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionWhenThereAreBadFilesInRoot() throws IOException {
        directoryPath.toFile().mkdir();
        directoryPath.resolve("fileName").toFile().createNewFile();
        new TableManager(directoryPath.toString());
    }

    @Test
    public void testTableManagerCreatedForDirectoryWithExistientTable()
            throws IOException {
        directoryPath.toFile().mkdir();
        directoryPath.resolve(tableName).toFile().mkdir();
        TableProvider test = new TableManager(directoryPath.toString());
        assertNotEquals(null, test.getTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionWhenArgumentIsNull() {
        TableProvider test = new TableManager(directoryPath.toString());
        test.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForWrongTableName1() {
        TableProvider test = new TableManager(directoryPath.toString());
        test.createTable("badName.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForWrongTableName2() {
        TableProvider test = new TableManager(directoryPath.toString());
        test.createTable("bad/name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForWrongTableName3() {
        TableProvider test = new TableManager(directoryPath.toString());
        test.createTable("bad\\name");
    }

    @Test
    public void testCreateTableMakesNewTableProperly() {
        TableProvider test = new TableManager(directoryPath.toString());
        assertNotEquals(null, test.createTable(tableName));
        assertTrue(directoryPath.resolve(tableName).toFile().exists());
    }

    @Test
    public void testCreateTableMakesExistentTableProperly() {
        directoryPath.resolve(tableName).toFile().mkdirs();
        TableProvider test = new TableManager(directoryPath.toString());
        assertEquals(null, test.createTable(tableName));
        assertTrue(directoryPath.resolve(tableName).toFile().exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionWhenArgumentIsNull() {
        TableProvider test = new TableManager(directoryPath.toString());
        test.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionWhenNameIsBad() {
        TableProvider test = new TableManager(directoryPath.toString());
        test.getTable("ab/cd");
    }

    @Test
    public void testGetTableCalledForNonexistentTable() {
        TableProvider test = new TableManager(directoryPath.toString());
        assertEquals(null, test.getTable(tableName));
    }

    @Test
    public void testGetTableCalledForExistentTable() {
        TableProvider test = new TableManager(directoryPath.toString());
        assertNotEquals(null, test.createTable(tableName));
        assertNotEquals(null, test.getTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionWhenArgumentIsNull() {
        TableProvider test = new TableManager(directoryPath.toString());
        test.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionWhenNameIsBad() {
        TableProvider test = new TableManager(directoryPath.toString());
        //Wrong table name contains '.', '/' or '\'.
        test.removeTable("ab\\cd");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableThrowsExceptionWhenTableNotExists() {
        TableProvider test = new TableManager(directoryPath.toString());
        test.removeTable(tableName);
    }

    @Test
    public void testRemoveTableCalledForTableWithData() {
        TableProvider test = new TableManager(directoryPath.toString());
        assertNotEquals(null, test.createTable(tableName));
        Table testTable = test.getTable(tableName);
        assertNotEquals(null, testTable);
        assertEquals(null, testTable.put("key", "value"));
        assertEquals(null, testTable.put("key2", "value"));
        testTable.commit();
        test.removeTable(tableName);
    }

    @After
    public void tearDown() {
        deleteRecursively(testDir.toFile());
    }

    private static void deleteRecursively(File directory) {
        if (directory.isDirectory()) {
            try {
                for (File currentFile : directory.listFiles()) {
                    deleteRecursively(currentFile);
                }
            } catch (NullPointerException e) {
                System.out.println("Error while recursive deleting directory.");
            }
            directory.delete();
        } else {
            directory.delete();
        }
    }
}
