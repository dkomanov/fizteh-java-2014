package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

import static org.junit.Assert.assertEquals;

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
    private static final String TIMESTAMP_REGEX = "timestamp=\"\\d*\"";
    private static final String PATH_REGEX = "\\[.*\\]";

    @Before
    public void setUp() throws IOException {
        TestHelper.TEST_DIR.toFile().mkdir();
        test = new TableManagerFactory();
        manager = (TableProvider) new DbLoggingProxyFactory().wrap(proxyWriter,
                test.create(TestHelper.TEST_DIR.toString()), TableProvider.class);
    }

    // Unable to check logging by string comparison because of timestamp.
    // @formatter:off
    @Test
    public void testLogging() throws IOException {
        try (DbTable table = (DbTable) manager.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE)) {
            String expectedlogString = "<invoke timestamp=\"\""
                    + " class=\"ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManager\" name=\"createTable\">"
                    + "<arguments>"
                        + "<argument>testTable</argument>"
                        + "<argument>"
                            + "<list>"
                            + "<value>class java.lang.Integer</value>"
                            + "<value>class java.lang.Long</value>"
                            + "<value>class java.lang.Byte</value>"
                            + "<value>class java.lang.Float</value>"
                            + "<value>class java.lang.Double</value>"
                            + "<value>class java.lang.Boolean</value>"
                            + "<value>class java.lang.String</value>"
                            + "</list>"
                        + "</argument>"
                    + "</arguments>"
                    + "<return>DbTable[]</return>"
                    + "</invoke>";
            assertLogs(expectedlogString, proxyWriter.toString());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testLoggingWithThrowingException() throws IOException {
        try {
            manager.removeTable(TestHelper.TEST_TABLE_NAME);
        } finally {
            String expectedlogString = "<invoke timestamp=\"\""
                    + " class=\"ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManager\" name=\"removeTable\">"
                    + "<arguments>"
                        + "<argument>testTable</argument>"
                    + "</arguments>"
                    + "<thrown>java.lang.IllegalStateException: There is no such table</thrown>"
                    + "</invoke>";
            assertLogs(expectedlogString, proxyWriter.toString());
        }
    }
    // @formatter:on

    void assertLogs(String expected, String actual) {
        String log1 = expected.replaceAll(TIMESTAMP_REGEX, "timestamp=\"\"").replaceAll(PATH_REGEX, "[]");
        String log2 = actual.replaceAll(TIMESTAMP_REGEX, "timestamp=\"\"").replaceAll(PATH_REGEX, "[]");
        assertEquals(log1, log2);
    }

    @After
    public void tearDown() throws IOException {
        test.close();
        Helper.recoursiveDelete(TestHelper.TEST_DIR);
    }

}
