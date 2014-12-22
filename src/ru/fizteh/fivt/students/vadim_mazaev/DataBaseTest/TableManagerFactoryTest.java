package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManagerFactory;

public class TableManagerFactoryTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DbTestDir");

    @Before
    public void setUp() {
        testDir.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerFactoryThrowsExceptionCreatedNullTableManager() throws IOException {
        TableProviderFactory test = new TableManagerFactory();
        test.create(null);
    }
    
    @Test
    public void testTableManagerFactoryCreatedNewTableManager() throws IOException {
        TableProviderFactory test = new TableManagerFactory();
        assertNotNull(test.create(testDir.toString()));
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
