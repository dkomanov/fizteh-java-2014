package ru.fizteh.fivt.students.sautin1.junit.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sautin1.junit.StringTableProviderFactory;
import ru.fizteh.fivt.students.sautin1.junit.filemap.StringTable;
import ru.fizteh.fivt.students.sautin1.junit.filemap.StringTableProvider;
import ru.fizteh.fivt.students.sautin1.junit.shell.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class StringTableProviderTest {
    Path testDir;
    StringTableProviderFactory factory;
    StringTableProvider provider;
    String tableName;
    String existingDirectoryName;

    @Before
    public void setUp() throws Exception {
        testDir = Paths.get("").resolve("test");
        factory = new StringTableProviderFactory();
        provider = factory.create(testDir.toString());
        tableName = "testTable";
        existingDirectoryName = "existingDir";
    }

    @After
    public void tearDown() throws Exception {
        try {
            provider.removeTable(tableName);
        } catch (Exception e) {
            // haven't created such table
        }
        FileUtils.clearDirectory(testDir);
    }

    @Test
    public void testCreateTable() throws Exception {
        StringTable table = provider.createTable(tableName);
        assertNotNull(table);

        StringTable table2 = provider.createTable(tableName);
        assertNull(table2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableExistingDirectory() throws Exception {
        Path tempDirPath = Files.createTempDirectory(testDir, existingDirectoryName);
        provider.createTable(tempDirPath.getFileName().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNotExistingPath() throws Exception {
        provider.createTable(existingDirectoryName + "/" + existingDirectoryName);
    }

    @Test
    public void testGetTable() throws Exception {
        StringTable createdTable = provider.createTable(tableName);
        StringTable gotTable = provider.getTable(tableName);
        assertEquals(createdTable, gotTable);

        gotTable = provider.getTable(tableName + tableName);
        assertNull(gotTable);
    }

    @Test
    public void testRemoveTable() throws Exception {
        provider.createTable(tableName);
        provider.removeTable(tableName);
        assertNull(provider.getTable(tableName));
        assertFalse(Files.exists(testDir.resolve(tableName)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNotExistingTable() throws Exception {
        provider.removeTable(tableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableDeletedDir() throws Exception {
        provider.createTable(tableName);
        FileUtils.removeDirectory(testDir.resolve(tableName));
        provider.removeTable(tableName);
    }
}
