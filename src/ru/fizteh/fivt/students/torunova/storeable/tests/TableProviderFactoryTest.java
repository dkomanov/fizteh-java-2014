package ru.fizteh.fivt.students.torunova.storeable.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.torunova.storeable.database.DatabaseFactoryWrapper;
import ru.fizteh.fivt.students.torunova.storeable.database.DatabaseWrapper;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class TableProviderFactoryTest {
    TableProviderFactory tableProviderFactory;
    File testDirectory;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Before
    public void setUp() throws Exception {
        tableProviderFactory = new DatabaseFactoryWrapper();
        testDirectory = folder.newFolder("db");
    }
    @Test
    public void testCreate() throws Exception {
        DatabaseWrapper db = new DatabaseWrapper(testDirectory.getAbsolutePath());
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
