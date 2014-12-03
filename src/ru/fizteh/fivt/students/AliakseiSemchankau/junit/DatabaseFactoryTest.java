package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class DatabaseFactoryTest {

    TemporaryFolder folder;
    String folderName;
    DatabaseFactory dFactory;

    @Test
    public void testCreate() {

        dFactory = new DatabaseFactory();

        try {
            dFactory.create(null);
            assertTrue(false);
        } catch (Exception exc) {
            assertTrue(true);
        }
    }

}