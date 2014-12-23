package ru.fizteh.fivt.students.ZatsepinMikhail.Parallel.JUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Parallel.MultiFileHashMap.MFileHashMapFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.shell.FileUtils;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestTableProviderFactory {
    String dir;
    TableProviderFactory testFactory;

    @Before
    public void setUp() {
        dir = Paths.get("").resolve("factory").toString();
        testFactory = new MFileHashMapFactory();
    }

    @After
    public void tearDown() {
        try {
            FileUtils.rmdir(Paths.get(dir));
            FileUtils.mkdir(Paths.get(dir));
        } catch (IOException e) {
            //suppress
        }
    }

    @Test
    public void testCreateDirExists() throws Exception {
        FileUtils.mkdir(Paths.get(dir));
        try {
            assertNotNull(testFactory.create(dir));
        } catch (IOException e) {
            assertTrue(false);
        }
    }


    @Test
    public void testCreateDirNotExists() throws Exception {
        assertNotNull(testFactory.create(dir));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        testFactory.create(null);
    }
}
