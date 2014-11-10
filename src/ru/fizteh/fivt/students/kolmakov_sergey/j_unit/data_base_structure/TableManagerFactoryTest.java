package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class TableManagerFactoryTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DataBaseTestDirectory");
    private final String tableName = "table";

    @Before
    public void setUp() {
        testDir.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerFactoryThrowsExceptionWhenArgumentIsNull() {
        TableProviderFactory test = new TableManagerFactory();
        test.create(null);
    }

    @Test
    public void testTableManagerFactoryCreateMethod() {
        TableProviderFactory test = new TableManagerFactory();
        TableProvider testProvider = test.create(testDir.toString());
        testProvider.createTable(tableName);
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
