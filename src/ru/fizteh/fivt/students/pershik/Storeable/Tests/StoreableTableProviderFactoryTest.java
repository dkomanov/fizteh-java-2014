package ru.fizteh.fivt.students.pershik.Storeable.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.pershik.Storeable.StoreableTableProviderFactory;

import java.io.IOException;

/**
 * Created by pershik on 11/11/14.
 */
public class StoreableTableProviderFactoryTest {

    private static StoreableTableProviderFactory factory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        factory = new StoreableTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull() throws IOException {
        factory.create(null);
    }

    @Test
    public void create() throws IOException {
        factory.create("dbDir");
    }

    @Test
    public void createSubDirectory() throws IOException {
        factory.create("dbDir2/db/db");
    }
}
