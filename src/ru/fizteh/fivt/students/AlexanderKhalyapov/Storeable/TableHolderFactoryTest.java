package ru.fizteh.fivt.students.AlexanderKhalyapov.Storeable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TableHolderFactoryTest {
    private final Path testDirectory
            = Paths.get(System.getProperty("fizteh.db.dir"));

    @Before
    public final void setUp()
            throws IOException {
        if (!Files.exists(testDirectory)) {
            Files.createDirectory(testDirectory);
        }
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

    @After
    public final void tearDown() throws IOException {
        Utility.recursiveDeleteCopy(testDirectory);
    }

}
