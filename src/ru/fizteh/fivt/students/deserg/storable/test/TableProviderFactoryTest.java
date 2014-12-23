package ru.fizteh.fivt.students.deserg.storable.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.deserg.storable.DbTableProviderFactory;
import ru.fizteh.fivt.students.deserg.storable.Shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by deserg on 29.11.14.
 */
public class TableProviderFactoryTest {

    Path testDir;

    @Before
    public void init() {

        Path tempPath = Paths.get("").resolve(System.getProperty("user.dir"));

        try {
            testDir = Files.createTempDirectory(tempPath, "temp");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void testCreateExisting() {

        DbTableProviderFactory factory = new DbTableProviderFactory();

        String name = "db1";

        Path path = testDir.resolve(name);

        try {
            Files.createDirectory(path);
        } catch (IOException ex) {
            System.out.println("Directory was not created");
        }

        try {
            assertNotNull(factory.create(name));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Test
    public void testCreateNotExisting() {

        DbTableProviderFactory factory = new DbTableProviderFactory();

        String name = testDir.getFileName() + "/non.existing.directory";

        Path homePath = Paths.get("").resolve(System.getProperty("user.dir"));
        Path path = homePath.resolve(name);

        while (Files.exists(path)) {
            name += "/1";
            path = homePath.resolve(name);
        }

        try {
            assertNotNull(factory.create(name));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Test
    public void testCreateOther() {

        DbTableProviderFactory factory = new DbTableProviderFactory();

        try {
            assertNotNull(factory.create(null));
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            assertNotNull(factory.create("name\000name"));
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }


    @After
    public void finish() {
        Shell.delete(testDir);
    }

}
