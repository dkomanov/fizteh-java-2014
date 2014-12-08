package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.test;

import org.junit.AfterClass;
import org.junit.Test;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MultiFileTableProviderFactoryTest {

    @AfterClass
    public static void tearDown() {
        try {
            Files.deleteIfExists(Paths.get(MultiFileTableTest.rootName));
        } catch (IOException e) {
            //
        }
    }

    @Test
    public void testCreate() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();

        assertNotNull(f.create(MultiFileTableTest.rootName));
    }

    @Test
    public void testCreateNonExisting() throws Exception {
        MultiFileTableProviderFactory f = new MultiFileTableProviderFactory();

        assertNull(f.create("/zzz/arguably/non/existing/directory"));
    }
}
