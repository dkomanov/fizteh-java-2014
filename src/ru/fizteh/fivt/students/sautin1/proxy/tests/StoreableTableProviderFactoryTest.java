package ru.fizteh.fivt.students.sautin1.proxy.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sautin1.proxy.shell.FileUtils;
import ru.fizteh.fivt.students.sautin1.proxy.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.sautin1.proxy.storeable.StoreableTableProviderFactory;

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

    @Test (expected = IllegalStateException.class)
    public void testClosedProvider() throws Exception {
        FileUtils.removeDirectory(testDir);
        StoreableTableProvider provider = factory.create(testDir.toString());
        factory.close();
        provider.getRootDir();
    }

    @Test (expected = IllegalStateException.class)
    public void testCloseBeforeMethod() throws Exception {
        FileUtils.removeDirectory(testDir);
        StoreableTableProvider provider = factory.create(testDir.toString());
        factory.close();
        factory.create(testDir.toString());
    }
}
