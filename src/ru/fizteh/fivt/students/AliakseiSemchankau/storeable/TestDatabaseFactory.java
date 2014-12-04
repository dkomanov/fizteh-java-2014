package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestDatabaseFactory {

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() {
        DatabaseFactory dFactory = new DatabaseFactory();
        dFactory.create(null);
    }

    @Test
    public void testCreatedNotNull() {
        DatabaseFactory dFactory = new DatabaseFactory();
        try {
            assertNotNull(dFactory.create("C:\\JavaTests\\NewCoolDatabase"));
        } catch (Exception exc) {
            assertTrue(false);
        }
    }

}
