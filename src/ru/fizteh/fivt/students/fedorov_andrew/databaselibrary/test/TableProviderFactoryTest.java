package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils;

import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.*;

/**
 * Tests {@link ru.fizteh.fivt.storage.structured.TableProviderFactory } mostly for error cases.
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
    public void testCreateProviderNull() throws IOException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Directory must not be null");
        factory.create(null);
    }

    @Test
    public void testCreateProviderBadString() throws IOException {
        exception.expect(IOException.class);
        exception.expectMessage(
                "Database directory parent path does not exist or is not a directory");
        factory.create("bad string");
    }

    @Test
    public void testCreateProviderSystemRoot() throws IOException {
        exception.expect(IOException.class);
        exception.expectMessage(allOf(startsWith("Failed to scan database directory")));
        factory.create("/");
    }

    @Test
    public void testCreateProviderFarNotExistingPath() throws IOException {
        exception.expect(IOException.class);
        exception.expectMessage(
                "Database directory parent path does not exist or is not a " + "directory");

        factory.create(DB_ROOT.resolve("subdir_with_missing_parent").toString());
    }

    @Test
    public void testCreateProviderNormal() throws IOException {
        factory.create(DB_ROOT.toString());
    }

    @Test
    public void testCreateProviderWithFileAsRoot() throws IOException {
        Files.createFile(DB_ROOT);

        exception.expect(IOException.class);
        exception.expectMessage("Database root must be a directory");

        factory.create(DB_ROOT.toString());
    }

    @Test
    public void testCreateProviderInDirWithBadFiles() throws Exception {
        Files.createDirectory(DB_ROOT);
        Files.createFile(DB_ROOT.resolve("some file"));

        exception.expect(IOException.class);
        exception.expectMessage(
                allOf(
                        startsWith("Failed to scan database directory"),
                        containsString("Non-directory path found in database folder")));
        factory.create(DB_ROOT.toString());
    }

    @Test
    public void testCreateProviderInDirWithDirsContainingFiles() throws Exception {
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
