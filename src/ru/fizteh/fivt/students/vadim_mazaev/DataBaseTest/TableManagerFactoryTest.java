package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.vadim_mazaev.DataBase.Helper;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManagerFactory;

@SuppressWarnings("resource")
public class TableManagerFactoryTest {
    @Before
    public void setUp() {
        TestHelper.TEST_DIR.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerFactoryThrowsExceptionCreatedNullTableManager()
            throws IOException {
        TableManagerFactory test = new TableManagerFactory();
        test.create(null);
    }

    @Test
    public void testTableManagerFactoryCreatedNewTableManager()
            throws IOException {
        TableManagerFactory test = new TableManagerFactory();
        assertNotNull(test.create(TestHelper.TEST_DIR.toString()));
        test.close();
    }

    @After
    public void tearDown() throws IOException {
        Helper.recoursiveDelete(TestHelper.TEST_DIR);
    }

}
