package ru.fizteh.fivt.students.LevkovMiron.ParallelTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.LevkovMiron.Parallel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Мирон on 11.11.2014 ru.fizteh.fivt.students.LevkovMiron.ParallelTest.
 */
public class TableProviderTest {
    private CTableProvider provider;
    private ArrayList<Class<?>> classes = new ArrayList<>();
    private CTable table;
    private void drop(File f) {
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {
            for (File inFile : f.listFiles()) {
                drop(inFile);
            }
        }
        f.delete();
    }
    @Before
    public void beforeTest() throws IOException {
        File f = new File("StoreableTestDir");
        drop(f);
        f.mkdir();
        provider = (CTableProvider) new CTableProviderFactory().create(f.getAbsolutePath());
        classes.add(Integer.class);
        classes.add(Long.class);
        classes.add(String.class);
        provider.createTable("t", classes);
        table = (CTable) provider.getTable("t");
    }

    @After
    public void afterTest() {
        File f = new File("StoreableTestDir");
        drop(f);
    }

    @Test
    public void showTablesTest() throws IOException {
        classes.add(Long.class);
        classes.add(Double.class);
        provider.createTable("t2", classes);
        classes.add(Boolean.class);
        classes.add(Byte.class);
        classes.add(Float.class);
        provider.createTable("t3", classes);
        provider.createTable("t4", classes);
        Set<String> set = provider.showTables();
        HashSet<String> result = new HashSet<>();
        result.add("t");
        result.add("t2");
        result.add("t3");
        result.add("t4");
        Assert.assertEquals(set, result);

    }

    @Test
    public void removeTest() throws IOException {
        provider.removeTable("t");
        Assert.assertNull(provider.getTable("t"));
        Assert.assertFalse(new File("StoreableTestDir/t").exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeExceptionTest() throws IllegalArgumentException, IOException {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void removeExceptionTest2() throws IllegalStateException, IOException {
        provider.removeTable("abc");
    }

    @Test
    public void createTest() {
        Assert.assertNotNull(table);
        Assert.assertTrue(new File("StoreableTestDir/t").exists());
    }

    @Test
    public void createForTest() {
        Assert.assertNotNull(provider.createFor(table));
    }

    @Test
    public void createForListTest() {
        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        list.add(1L << 40);
        list.add("SS");
        CStoreable storeable1 = (CStoreable) provider.createFor(table, list);
        CStoreable storeable2 = new CStoreable(list);
        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals(storeable1.getColumnAt(i), storeable2.getColumnAt(i));
        }
    }

    @Test(expected = ColumnFormatException.class)
    public void createForExceptionTest() {
        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        list.add("s");
        list.add("SS");
        provider.createFor(table, list);
    }
}
