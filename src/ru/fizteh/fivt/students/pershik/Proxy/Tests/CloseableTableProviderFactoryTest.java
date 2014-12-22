package ru.fizteh.fivt.students.pershik.Proxy.Tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.pershik.Proxy.CloseableTableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by pershik on 11/11/14.
 */
public class CloseableTableProviderFactoryTest {

    private static CloseableTableProviderFactory factory;
    public File folder;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        factory = new CloseableTableProviderFactory();
        folder = tmpFolder.newFolder("dbDir");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull() throws IOException {
        factory.create(null);
    }

    @Test
    public void create() throws IOException {
        factory.create(folder.getAbsolutePath());
    }

    @Test
    public void createSubDirectory() throws IOException {
        factory.create(folder.getAbsolutePath() + File.separator + "db"
                + File.separator + "db");
    }

    @Test(expected = IllegalStateException.class)
    public void usingAfterClose() throws Exception {
        factory.close();
        factory.create("dbDir");
    }
}
