package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.MultiFileTableProviderFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MultiFileTableProviderFactoryTest {
    String root = "test/junit-proxy";

    @Rule
    public ExpectedException exception = ExpectedException.none();

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
