package ru.fizteh.fivt.students.olga_chupakhina.storeable.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import ru.fizteh.fivt.students.olga_chupakhina.storeable.OTableProviderFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class TestOTableProviderFactory {

    private final Path testDirectory
            = Paths.get(System.getProperty("user.dir") + File.separator + "db");

    @Before
    public final void setUp()
            throws Exception {
        testDirectory.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testFactoryThrowsExceptionCreatedNullTableHolder() throws IOException {
        TableProviderFactory test = new OTableProviderFactory();
        test.create(null);
    }

    @Test
    public final void testTableHolderFactoryCreatedNewValidTableHolder() throws Exception {
        TableProviderFactory test = new OTableProviderFactory();
        TableProvider testProvider = test.create(testDirectory.toString());
        List<Class<?>> sig = new ArrayList<>();
        sig.add(Integer.class);
        sig.add(String.class);
        testProvider.createTable("MyTable", sig);
        assertTrue(testDirectory.resolve("MyTable").toFile().exists());
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