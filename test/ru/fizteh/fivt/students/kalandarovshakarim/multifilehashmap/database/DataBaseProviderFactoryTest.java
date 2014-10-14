/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.database;

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
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellUtils;

/**
 *
 * @author shakarim
 */
public class DataBaseProviderFactoryTest {

    private TableProviderFactory instance;
    private final String testDirectory = System.getProperty("test.dir");;
    private String existsDirectory;
    private String invalidDirectory;
    private String withoutParentDirectory;
    private String withParentDirectory;
    private String notDirectory;

    @Before
    public void setUp() {
        instance = new DataBaseProviderFactory();
        existsDirectory = testDirectory + "/test.db.dir";
        invalidDirectory = testDirectory + "/notDirectory.txt/db.dir";
        withoutParentDirectory = testDirectory + "/dirNotExists/db.dir";
        withParentDirectory = testDirectory + "/dirNotExists";
        notDirectory = testDirectory + "/notDirectory.txt";
    }

    @After
    public void tearDown() {
        ShellUtils utils = new ShellUtils();
        try {
            utils.rm(withParentDirectory, true);
            utils.rm(withoutParentDirectory, true);
        } catch (Exception e) {
            // Nothing.
        }
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreate1() {
        System.out.println("create database in non-existing directory");
        TableProvider result = instance.create(null);
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreate2() {
        System.out.println("create database in directory without parent");
        TableProvider result = instance.create(withoutParentDirectory);
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreate3() {
        System.out.println("create database on non-directory path");
        TableProvider result = instance.create(notDirectory);
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreate4() {
        System.out.println("create database on invalid path");
        TableProvider result = instance.create(invalidDirectory);
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test
    public void testCreate5() {
        System.out.println("create database in directory which not exists but has parent");
        TableProvider result = instance.create(withParentDirectory);
        assertNotNull(result);
    }

    /**
     * Test of create method, of class DataBaseProviderFactory.
     */
    @Test
    public void testCreate6() {
        System.out.println("create database in existing directory");
        TableProvider result = instance.create(existsDirectory);
        assertNotNull(result);
    }
}
