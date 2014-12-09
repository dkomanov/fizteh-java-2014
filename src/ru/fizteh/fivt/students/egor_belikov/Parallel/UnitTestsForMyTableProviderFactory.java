package ru.fizteh.fivt.students.egor_belikov.Parallel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class UnitTestsForMyTableProviderFactory {

    private final Path testDirectory
            = Paths.get(System.getProperty("java.io.tmpdir"));

    @Before
    public final void setUp()
            throws Exception {
        testDirectory.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testFactoryThrowsExceptionCreatedNullTableHolder() throws IOException {
        TableProviderFactory test = new MyTableProviderFactory();
        test.create(null);
    }

    @Test
    public final void testTableHolderFactoryCreatedNewValidTableHolder() throws Exception {
        TableProviderFactory test = new MyTableProviderFactory();
        TableProvider testProvider = test.create(testDirectory.toString());
        List<Class<?>> sig = new ArrayList<>();
        sig.add(Integer.class);
        sig.add(String.class);
        testProvider.createTable("MyTable", sig);
        assertTrue(testDirectory.resolve("MyTable").toFile().exists());
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
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}