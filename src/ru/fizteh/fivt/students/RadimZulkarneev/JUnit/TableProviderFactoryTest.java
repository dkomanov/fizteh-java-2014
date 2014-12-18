package ru.fizteh.fivt.students.RadimZulkarneev.JUnit;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.RadimZulkarneev.DataBase.TableProviderFactoryRealize;

public class TableProviderFactoryTest {

    private Path factoryPath;
    private TableProviderFactory tpFactory;
    private final String testDir = "factoryTestDirectory";
    @Before
    public void setUp() {
        factoryPath = Paths.get(System.getProperty("java.io.tmpdir"), testDir);
        tpFactory = null;
    }

    @Test
    public void tableProviderFactoryTest() {
        TableProviderFactory tpFactory = new TableProviderFactoryRealize();
    }

    @Test
    public void tableProviderFactoryCreateBaseTest() {
        TableProviderFactory tpFactory = new TableProviderFactoryRealize();
        tpFactory.create(factoryPath.toString());
        assertTrue(factoryPath.toFile().exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tableProviderFactoryThrowsExceptionForNullArgument() {
        TableProviderFactory tpFactory = new TableProviderFactoryRealize();
        tpFactory.create(null);
    }

    @After
    public void tearDown() {
        factoryPath.toFile().delete();
    }
}
