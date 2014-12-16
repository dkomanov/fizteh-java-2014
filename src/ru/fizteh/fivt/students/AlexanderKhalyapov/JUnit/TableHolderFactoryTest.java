package ru.fizteh.fivt.students.AlexanderKhalyapov.JUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TableHolderFactoryTest {
    private final Path testDirectory
            = Paths.get(System.getProperty("fizteh.db.dir"));

    @Before
    public final void setUp()
            throws IOException {
        if (!testDirectory.toFile().exists()) {
            Files.createDirectory(testDirectory);
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public final void testFactoryThrowsExceptionCreatedNullTableHolder() {
        TableProviderFactory test = new TableHolderFactory();
        test.create(null);
    }

    @Test
    public final void testTableHolderFactoryCreatedNewValidTableHolder() {
        TableProviderFactory test = new TableHolderFactory();
        TableProvider testProvider = test.create(testDirectory.toString());
        testProvider.createTable("MyTable");

        assertTrue(testDirectory.resolve("MyTable").toFile().exists());
    }

    @After
    public final void tearDown() throws IOException {
        Utility.recursiveDelete(testDirectory);
    }
}
