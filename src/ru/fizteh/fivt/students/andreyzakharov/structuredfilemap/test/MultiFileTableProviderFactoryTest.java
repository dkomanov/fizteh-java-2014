package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.test;

import org.junit.Test;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableProviderFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MultiFileTableProviderFactoryTest {
    String root = "test-junit-structured";

    @Test
    public void testCreate() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();

        assertNotNull(f.create(root));
    }

    @Test
    public void testCreateNonExisting() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();

        assertNull(f.create("/zzz/arguably/non/existing/directory"));
    }
}
