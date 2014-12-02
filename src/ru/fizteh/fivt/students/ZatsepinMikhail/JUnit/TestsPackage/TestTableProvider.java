package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.TestsPackage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.MultiFileHashMapPackage.MFileHashMapFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.ShellPackage.FileUtils;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TestTableProvider {
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
    public void testCreateDouble() throws Exception {
        provider.createTable(tableName);
        assertNull(provider.createTable(tableName));
    }

    @Test
    public void testCreateTableDirNotExists() throws Exception {
        FileUtils.rmdir(Paths.get(providerDirectory, tableName));
        assertNotNull(provider.createTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws Exception {
        provider.createTable(null);
    }

    @Test
    public void testRemoveTableDirExists() throws Exception {
        provider.createTable(tableName);
        provider.removeTable(tableName);
        assertNull(provider.getTable(tableName));
        assertTrue(!Files.exists(Paths.get(providerDirectory, tableName)));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableDirNotExists() throws Exception {
        FileUtils.rmdir(Paths.get(providerDirectory, tableName));
        provider.removeTable(tableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws Exception {
        provider.removeTable(null);
    }
}
