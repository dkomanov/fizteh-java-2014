package ru.fizteh.fivt.students.kolmakov_sergey.proxy.tests.logging;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_structure.TableManager;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.logging.LoggingProxyFactoryClass;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.util.DirectoryKiller;

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;

public class LoggingProxyFactoryClassTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DataBaseTestDirectory");
    private final String tableName = "table1";
    private final String nonExistentName = "non-existent";
    private final String TIMESTAMP_PATTERN = ".*timestamp=\"\\d*\".*";
    List<Class<?>> signature;
    TableProvider provider;
    StringWriter writer = new StringWriter();

    @Before
    public void setUp() throws Exception {
        testDir.toFile().mkdir();
        signature = new LinkedList<>();
        signature.add(Integer.class);
        signature.add(String.class);
        provider = (TableProvider) new LoggingProxyFactoryClass().wrap(writer, new TableManager(testDir.toString()),
                TableProvider.class);
    }

    @Test
    public void testLoggingWithoutException() throws IOException {
        provider.createTable(tableName, signature);
        String needed = new String ("class=\"ru.fizteh.fivt.students.kolmakov_sergey."
                + "proxy.data_base_structure.TableManager\" "
                + "name=\"createTable\">"
                + "<arguments><argument>table1</argument><argument>"
                + "<list><value>class java.lang.Integer</value>"
                + "<value>class java.lang.String</value>"
                + "</list></argument></arguments>"
                + "<return>TableClass["
                + testDir + "\\" + tableName
        + "]</return></invoke>");
        assert (writer.toString().matches(TIMESTAMP_PATTERN));
        assert (writer.toString().contains(needed));
    }

    @Test(expected = IllegalStateException.class)
    public void testLoggingWithExceptions() throws IOException {
        try {
            provider.removeTable(nonExistentName);
        } finally {
            String needed = "class=\"ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_structure.TableManager\""
            + " name=\"removeTable\"><arguments><argument>" + nonExistentName + "</argument></arguments><thrown>"
            + "java.lang.IllegalStateException: Table not found</thrown></invoke>";
            assert (writer.toString().contains(needed));
            assert (writer.toString().matches(TIMESTAMP_PATTERN));
        }
    }

    @After
    public void tearDown() {
        DirectoryKiller.delete(testDir.toFile());
    }
}
