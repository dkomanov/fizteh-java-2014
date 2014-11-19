package ru.fizteh.fivt.students.pavel_voropaev.project.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.TableDoesNotExistException;
import ru.fizteh.fivt.students.pavel_voropaev.project.database.Database;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class DatabaseTest {
    private Path databasePath;
    private final String databaseDirectory = "test";
    private final String tableName = "testTable";
    private final String fileName = "testFile";
    private final String stringWithBannedSymbols = "Где\\Моя|Любимая<Папка?";

    @Rule
    public TemporaryFolder testDirectory = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        databasePath = testDirectory.getRoot().toPath().resolve(databaseDirectory);
    }

    @Test
    public void databaseInitializationForNewDirectory() {
        new Database(databasePath.toString());
    }

    @Test
    public void databaseInitializationForExistingDirectory() {
        databasePath.toFile().mkdir();
        new Database(databasePath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void databaseInitializationForNotDirectory() throws IOException {
        databasePath.toFile().createNewFile();
        new Database(databasePath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void databaseInitializationForDirectoryWithGarbage() throws IOException {
        databasePath.toFile().mkdir();
        databasePath.resolve(fileName).toFile().createNewFile();
        new Database(databasePath.toString());
    }

    @Test
    public void databaseInitializationForDirectoryWithTables() throws IOException {
        databasePath.toFile().mkdir();
        databasePath.resolve(tableName).toFile().mkdir();
        new Database(databasePath.toString());
        TableProvider test = new Database(databasePath.toString());
        assertNotEquals(null, test.getTable(tableName));
    }

    @Test
    public void getTableForNotExistingTable() {
        TableProvider test = new Database(databasePath.toString());
        assertEquals(null, test.getTable(tableName));
    }

    @Test
    public void getTableForExistingTable() {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(tableName);
        assertNotEquals(null, test.getTable(tableName));
        assertEquals(tableName, test.getTable(tableName).getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableForWrongNamedTable() {
        TableProvider test = new Database(databasePath.toString());
        test.getTable(stringWithBannedSymbols);
    }

    @Test
    public void createTableForNotExistingTable() {
        TableProvider test = new Database(databasePath.toString());
        assertNotEquals(null, test.createTable(tableName));
        assertTrue(databasePath.resolve(tableName).toFile().exists());
    }

    @Test
    public void testCreateTableForExistingTable() {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(tableName);
        assertEquals(null, test.createTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableForWrongNamedTable() {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(stringWithBannedSymbols);
    }

    @Test
    public void removeTableForExistingTable() throws IOException {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(tableName);
        Table testTable = test.getTable(tableName);
        testTable.put("key", "val");
        testTable.put("key2", "v");
        testTable.commit();
        test.removeTable(tableName);
    }

    @Test(expected = IllegalStateException.class)
    public void removeTableForNotExistingTable() {
        TableProvider test = new Database(databasePath.toString());
        test.removeTable(tableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableForWrongTableName() {
        TableProvider test = new Database(databasePath.toString());
        test.removeTable(stringWithBannedSymbols);
    }

    //Tests for unofficial metods in TableProvider

    @Test
    public void setActiveTableForExistingTable() {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(tableName);
        test.setActiveTable(tableName);
        assertEquals(test.getActiveTable().getName(), tableName);
    }

    @Test(expected = TableDoesNotExistException.class)
    public void setActiveTableForNotExistingTable() {
        TableProvider test = new Database(databasePath.toString());
        test.setActiveTable(tableName);
    }

    @Test
    public void testGetTablesList() {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(tableName);
        assertEquals(test.getTablesList().get(0), tableName);
    }
}

