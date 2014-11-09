package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils;

import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.*;

/**
 * Tests {@link TableProviderFactory } mostly for error cases.
 * @author phoenix
 */
@RunWith(org.junit.runners.JUnit4.class)
public class TableProviderFactoryTest extends TestBase {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private TableProviderFactory factory;

    @Before
    public void prepare() {
        factory = TestUtils.obtainFactory();
    }

    @After
    public void cleanup() throws IOException {
        cleanDBRoot();
        factory = null;
    }

    @Test
    public void testCreateProviderNull() throws DatabaseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Directory must not be null");
        factory.create(null);
    }

    @Test
    public void testCreateProviderBadString() throws DatabaseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(
                "Database directory parent path does not exist or is not a " + "directory");
        factory.create("bad string");
    }

    @Test
    public void testCreateProviderSystemRoot() throws DatabaseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(startsWith("DB file is corrupt"));
        factory.create("/");
    }

    @Test
    public void testCreateProviderFarNotExistingPath() throws DatabaseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(
                "Database directory parent path does not exist or is not a " + "directory");

        factory.create(DB_ROOT.resolve("subdir_with_missing_parent").toString());
    }

    @Test
    public void testCreateProviderNormal() throws DatabaseException {
        factory.create(DB_ROOT.toString());
    }

    @Test
    public void testCreateProviderWithFileAsRoot() throws IOException {
        Files.createFile(DB_ROOT);

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Database root must be a directory");

        factory.create(DB_ROOT.toString());
    }

    @Test
    public void testCreateProviderInDirWithBadFiles() throws DatabaseException, IOException {
        Files.createDirectory(DB_ROOT);
        Files.createFile(DB_ROOT.resolve("some file"));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(
                allOf(
                        startsWith("DB file is corrupt"),
                        containsString("Non-directory path found in database folder")));
        factory.create(DB_ROOT.toString());
    }

    @Test
    public void testCreateProviderInDirWithDirsContainingFiles() throws IOException, DatabaseException {
        String table = "table";

        Files.createDirectory(DB_ROOT);
        Files.createDirectory(DB_ROOT.resolve(table));
        Files.createFile(DB_ROOT.resolve(table).resolve("file"));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(
                allOf(
                        startsWith("Table " + table + " is corrupt"),
                        containsString("Database element must be a directory")));

        TableProvider provider = factory.create(DB_ROOT.toString());
        provider.getTable(table);
    }
}
