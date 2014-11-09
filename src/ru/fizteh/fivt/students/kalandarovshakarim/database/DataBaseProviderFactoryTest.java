/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.database;

import ru.fizteh.fivt.students.kalandarovshakarim.database.*;
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
import ru.fizteh.fivt.students.kalandarovshakarim.database.DataBaseProviderFactory;
import ru.fizteh.fivt.students.kalandarovshakarim.filesystem.FileSystemUtils;

/**
 *
 * @author shakarim
 */
public class DataBaseProviderFactoryTest {

    private TableProviderFactory instance;
    private final String testDirectory = System.getProperty("java.io.tmpdir");
    private String existsDirectory;
    private String invalidDirectory;
    private String withoutParentDirectory;
    private String withParentDirectory;
    private String notDirectory;
    private Path txtFile;

    @Before
    public void setUp() throws IOException {
        instance = new DataBaseProviderFactory();
        txtFile = Paths.get(testDirectory, "notDirectory.txt");
        if (!Files.exists(txtFile)) {
            Files.createFile(txtFile);
        }
        existsDirectory = Paths.get(testDirectory, "test.db.dir").toString();
        invalidDirectory = Paths.get(testDirectory, "notDirectory.txt", "db.dir").toString();
        withoutParentDirectory = Paths.get(testDirectory, "dirNotExists", "db.dir").toString();
        withParentDirectory = Paths.get(testDirectory, "dirNotExists").toString();
        notDirectory = Paths.get(testDirectory, "notDirectory.txt").toString();
    }

    void rmTry(String filePath) {
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

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() {
        System.out.println("create database in non-existing directory");
        instance.create(null);
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateInDirectoryWithoutParent() {
        System.out.println("create database in directory without parent");
        instance.create(withoutParentDirectory);
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateOnNondirectoryPath() {
        System.out.println("create database on non-directory path");
        instance.create(notDirectory);
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateOnInvalidPath() {
        System.out.println("create database on invalid path");
        instance.create(invalidDirectory);
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test
    public void testCreateNonexistingDirectory() {
        System.out.println("create database in directory which not exists but has parent");
        TableProvider result = instance.create(withParentDirectory);
        assertNotNull(result);
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test
    public void testCreateInExistingDirectory() {
        System.out.println("create database in existing directory");
        TableProvider result = instance.create(existsDirectory);
        assertNotNull(result);
    }
}
