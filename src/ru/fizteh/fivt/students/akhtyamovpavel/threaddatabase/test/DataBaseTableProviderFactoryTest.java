package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.test;

import org.junit.Test;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTableProviderFactory;

import java.io.IOException;

import static org.junit.Assert.*;

public class DataBaseTableProviderFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() throws IOException {
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        factory.create(null);
    }

    @Test
    public void testCreateNotNull() {
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        try {
            assertNotNull(factory.create("D:\\test\\database5"));
        } catch (IOException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testCreateNotExists() {
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        try {
            assertNull(factory.create("D:\\test\\database6\\not_exist", true));
        } catch (Exception e) {
            assertTrue(false);
        }
    }

}

