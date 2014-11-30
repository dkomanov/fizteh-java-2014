package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.test;

import org.junit.Test;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.DataBaseTableProviderFactory;

import java.io.IOException;

import static org.junit.Assert.*;

public class DataBaseTableProviderFactoryTest {

    public static final String PATH = "/home/akhtyamovpavel/Development/test/database5";
    @Test(expected = IllegalArgumentException.class)
    public void testCreate() throws IOException {
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        factory.create(null);
    }

    @Test
    public void testCreateNotNull() {
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        try {
            assertNotNull(factory.create("/home/akhtyamovpavel/Development/test/database5"));
        } catch (IOException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testCreateNotExists() {
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        try {
            assertNull(factory.create(
                    "/home/akhtyamovpavel/Development/test/database6/non_exist", true));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testClose() {
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        try {
            assertNotNull(factory.create(PATH));
        } catch (IOException e) {
            fail();
        }
        try {
            factory.close();
        } catch (Exception e) {
            fail();
        }

        try {
            factory.create(PATH);
            fail();
        } catch (IOException e) {
            fail();
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }





}

