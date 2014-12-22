package ru.fizteh.fivt.students.sautin1.storeable.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sautin1.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.sautin1.storeable.StoreableTableProviderFactory;
import ru.fizteh.fivt.students.sautin1.storeable.shell.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;

/**
 * Created by sautin1 on 12/13/14.
 */
public class StoreableTableProviderFactoryTest {
    Path testDir;
    StoreableTableProviderFactory factory;

    @Before
    public void setUp() throws Exception {
        testDir = Paths.get("").resolve("test");
        try {
            Files.createDirectory(testDir);
        } catch (IOException e) {
            // ok
        }
        factory = new StoreableTableProviderFactory();
    }

    @Test
    public void testCreate() throws Exception {
        StoreableTableProvider provider = factory.create(testDir.toString());
        assertNotNull(provider);
    }

    @Test
    public void testCreateNonExisting() throws Exception {
        FileUtils.removeDirectory(testDir);
        StoreableTableProvider provider = factory.create(testDir.toString());
        assertNotNull(provider);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        StoreableTableProvider provider = factory.create(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateRubbish() throws Exception {
        StoreableTableProvider provider = factory.create("([/<:>/])");
    }

    @Test (expected = IOException.class)
    public void testCreateAlreadyExistingDirectory() throws Exception {
        Files.createFile(testDir);
        factory.create(testDir.toString());
    }
}
