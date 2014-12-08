package ru.fizteh.fivt.students.Bulat_Galiev.proxy.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.Bulat_Galiev.proxy.TabledbProviderFactory;

public class TabledbProviderFactoryTest {
    TableProviderFactory factory = new TabledbProviderFactory();
    Path testDir;

    @Before
    public void setUp() throws Exception {
        String tmpDirPrefix = "Swing_";
        testDir = Files.createTempDirectory(tmpDirPrefix);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullProvider() throws IOException {
        factory.create(null);
    }

    @Test
    public void testCreateNormalDatabase() throws Exception {
        factory.create(testDir.toString());
    }

    @After
    public void tearDown() throws Exception {
        Cleaner.clean(testDir.toFile());
    }

}
