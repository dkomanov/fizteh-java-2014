package ru.fizteh.fivt.students.pershik.JUnit.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.students.pershik.JUnit.HashTableProviderFactory;

import java.io.IOException;

/**
 * Created by pershik on 10/31/14.
 */
public class HashTableProviderFactoryTest {
    private static HashTableProviderFactory factory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        factory = new HashTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull() {
        factory.create(null);
    }

    @Test
    public void create() {
        factory.create("dbDir");
    }
}
