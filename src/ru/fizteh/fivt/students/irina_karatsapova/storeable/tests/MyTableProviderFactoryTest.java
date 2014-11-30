package ru.fizteh.fivt.students.irina_karatsapova.storeable.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.table_provider_factory.MyTableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.Utils;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;


public class MyTableProviderFactoryTest {

    String existingDir = "d://tmp-storeable-test/existing-dir";
    String notExistingDir = "d://tmp-storeable-test/not-existing-dir";
    TableProviderFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new MyTableProviderFactory();
    }

    @Test
    public void testCreateExistingDir() throws Exception {
        File existingDirFile = Paths.get(existingDir).toFile();
        existingDirFile.mkdirs();
        assertNotNull(factory.create(existingDir));
        Utils.rmdirs(existingDirFile);
    }

    @Test
    public void testCreateNotExistingDir() throws Exception {
        File notExistingDirFile = Paths.get(notExistingDir).toFile();
        notExistingDirFile.mkdirs();
        assertNotNull(factory.create(notExistingDir));
        Utils.rmdirs(notExistingDirFile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }
}
