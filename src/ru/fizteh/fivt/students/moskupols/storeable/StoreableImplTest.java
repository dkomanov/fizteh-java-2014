package ru.fizteh.fivt.students.moskupols.storeable;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class StoreableImplTest {
    private Storeable stor;

    @Before
    public void setUp() throws Exception {
        stor = new StoreableImpl(Arrays.asList(StoreableAtomType.values()));
    }

    @Test
    public void testSetColumnAt() throws Exception {
        stor.setColumnAt(0, 42);
        stor.setColumnAt(0, 5);
        stor.setColumnAt(1, 3.14);
        stor.setColumnAt(2, (byte) 5);
        stor.setColumnAt(3, " some str");
        stor.setColumnAt(3, null);
        stor.setColumnAt(4, (long) 4);
        stor.setColumnAt(5, true);
        stor.setColumnAt(5, null);
        stor.setColumnAt(6, (float) 2.8);
    }

    @Test
    public void testGetColumnAt() throws Exception {
        assertEquals(null, stor.getColumnAt(0));
        stor.setColumnAt(0, 42);
        assertEquals(42, stor.getColumnAt(0));
        stor.setColumnAt(0, null);
        assertEquals(null, stor.getColumnAt(0));
    }
}
