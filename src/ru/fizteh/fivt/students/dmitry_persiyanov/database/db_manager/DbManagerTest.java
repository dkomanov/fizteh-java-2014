package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager;

import javafx.scene.control.Tab;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.WrongTableNameException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class DbManagerTest {
    private DbManager dbm;
    private File dbDir;
    private File tempFile;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        dbDir = tempFolder.newFolder();
        dbm = new DbManager(dbDir);
        tempFile = tempFolder.newFile();
    }

    @Test(expected = NullPointerException.class)
    public void createDbManagerWithNullArgument() {
        new DbManager(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDbManagerWithNonDirectoryArgument() {
        new DbManager(tempFile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingTableWithWrongName() {
        dbm.createTable(null);
    }

    @Test
    public void checkingTableReturnedByCreatedForNonNull() {
        Table createdTable = dbm.createTable("table1");
        assertFalse(createdTable == null);
    }

    @Test
    public void checkingEqualityOfReturningTablesByCreateAndGet() {
        Table createdTable = dbm.createTable("table1");
        Table gettedTable = dbm.getTable("table1");
        assertEquals(createdTable.getName(), gettedTable.getName());
        assertEquals("table1", createdTable.getName());
    }

    @Test
    public void creatingRemovingAndGettingItMustReturnNull() {
        Table createdTable = dbm.createTable("table1");
        dbm.removeTable("table1");
        assertTrue(dbm.getTable("table1") == null);
    }

    @Test
    public void createMustReturnNullWhenTheTableHasAlreadyExists() {
        dbm.createTable("table1");
        assertTrue(dbm.createTable("table1") == null);
    }

    @Test(expected = IllegalStateException.class)
    public void removeUnexistedTableMustReturnException() {
        dbm.removeTable("wasssssup");
    }

    @Test
    public void testingRightTableDeleting() {
        Table t = dbm.createTable("table1");
        t.put("key", "value");
        t.commit();
        dbm.removeTable("table1");
        TableProvider tableProvider = new DbManager(dbDir);
        assertTrue(tableProvider.getTable("table1") == null);
    }

    @Test(expected = WrongTableNameException.class)
    public void createTableWithWrongName1() {
        dbm.createTable(null);
    }

    @Test(expected = WrongTableNameException.class)
    public void createTableWithWrongName2() {
        dbm.createTable("spaces are not allowed!");
    }

    @Test(expected = WrongTableNameException.class)
    public void useTableWithWrongName() {
        dbm.useTable("wrong naaame");
    }

    @Test(expected = IllegalArgumentException.class)
    public void useUnexistedTable() {
        dbm.useTable("unexisted");
    }

    @Test
    public void useTableFirstTime() {
        Table table = dbm.createTable("table");
        dbm.useTable("table");
        assertEquals("table", dbm.getCurrentTable().getName());
    }

    @Test
    public void useWithUncommittedCurrentTable() {
        dbm.createTable("table1");
        dbm.createTable("table2");
        dbm.useTable("table1");
        dbm.getCurrentTable().put("heeey", "yoooo");
        assertFalse(dbm.useTable("table2") == 0);
    }

    @Test
    public void useWithCommittedCurrentTable() {
        dbm.createTable("table1");
        dbm.createTable("table2");
        dbm.useTable("table1");
        dbm.getCurrentTable().put("heeyey", "yoo");
        dbm.getCurrentTable().commit();
        assertTrue(dbm.useTable("table2") == 0);
    }
}