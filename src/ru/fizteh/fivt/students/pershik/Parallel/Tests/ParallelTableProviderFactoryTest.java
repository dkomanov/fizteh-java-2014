package ru.fizteh.fivt.students.pershik.Parallel.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.pershik.Parallel.ParallelTableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by pershik on 11/11/14.
 */
public class ParallelTableProviderFactoryTest {

    private static ParallelTableProviderFactory factory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    public File folder;

    @Before
    public void before() throws IOException {
        factory = new ParallelTableProviderFactory();
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
}
