package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.JUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMapFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.shell.FileUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestStoreable {
    static String providerDirectory;
    static String tableName;
    static TableProviderFactory factory;
    static TableProvider provider;
    static Table table;
    static Storeable entry;
    static List<Class<?>> typeList;

    @Before
    public void setUp() throws Exception {
        providerDirectory = Paths.get("").resolve("provider").toString();
        tableName = "testTable2";
        factory = new MFileHashMapFactory();
        typeList = new ArrayList<>();
        typeList.add(Integer.class);
        typeList.add(String.class);
        try {
            provider = factory.create(providerDirectory);
            table = provider.createTable(tableName, typeList);
            entry = provider.createFor(table);
        } catch (IOException e) {
            //suppress
        }
    }

    @After
    public void tearDown() throws Exception {
        try {
            FileUtils.rmdir(Paths.get(providerDirectory));
        } catch (IOException e) {
            //suppress
        }
        FileUtils.mkdir(Paths.get(providerDirectory));
    }

    @Test
    public void testSetColumnAt() throws Exception {
        entry.setColumnAt(0, null);
        boolean exceptionWas = false;
        try {
            entry.setColumnAt(0, "new");
        } catch (ColumnFormatException e) {
            exceptionWas = true;
        }
        assertTrue(exceptionWas);
        exceptionWas = false;
        try {
            entry.setColumnAt(2, "new");
        } catch (IndexOutOfBoundsException e) {
            exceptionWas = true;
        }
        assertTrue(exceptionWas);
    }

    @Test
    public void testGetColumnAt() throws Exception {
        entry.setColumnAt(0, 1);
        assertTrue((entry.getColumnAt(0)).equals(1));

        boolean exceptionWas = false;
        try {
            entry.getColumnAt(2);
        } catch (IndexOutOfBoundsException e) {
            exceptionWas = true;
        }
        assertTrue(exceptionWas);
    }

    @Test
    public void testGetIntAt() throws Exception {
        entry.setColumnAt(0, 1);
        entry.setColumnAt(1, "new");
        assertTrue(entry.getIntAt(0) == 1);

        boolean exceptionWas = false;
        try {
            entry.getIntAt(1);
        } catch (ColumnFormatException e) {
            exceptionWas = true;
        }
        assertTrue(exceptionWas);

        exceptionWas = false;
        try {
            entry.getIntAt(100);
        } catch (IndexOutOfBoundsException e) {
            exceptionWas = true;
        }
        assertTrue(exceptionWas);
    }
}
