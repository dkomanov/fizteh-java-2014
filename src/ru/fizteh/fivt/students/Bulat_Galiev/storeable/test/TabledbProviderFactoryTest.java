package ru.fizteh.fivt.students.Bulat_Galiev.storeable.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.Bulat_Galiev.storeable.TabledbProviderFactory;
import ru.fizteh.fivt.students.Bulat_Galiev.storeable.test.Cleaner;

public class TabledbProviderFactoryTest {
    TableProviderFactory factory = new TabledbProviderFactory();
    Path testDir;

    @Before
    public void setUp() throws Exception {
        String tmp_dir_prefix = "Swing_";
        testDir = Files.createTempDirectory(tmp_dir_prefix);
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
