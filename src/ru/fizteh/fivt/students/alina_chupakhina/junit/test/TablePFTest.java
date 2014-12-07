package ru.fizteh.fivt.students.alina_chupakhina.junit.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.alina_chupakhina.junit.TablePF;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TablePFTest {
    private final Path testDirectory
            = Paths.get("C:\\Ololo\\Tests");

    @Before
    public final void setUp()
            throws Exception {
        testDirectory.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testFactoryThrowsExceptionCreatedNullTableHolder() {
        TableProviderFactory test = new TablePF();
        test.create(null);
    }

    @Test
    public final void testTableHolderFactoryCreatedNewValidTableHolder() throws Exception {
        TableProviderFactory test = new TablePF();
        TableProvider testProvider = test.create(testDirectory.toString());
        testProvider.createTable("MyTable");
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
