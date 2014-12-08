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
import java.io.File;
import java.io.IOException;

public class LoggingProxyFactoryClassTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DataBaseTestDirectory");
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
        provider.createTable("table1", signature);
        System.out.println(writer.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testLoggingWithExceptions() throws IOException {
        try {
            provider.removeTable("non-existent");
        } finally {
            System.out.println(writer.toString());
        }
    }

    @After
    public void tearDown() {
        DirectoryKiller.delete(testDir.toFile());
    }
}
