package ru.fizteh.fivt.students.deserg.junit.test;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.deserg.junit.DbTableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by deserg on 29.11.14.
 */
public class TableProviderFactoryTest {

    Path curDir;

    @Before
    public void init() {

        curDir = Paths.get("").resolve(System.getProperty("user.dir"));

    }

    @Test
    public void testCreateExitsting() {

        DbTableProviderFactory factory = new DbTableProviderFactory();

        String name = "db1";

        Path path = curDir.resolve(name);

        try {
            Files.createDirectories(path);
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

        Path path = curDir.resolve(name);

        while (Files.exists(path)) {
            name += "/1";
            path = curDir.resolve(name);
        }

        try {
            assertNotNull(factory.create(name));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

    }


}
