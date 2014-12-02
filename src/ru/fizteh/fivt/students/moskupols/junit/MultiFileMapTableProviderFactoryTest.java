package ru.fizteh.fivt.students.moskupols.junit;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;

import static org.junit.Assert.assertNotNull;

public class MultiFileMapTableProviderFactoryTest {

    private MultiFileMapTableProviderFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new MultiFileMapTableProviderFactory();
    }

    @Test
    public void testCreate() throws Exception {
        assertNotNull(factory.create(Files.createTempDirectory("TestFactory").toString()));
        assertNotNull(factory.create(Files.createTempDirectory("TestFactory").toString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTrash() throws Exception {
        factory.create("%eg/123f......fasd");
    }
}
