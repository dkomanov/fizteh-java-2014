package ru.fizteh.fivt.students.sautin1.junit.test;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sautin1.junit.StringTableProviderFactory;
import ru.fizteh.fivt.students.sautin1.junit.filemap.StringTableProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class StringTableProviderFactoryTest {
    Path testDir = Paths.get("").resolve("test");
    StringTableProviderFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new StringTableProviderFactory();
    }

    @Test
    public void testCreate() throws Exception {
        StringTableProvider provider = factory.create(testDir.toString());
        assertNotNull(provider);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        StringTableProvider provider = factory.create(null);
    }
}
