package ru.fizteh.fivt.students.dsalnikov.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTableProvider;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTableProviderFactory;
import ru.fizteh.fivt.students.dsalnikov.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class StorableTableProviderFactoryTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    StorableTableProviderFactory tableProviderFactory;
    File directory;

    @Before
    public void setUpTestObject() throws IOException {
        directory = folder.newFolder();
        tableProviderFactory = new StorableTableProviderFactory();
    }

    @After
    public void tearDownTestObject() throws IOException {
        FileUtils.forceRemoveDirectory(directory);
    }


    @Test(expected = IllegalArgumentException.class)
    public void createNullTableProvider() throws IOException {
        tableProviderFactory.create(null);
    }

    @Test(expected = IllegalStateException.class)
    public void createAfterClose() throws IOException {
        tableProviderFactory.close();
        tableProviderFactory.create(directory.getAbsolutePath());
    }

    @Test
    public void openProviderWithCreateAfterClose() throws IOException {
        StorableTableProvider provider = (StorableTableProvider) tableProviderFactory
                .create(directory.getAbsolutePath());
        provider.close();
        StorableTableProvider provider1 = (StorableTableProvider) tableProviderFactory
                .create(directory.getAbsolutePath());
        Assert.assertNotEquals(provider, provider1);
    }
}
