package ru.fizteh.fivt.students.vladislav_korzun.JUnit;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactoryTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "TestDir");

    @Before
    public void setUp() {
        testDir.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableProviderFactoryTestTest() throws Exception {
        TableProviderFactory test = new MyTableProviderFactory();
        test.create(null);
    }
    
    @Test
    public void createNewTableProviderFactoryTestTest() throws Exception {
        TableProviderFactory test = new MyTableProviderFactory();
        TableProvider testProvider = test.create(testDir.toString());
        testProvider.createTable("testTable");
        assertTrue(!testDir.resolve("testTable").toFile().exists());
    }
    
    @After
    public void remodeDat() {
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
