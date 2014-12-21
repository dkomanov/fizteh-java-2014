package ru.fizteh.fivt.students.AliakseiSemchankau.parallel2;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class TestDatabaseFactory {

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() {
        DatabaseFactory dFactory = new DatabaseFactory();
        dFactory.create(null);
    }

    @Test
    public void testCreatedNotNull() {
        TemporaryFolder tmp;
        String tmpName;
        tmp = new TemporaryFolder();
        tmpName = tmp.toString();
        DatabaseFactory dFactory = new DatabaseFactory();
        try {
            assertNotNull(dFactory.create(tmpName));
        } catch (Exception exc) {
            assertTrue(false);
        }
    }

}