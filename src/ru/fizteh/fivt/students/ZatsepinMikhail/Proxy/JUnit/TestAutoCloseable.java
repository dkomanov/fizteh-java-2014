package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.JUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMapFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestAutoCloseable {
    String providerDirectory;
    String tableName;

    Table testTable;
    TableProvider provider;
    TableProviderFactory factory;

    static List<Class<?>> typeList;


    @BeforeClass
    public static void setUpBeforeClass() {
        typeList = new ArrayList<>();
        typeList.add(Integer.class);
        typeList.add(String.class);
        typeList.add(Boolean.class);
    }

    @Before
    public void setUp() {
        providerDirectory = Paths.get("").resolve("provider").toString();
        tableName = "testTable";
        factory = new MFileHashMapFactory();
        try {
            provider = factory.create(providerDirectory);
            testTable = provider.createTable(tableName, typeList);
            if (testTable == null) {
                testTable = provider.getTable(tableName);
            }
        } catch (IOException e) {
            //suppress
        }
    }

    @After
    public void tearDown() {
        try {
            provider.removeTable(tableName);
        } catch (IOException e) {
            //suppress
        } catch (IllegalStateException e) {
            //suppress
        }
    }

    @Test (expected = IllegalStateException.class)
    public void testFileMapRunMethodAfterClose() {
        FileMap testFileMap = (FileMap) testTable;
        try {
            testFileMap.close();
        } catch (Exception e) {
            //suppress
        }
        try {
            testFileMap.commit();
        } catch (IOException e) {
            //suppress
        }
    }

    @Test
    public void testFileMapRunDoubleClose() {
        FileMap testFileMap = (FileMap) testTable;
        try {
            testFileMap.close();
        } catch (Exception e) {
            //suppress
        }
        boolean exceptionWas = false;
        try {
            testFileMap.close();
        } catch (IllegalStateException e) {
            exceptionWas = true;
        } catch (Exception e) {
            //suppress
        }
        assertTrue(exceptionWas);
    }

    @Test (expected = IllegalStateException.class)
    public void testMFileHashMapClose() {
       MFileHashMap testMFileHashMap = (MFileHashMap) provider;
       try {
           testMFileHashMap.close();
       } catch (Exception e) {
           //suppress
       }
       testMFileHashMap.getTable("random");
    }

    @Test (expected = IllegalStateException.class)
    public void testMFileHashMapFactoryClose() {
        MFileHashMapFactory testMFileHashMapFactory = (MFileHashMapFactory) factory;
        try {
            testMFileHashMapFactory.close();
        } catch (Exception e) {
            //suppress
        }
        try {
            testMFileHashMapFactory.create("random");
        } catch (IOException e) {
            //suppress
        }
    }
}
