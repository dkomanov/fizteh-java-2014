package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap.MFileHashMapFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.shell.FileUtils;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TableProviderTest {
    String providerDirectory;
    String tableName;
    TableProvider provider;
    TableProviderFactory factory;

    @Before
    public void setUp() {
        providerDirectory = Paths.get("").resolve("provider").toString();
        tableName = "testTable";
        factory = new MFileHashMapFactory();
        provider = factory.create(providerDirectory);
    }

    @After
    public void tearDown() {
        FileUtils.rmdir(Paths.get(providerDirectory));
        FileUtils.mkdir(Paths.get(providerDirectory));
    }

    @Test
    public void testGetTable() throws Exception {
        assertNull(provider.getTable(tableName));
        assertNotNull(provider.createTable(tableName));
        assertNotNull(provider.getTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableDirExists() throws Exception {
        FileUtils.mkdir(Paths.get(providerDirectory, tableName));
        provider.createTable(tableName);
    }

    @Test
    public void testRemoveTable() throws Exception {

    }
}
