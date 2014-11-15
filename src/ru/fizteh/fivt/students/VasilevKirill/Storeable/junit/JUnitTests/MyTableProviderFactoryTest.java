package ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.JUnitTests;

import org.junit.Test;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.MyTableProviderFactory;

import static org.junit.Assert.assertTrue;

public class MyTableProviderFactoryTest {

    @Test
    public void testCreate() throws Exception {
        try {
            new MyTableProviderFactory().create(null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}
