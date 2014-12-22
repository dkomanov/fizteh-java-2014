package ru.fizteh.fivt.students.Bulat_Galiev.junit.test;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.Bulat_Galiev.junit.TabledbProviderFactory;

public class TabledbProviderFactoryTest {
    private TableProviderFactory factory = new TabledbProviderFactory();
    private Path testDir;

    @Before
    public final void setUp() throws Exception {
        String tmpDirPrefix = "Swing_";
        testDir = Files.createTempDirectory(tmpDirPrefix);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateNullProvider() {
        factory.create(null);
    }

    @Test
    public final void testCreateNormalDatabase() throws Exception {
        factory.create(testDir.toString());
    }

    @After
    public final void tearDown() throws Exception {
        Cleaner.clean(testDir.toFile());
    }

}
