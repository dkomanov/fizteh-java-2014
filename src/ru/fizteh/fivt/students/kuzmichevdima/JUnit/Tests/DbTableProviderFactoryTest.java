package ru.fizteh.fivt.students.kuzmichevdima.JUnit.Tests;


import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.kuzmichevdima.JUnit.DbTableProviderFactory;

public class DbTableProviderFactoryTest {
    private DbTableProviderFactory factory;
    private final Path testFolder = Paths.get(System.getProperty("java.io.tmpdir"), "testFolder");

    @Before
    public void setUp() {
        factory = new DbTableProviderFactory();
        testFolder.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnThrowsExceptionCreatedNullTableManager() {
        //TableProviderFactory factory = new DbTableProviderFactory() {};
        factory.create(null);
    }

    @Test
    public void testOnCreateNewTableProvider() {
        //TableProviderFactory factory = new DbTableProviderFactory() {};Null
        TableProvider testProvider = factory.create(testFolder.toString());
        testProvider.createTable("testTable");
        assertTrue(testFolder.resolve("testTable").toFile().exists());
    }

    @After
    public void tearDown() {
        for (File curFile : testFolder.toFile().listFiles()) {
            if (curFile.isDirectory()) {
                for (File subFile : curFile.listFiles()) {
                    subFile.delete();
                }
            }
            curFile.delete();
        }
        testFolder.toFile().delete();
    }
}
