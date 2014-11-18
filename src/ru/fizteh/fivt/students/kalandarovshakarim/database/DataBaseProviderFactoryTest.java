/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.kalandarovshakarim.filesystem.FileSystemUtils;

/**
 *
 * @author shakarim
 */
public class DataBaseProviderFactoryTest {

    private TableProviderFactory instance;
    private static final String TEST_DIRECTORY = System.getProperty("java.io.tmpdir");
    private String existsDirectory;
    private String invalidDirectory;
    private String withoutParentDirectory;
    private String withParentDirectory;
    private String notDirectory;
    private Path txtFile;

    @Before
    public void setUp() throws IOException {
        instance = new DataBaseProviderFactory();
        txtFile = Paths.get(TEST_DIRECTORY, "notDirectory.txt");
        if (!Files.exists(txtFile)) {
            Files.createFile(txtFile);
        }
        existsDirectory = Paths.get(TEST_DIRECTORY, "test.db.dir").toString();
        invalidDirectory = Paths.get(TEST_DIRECTORY, "notDirectory.txt", "db.dir").toString();
        withoutParentDirectory = Paths.get(TEST_DIRECTORY, "dirNotExists", "db.dir").toString();
        withParentDirectory = Paths.get(TEST_DIRECTORY, "dirNotExists").toString();
        notDirectory = Paths.get(TEST_DIRECTORY, "notDirectory.txt").toString();
    }

    private void rmTry(String filePath) {
        FileSystemUtils utils = new FileSystemUtils();
        try {
            utils.rm(filePath, true);
        } catch (IOException e) {
            // Nothing.
        }
    }

    @After
    public void tearDown() {
        rmTry(existsDirectory);
        rmTry(invalidDirectory);
        rmTry(notDirectory);
        rmTry(withParentDirectory);
        rmTry(withoutParentDirectory);
        rmTry(txtFile.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCalledWithNullArgument() {
        instance.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInDirectoryWithoutParent() {
        instance.create(withoutParentDirectory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOnNondirectoryPath() {
        instance.create(notDirectory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOnInvalidPath() {
        instance.create(invalidDirectory);
    }

    @Test
    public void testCreateNonexistingDirectory() {
        TableProvider result = instance.create(withParentDirectory);
        assertNotNull(result);
    }

    @Test
    public void testCreateInExistingDirectory() {
        TableProvider result = instance.create(existsDirectory);
        assertNotNull(result);
    }
}
