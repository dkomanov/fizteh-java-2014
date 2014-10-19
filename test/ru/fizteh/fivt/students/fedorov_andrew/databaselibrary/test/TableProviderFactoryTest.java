package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import static org.hamcrest.CoreMatchers.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils;

/**
 * Tests {@link TableProviderFactory } mostly for error cases.
 * 
 * @author phoenix
 * 
 */
@RunWith(org.junit.runners.JUnit4.class)
public class TableProviderFactoryTest extends TestBase {
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

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testCreateProviderNull() {
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Directory must not be null");
	factory.create(null);
    }

    @Test
    public void testCreateProviderBadString() {
	exception.expect(IllegalArgumentException.class);
	exception
		.expectMessage("Database directory parent path does not exist or is not a directory");
	factory.create("bad string");
    }

    @Test
    public void testCreateProviderSystemRoot() {
	exception.expect(IllegalArgumentException.class);
	exception
		.expectMessage(startsWith("Failed to scan database directory"));
	factory.create("/");
    }

    @Test
    public void testCreateProviderFarNotExistingPath() {
	exception.expect(IllegalArgumentException.class);
	exception
		.expectMessage("Database directory parent path does not exist or is not a directory");

	factory.create(dbRoot.resolve("subdir_with_missing_parent").toString());
    }

    @Test
    public void testCreateProviderNormal() {
	factory.create(dbRoot.toString());
    }

    @Test
    public void testCreateProviderInDirWithBadFiles() throws IOException {
	Files.createDirectory(dbRoot);
	Files.createFile(dbRoot.resolve("some file"));

	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("DB directory scan: found inproper files");
	factory.create(dbRoot.toString());
    }

    @Test
    public void testCreateProviderInDirWithDirsContainingFiles()
	    throws IOException {
	Files.createDirectory(dbRoot);
	Files.createDirectory(dbRoot.resolve("table"));
	Files.createFile(dbRoot.resolve("table").resolve("file"));

	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("DB directory scan: found inproper files");
	factory.create(dbRoot.toString());
    }

    @Test
    public void testCreateProviderWithCorruptTableFiles() throws IOException {
	Files.createDirectory(dbRoot);
	Path tablePartPath = Paths.get(dbRoot.toString(), "table", "1.dir",
		"1.dat");
	Files.createDirectories(tablePartPath.getParent());
	Files.createFile(tablePartPath);
	try (BufferedWriter writer = Files.newBufferedWriter(tablePartPath,
		Charset.forName("UTF-8"))) {
	    writer.write("invalid data");
	}
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(startsWith("DB file is corrupt"));
	factory.create(dbRoot.toString());
    }
}
