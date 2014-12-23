package ru.fizteh.fivt.students.pavel_voropaev.project.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.TableDoesNotExistException;
import ru.fizteh.fivt.students.pavel_voropaev.project.database.Database;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DatabaseTest {
    private Path databasePath;
    private final String databaseDirectory = "test";
    private final String tableName = "testTable";
    private final String fileName = "testFile";
    private final String stringWithBannedSymbols = "Моя<Папка";
    private List<Class<?>> signature;

    @Rule
    public TemporaryFolder testDirectory = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        databasePath = testDirectory.getRoot().toPath().resolve(databaseDirectory);
        signature = Arrays.asList(Integer.class, String.class); // Default signature.
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
        TableProvider test = new Database(databasePath.toString());
        test.createTable(tableName, signature);
        TableProvider testInit = new Database(databasePath.toString());
        assertEquals(tableName, testInit.getTable(tableName).getName());

    }

    @Test
    public void getTableForNotExistingTable() {
        TableProvider test = new Database(databasePath.toString());
        assertEquals(null, test.getTable(tableName));
    }

    @Test
    public void getTableForExistingTable() throws IOException {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(tableName, signature);
        assertNotEquals(null, test.getTable(tableName));
        assertEquals(tableName, test.getTable(tableName).getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableForWrongNamedTable() {
        TableProvider test = new Database(databasePath.toString());
        test.getTable(stringWithBannedSymbols);
    }

    @Test
    public void createTableForNotExistingTable() throws IOException {
        TableProvider test = new Database(databasePath.toString());
        assertNotEquals(null, test.createTable(tableName, signature));
        assertTrue(databasePath.resolve(tableName).toFile().exists());
    }

    @Test
    public void testCreateTableForExistingTable() throws IOException {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(tableName, signature);
        assertEquals(null, test.createTable(tableName, signature));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableForWrongNamedTable() throws IOException {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(stringWithBannedSymbols, signature);
    }

    @Test(expected = TableDoesNotExistException.class)
    public void removeTableForExistingTable() throws IOException {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(tableName, signature);
        Table testTable = test.getTable(tableName);
        test.removeTable(tableName);
        testTable.getName();
    }

    @Test(expected = IllegalStateException.class)
    public void removeTableForNotExistingTable() throws IOException {
        TableProvider test = new Database(databasePath.toString());
        test.removeTable(tableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableForWrongTableName() throws IOException {
        TableProvider test = new Database(databasePath.toString());
        test.removeTable(stringWithBannedSymbols);
    }

    @Test
    public void testGetTablesList() throws IOException {
        TableProvider test = new Database(databasePath.toString());
        test.createTable(tableName, signature);
        assertEquals(tableName, test.getTableNames().get(0));
    }
}

