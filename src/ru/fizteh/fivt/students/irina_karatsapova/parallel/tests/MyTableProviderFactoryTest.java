package ru.fizteh.fivt.students.irina_karatsapova.parallel.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.table_provider_factory.MyTableProviderFactory;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class MyTableProviderFactoryTest {

    TemporaryFolder tempFolder = new TemporaryFolder();
    String existingDir = "existing-dir";
    String notExistingDir = "not-existing-dir";
    TableProviderFactory factory;

    @Before
    public void setUp() throws Exception {
        tempFolder.create();
        File factoryDir = tempFolder.newFolder();
        factory = new MyTableProviderFactory(factoryDir.toString());
    }

    @After
    public void tearDown() throws Exception {
        tempFolder.delete();
    }

    @Test
    public void testCreateExistingDir() throws Exception {
        assertNotNull(factory.create(existingDir));
        assertNotNull(factory.create(existingDir));
    }

    @Test
    public void testCreateNotExistingDir() throws Exception {
        assertNotNull(factory.create(notExistingDir));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }
}
