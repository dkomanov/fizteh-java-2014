package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap.MFileHashMapFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.shell.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestTableProvider {
    String providerDirectory;
    String tableName;
    TableProvider provider;
    TableProviderFactory factory;

    static List<Class<?>> typeList;

    @BeforeClass
    public static void setUpBeforeClass() {
        typeList = new ArrayList<>();
        typeList.add(Integer.class);
        typeList.add(String.class);
    }

    @Before
    public void setUp() {
        providerDirectory = Paths.get("").resolve("provider").toString();
        tableName = "testTable";
        factory = new MFileHashMapFactory();
        try {
            provider = factory.create(providerDirectory);
        } catch (IOException e) {
            //suppress
        }
    }

    @After
    public void tearDown() {
        try {
            FileUtils.rmdir(Paths.get(providerDirectory));
        } catch (IOException e) {
            //suppress
        }
        FileUtils.mkdir(Paths.get(providerDirectory));
    }

    @Test
    public void testGetTable() throws Exception {
        assertNull(provider.getTable(tableName));
        assertNotNull(provider.createTable(tableName, typeList));
        assertNotNull(provider.getTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableDirExists() throws Exception {
        FileUtils.mkdir(Paths.get(providerDirectory, tableName));
        provider.createTable(tableName, typeList);
    }

    @Test
    public void testCreateDouble() throws Exception {
        provider.createTable(tableName, typeList);
        assertNull(provider.createTable(tableName, typeList));
    }

    @Test
    public void testCreateTableDirNotExists() throws Exception {
        try {
            FileUtils.rmdir(Paths.get(providerDirectory, tableName));
        } catch (IllegalArgumentException e) {
            //suppress - means directory doesn't exist
        }
        assertNotNull(provider.createTable(tableName, typeList));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNullDirectory() throws Exception {
        provider.createTable(null, typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNullTypeList() throws Exception {
        provider.createTable(tableName, null);
    }

    @Test
    public void testRemoveTableExists() throws Exception {
        provider.createTable(tableName, typeList);
        provider.removeTable(tableName);
        assertNull(provider.getTable(tableName));
        assertTrue(!Files.exists(Paths.get(providerDirectory, tableName)));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableNotExists() throws Exception {
        provider.removeTable(tableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws Exception {
        provider.removeTable(null);
    }
}
