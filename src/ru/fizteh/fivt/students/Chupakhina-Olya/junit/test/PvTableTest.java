package ru.fizteh.fivt.students.olga_chupakhina.junit.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.olga_chupakhina.junit.PvTable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNull;

public class PvTableTest {
    private final Path testDirectory
            = Paths.get(System.getProperty("fizteh.db.dir"));
    private final String testTableName = "testTable";
    private final String fileInTableDirectory = "unnecessaryFiles";

    @Before
    public final void setUp()
            throws Exception {
        if (!testDirectory.toFile().exists()) {
            testDirectory.toFile().mkdir();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableProvaderWithNullDirectoryName() {
        TableProvider pv = new PvTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableWithEmptyName() {
        TableProvider pv = new PvTable(testDirectory.toFile().getAbsolutePath());
        pv.createTable(null);
    }

    @Test
    public final void testCreateTableIfItAlreadyExist() {
        TableProvider pv = new PvTable(testDirectory.toFile().getAbsolutePath());
        pv.createTable(testTableName);
        assertNull(pv.createTable(testTableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetTableWithNullName() {
        TableProvider pv = new PvTable(testDirectory.toFile().getAbsolutePath());
        pv.getTable(null);
    }

    @Test(expected = NullPointerException.class)
    public final void testGetTableIfItNotExist() {
        TableProvider pv = new PvTable(testDirectory.toFile().getAbsolutePath());
        pv.getTable(testTableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveTableWithNullName() {
        TableProvider pv = new PvTable(testDirectory.toFile().getAbsolutePath());
        pv.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public final void testRemoveTableIfItNotExist() {
        TableProvider pv = new PvTable(testDirectory.toFile().getAbsolutePath());
        pv.removeTable(testTableName);
    }

    @After
    public final void tearDown() throws Exception {
        for (File currentFile : testDirectory.toFile().listFiles()) {
            if (currentFile.isDirectory()) {
                for (File subFile : currentFile.listFiles()) {
                    subFile.delete();
                }
            }
            currentFile.delete();
        }
        testDirectory.toFile().delete();
    }
}
