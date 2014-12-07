package ru.fizteh.fivt.students.torunova.junit.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.torunova.junit.Database;
import ru.fizteh.fivt.students.torunova.junit.DatabaseFactory;

import java.io.File;

import static org.junit.Assert.*;

public class TableProviderFactoryTest {
    TableProviderFactory tableProviderFactory;
    File testDirectory;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Before
    public void setUp() throws Exception {
        tableProviderFactory = new DatabaseFactory();
        testDirectory = folder.newFolder("db");
    }
    @Test
    public void testCreate() throws Exception {
        Database db = new Database(testDirectory.getAbsolutePath());
        assertEquals(db, tableProviderFactory.create(testDirectory.getAbsolutePath()));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableProviderWithNullName() throws Exception {
        tableProviderFactory.create(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableProviderWithIllegalName1() throws Exception {
        tableProviderFactory.create(".");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableProviderWithIllegalName2() throws Exception {
        tableProviderFactory.create("..");
    }

}
