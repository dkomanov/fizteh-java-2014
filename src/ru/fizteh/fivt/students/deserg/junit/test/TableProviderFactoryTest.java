package ru.fizteh.fivt.students.deserg.junit.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.deserg.junit.DbTableProviderFactory;
import ru.fizteh.fivt.students.deserg.junit.Shell;

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
            System.exit(1);
        }

    }

    @Test
    public void testCreateNotExisting() {

        DbTableProviderFactory factory = new DbTableProviderFactory();

        String name = "non.existing.directory";

        Path path = testDir.resolve(name);

        while (Files.exists(path)) {
            name += "/1";
            path = testDir.resolve(name);
        }

        try {
            assertNotNull(factory.create(name));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

    }

    @After
    public void finish() {
        Shell.delete(testDir);
    }

}
