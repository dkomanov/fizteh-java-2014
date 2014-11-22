package ru.fizteh.fivt.students.VasilevKirill.Storeable.JUnitTests;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.MultiMap;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class TableProviderTest {
    private static TableProvider tableProvider;
    private static String path;
    private static List<Class<?>> typeList;

    static {
        try {
            path = new File("").getCanonicalPath();
            tableProvider = new MultiMap(path);
            typeList.add(Integer.class);
            typeList.add(String.class);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void testGetTable() throws Exception {
        tableProvider.createTable("First", typeList);
        Table table = tableProvider.getTable("First");
    }

    @Test
    public void testCreateTable() throws Exception {

    }

    @Test
    public void testRemoveTable() throws Exception {

    }

    @Test
    public void testDeserialize() throws Exception {

    }

    @Test
    public void testSerialize() throws Exception {

    }

    @Test
    public void testCreateFor() throws Exception {

    }

    @Test
    public void testCreateFor1() throws Exception {

    }
}
