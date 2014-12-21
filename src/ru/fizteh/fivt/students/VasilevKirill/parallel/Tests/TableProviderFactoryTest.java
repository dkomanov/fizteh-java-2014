package ru.fizteh.fivt.students.VasilevKirill.parallel.Tests;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.VasilevKirill.parallel.structures.MyTableProviderFactory;


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
}

