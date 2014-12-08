package ru.fizteh.fivt.students.pershik.JUnit.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.students.pershik.JUnit.HashTableProvider;
import ru.fizteh.fivt.students.pershik.JUnit.HashTableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by pershik on 10/29/14.
 */
public class HashTableProviderTest {
    private static HashTableProviderFactory factory;
    private static HashTableProvider provider;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        factory = new HashTableProviderFactory();
    }

    @Before
    public void before() throws IOException {
        provider = factory.create(tmpFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInvalidNameDoublePoint() {
        provider.createTable("..");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInvalidNamePoint() {
        provider.createTable(".");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInvalidNameSlash() {
        provider.createTable("gg" + File.separator + "fd");
    }

    @Test
    public void createNotExisting() {
        Assert.assertNotNull(provider.createTable("table"));
    }

    @Test
    public void createExisting() {
        provider.createTable("table");
        Assert.assertNull(provider.createTable("table"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNull() {
        provider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeInvalidNameDoublePoint() {
        provider.removeTable("..");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeInvalidNamePoint() {
        provider.removeTable(".");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeInvalidNameSlash() {
        provider.removeTable("gg" + File.separator + "fd");
    }

    @Test(expected = IllegalStateException.class)
    public void removeNotExisting() {
        provider.removeTable("table");
    }

    @Test
    public void removeExisting() {
        provider.createTable("table");
        provider.removeTable("table");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInvalidNameDoublePoint() {
        provider.getTable("..");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInvalidNamePoint() {
        provider.getTable(".");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInvalidNameSlash() {
        provider.getTable("gg" + File.separator + "fd");
    }

    @Test
    public void getNotExisting() {
        Assert.assertNull(provider.getTable("table"));
    }

    @Test
    public void getExisting() {
        provider.createTable("table");
        Assert.assertNotNull(provider.getTable("table"));
    }

    @Test
    public void get() {
        provider.createTable("table");
        provider.createTable("table2");
        provider.removeTable("table");
        Assert.assertNull(provider.getTable("table"));
        Assert.assertNotNull(provider.getTable("table2"));
    }
}
