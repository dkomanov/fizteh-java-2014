package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.TableHolderFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.assertTrue;

public class TableHolderFactoryTest {
    private final Path testDirectory
            = Paths.get(System.getProperty("fizteh.db.dir"));

    @Before
    public final void setUp()
            throws Exception {
        testDirectory.toFile().mkdir();
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
        Files.walkFileTree(testDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e)
                    throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                        /*
                         * directory iteration failed
                          */
                    throw e;
                }
            }
        });
    }
}
