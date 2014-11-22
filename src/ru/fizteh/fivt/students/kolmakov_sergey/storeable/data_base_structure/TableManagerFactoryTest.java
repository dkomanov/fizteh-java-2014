package ru.fizteh.fivt.students.kolmakov_sergey.storeable.data_base_structure;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TableManagerFactoryTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DataBaseTestDirectory");
    private final String tableName = "table";
    private List<String> values = new ArrayList<>();
    private List<Class<?>> columnTypes = new ArrayList<>();

    @Before
    public void setUp() {
        columnTypes.add(String.class);
        values.add("\"string value\"");
        testDir.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerFactoryThrowsExceptionWhenArgumentIsNull() throws IOException {
        TableProviderFactory test = new TableManagerFactory();
        test.create(null);
    }

    @Test
    public void testTableManagerFactoryCreateMethod() throws IOException {
        TableProviderFactory test = new TableManagerFactory();
        TableProvider testProvider = test.create(testDir.toString());
        testProvider.createTable(tableName, columnTypes);
        assertTrue(testDir.resolve(tableName).toFile().exists());
    }

    @After
    public void tearDown() {
        for (File curFile : testDir.toFile().listFiles()) {
            if (curFile.isDirectory()) {
                for (File subFile : curFile.listFiles()) {
                    subFile.delete();
                }
            }
            curFile.delete();
        }
        testDir.toFile().delete();
    }
}
