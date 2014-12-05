package ru.fizteh.fivt.students.egor_belikov.JUnit.UnitTests;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.egor_belikov.JUnit.MyTableProvider;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UnitTestsForTableProvider {
    private final Path testDirectory
            = Paths.get(System.getProperty("user.dir") + File.separator + "db");//Paths.get(System.getProperty("fizteh.db.dir"));
    private final String testTableName = "qqq";

    @Before
    public final void setUp()
            throws Exception {
        if (!testDirectory.toFile().exists()) {
            testDirectory.toFile().mkdir();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public final void tableProviderNull() {
        TableProvider myTableProvider = new MyTableProvider(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void createTableNull() {
        TableProvider myTableProvider = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        myTableProvider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void getTableNull() {
        TableProvider myTableProvider = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        myTableProvider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void removeTableNull() {
        TableProvider myTableProvider = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        myTableProvider.removeTable(null);
    }

    @Test
    public final void createTableExisted() {
        TableProvider myTableProvider = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        myTableProvider.createTable(testTableName);
        assertTrue(null == myTableProvider.createTable(testTableName));
    }


    @Test
    public final void getTableNotExisted() {
        TableProvider myTableProvider = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        assertNull(myTableProvider.getTable(testTableName));
    }

    @Test(expected = IllegalStateException.class)
    public final void removeTableNotExisted() {
        TableProvider myTableProvider = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        myTableProvider.removeTable(testTableName);
    }

    @After
    public final void tearDown() throws Exception {
        try {
            for (File currentFile : testDirectory.toFile().listFiles()) {
                if (currentFile.isDirectory()) {
                    for (File subFile : currentFile.listFiles()) {
                        subFile.delete();
                    }
                }
                currentFile.delete();
            }
            testDirectory.toFile().delete();
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
