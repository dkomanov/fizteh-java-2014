package ru.fizteh.fivt.students.alexpodkin.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.alexpodkin.JUnit.HashTableProviderFactory;

import java.io.IOException;

/**
 * Created by Alex on 16.11.14.
 */
public class HashTableProviderFactoryTest {
    private static HashTableProviderFactory hashTableProviderFactory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        hashTableProviderFactory = new HashTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullName() {
        hashTableProviderFactory.create(null);
    }

    @Test
    public void create() {
        Assert.assertNotNull(hashTableProviderFactory.create("baseDir"));
    }
}
