package ru.fizteh.fivt.students.VasilevKirill.proxy.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.VasilevKirill.proxy.structures.MyTableProviderFactory;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class TableProviderFactoryTest {
    private static TableProviderFactory factory;

    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
    }

    @Test
    public void testCreate() throws Exception {
        try {
            factory.create(null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testClose() throws Exception {
        TableProviderFactory factory2 = new MyTableProviderFactory();
        try {
            factory2.create(Files.createTempDirectory("database1").toString());
            assertTrue(true);
        } catch (IllegalStateException e) {
            assertTrue(false);
        }
        ((MyTableProviderFactory) factory2).close();
        try {
            factory2.create(Files.createTempDirectory("database2").toString());
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }
}
