package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.TableHolderFactory;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertTrue;

public class TableHolderFactoryTest {
    private final Path testDirectory = Paths.get(System.getProperty("fizteh.db.dir"));

    @Before
    public void setUp() throws Exception { testDirectory.toFile().mkdir(); }
    @Test(expected = IllegalArgumentException.class)
    public void testTableHolderFactoryThrowsIllegalArgumentExceptionCreatedNullTableHolder() {
        TableProviderFactory test = new TableHolderFactory();
        test.create(null);
    }

    @Test
    public void testTableHolderFactoryCreatedNewValidTableHolder() {
        TableProviderFactory test = new TableHolderFactory();
        TableProvider testProvider = test.create(testDirectory.toString());
        testProvider.createTable("MyTable");
        assertTrue(testDirectory.resolve("MyTable").toFile().exists());
    }

    @After
    public void tearDown() throws Exception {
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
