package ru.fizteh.fivt.students.VasilevKirill.junit.JUnitTests;

import org.junit.Test;
import ru.fizteh.fivt.students.VasilevKirill.junit.MyTableProviderFactory;

import static org.junit.Assert.*;

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
