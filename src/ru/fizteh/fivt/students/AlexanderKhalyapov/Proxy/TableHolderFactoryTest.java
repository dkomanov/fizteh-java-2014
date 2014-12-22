package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


import static org.junit.Assert.*;

public class TableHolderFactoryTest {
    private Path testDirectory;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public final void setUp()
            throws IOException {
        testDirectory = tempFolder.newFolder().toPath();
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testFactoryThrowsExceptionCreatedNullTableHolder() throws IOException {
        TableProviderFactory test = new TableHolderFactory();
        test.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testFactoryThrowsExceptionCreatedForInvalidPath() throws IOException {
        TableProviderFactory test = new TableHolderFactory();
        test.create("\0");
    }

    @Test(expected = IllegalStateException.class)
    public final void testFactoryThrowsExceptionAfterClosing() throws Exception {
        TableProviderFactory test = new TableHolderFactory();
        ((TableHolderFactory) test).close();
        test.create(testDirectory.toString());
    }

    @Test
    public final void testFactoryClosesAllTheProvidersCreatedAfterClosing() throws Exception {
        TableProviderFactory test = new TableHolderFactory();
        Path pathProvider1 = testDirectory.resolve("provider1");
        Path pathProvider2 = testDirectory.resolve("provider2");
        Files.createDirectory(pathProvider1);
        Files.createDirectory(pathProvider2);
        TableProvider testProvider1 = test.create(pathProvider1.toString());
        TableProvider testProvider2 = test.create(pathProvider2.toString());
        ((TableHolderFactory) test).close();
        try {
            testProvider1.getTableNames();
        } catch (IllegalStateException e1) {
            assertEquals("TableProvider was closed\n", e1.getMessage());
        }
        try {
            testProvider2.getTableNames();
        } catch (IllegalStateException e2) {
            assertEquals("TableProvider was closed\n", e2.getMessage());
        }
    }

    @After
    public final void tearDown() throws IOException {
        Utility.recursiveDeleteCopy(testDirectory);
    }

}
