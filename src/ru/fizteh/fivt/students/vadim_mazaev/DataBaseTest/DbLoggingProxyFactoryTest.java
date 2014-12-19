package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.DbLoggingProxyFactory;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.DbTable;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.Helper;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManagerFactory;

public class DbLoggingProxyFactoryTest {
    private TableManagerFactory test;
    private StringWriter proxyWriter = new StringWriter();
    private TableProvider manager;

    @Before
    public void setUp() throws IOException {
        TestHelper.TEST_DIR.toFile().mkdir();
        test = new TableManagerFactory();
        manager = (TableProvider) new DbLoggingProxyFactory().wrap(proxyWriter,
                test.create(TestHelper.TEST_DIR.toString()),
                TableProvider.class);
    }

    // Unable to check logging by string comparison because of timestamp.
    @Test
    public void testLogging() throws IOException {
        try (DbTable table = (DbTable) manager.createTable(
                TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE)) {
            System.out.println(proxyWriter.toString());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testLoggingWithThrowingException() throws IOException {
        try {
            manager.removeTable(TestHelper.TEST_TABLE_NAME);
        } finally {
            System.out.println(proxyWriter.toString());
        }
    }

    @After
    public void tearDown() throws IOException {
        test.close();
        Helper.recoursiveDelete(TestHelper.TEST_DIR);
    }

}
