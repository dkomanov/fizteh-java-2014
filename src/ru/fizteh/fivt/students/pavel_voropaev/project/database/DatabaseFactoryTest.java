package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.pavel_voropaev.project.Utils;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class DatabaseFactoryTest {
    private final Path testDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "FiztehDbDir");

    @Before
    public void setUp() {
        testDirectory.toFile().mkdir();
    }

    @After
    public void tearDown() throws IOException {
        Utils.rm(testDirectory);
    }

    @Test
    public void factoryCreateForExistingFolder() {
        TableProviderFactory test = new DatabaseFactory();
        test.create(testDirectory.toString());
    }

    @Test
    public void factoryCreateForNotExistingFolder() throws IOException {
        Utils.rm(testDirectory);

        TableProviderFactory test = new DatabaseFactory();
        test.create(testDirectory.toString());
        assertTrue(testDirectory.toFile().exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryCreateForNull() {
        TableProviderFactory test = new DatabaseFactory();
        test.create(null);
    }
}
