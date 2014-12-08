package ru.fizteh.fivt.students.LevkovMiron.StorableTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.LevkovMiron.Storeable.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Мирон on 11.11.2014 ru.fizteh.fivt.students.LevkovMiron.StorableTest.
 */
public class TableTest {

    private CTableProvider provider;
    private ArrayList<Class<?>> classes = new ArrayList<>();
    private CTable table;
    private Parser parser = new Parser();

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
        f.mkdir();
        provider = (CTableProvider) new CTableProviderFactory().create(f.getAbsolutePath());
        classes.add(Integer.class);
        classes.add(Double.class);
        classes.add(String.class);
        provider.createTable("t", classes);
        table = (CTable) provider.getTable("t");
    }

    @After
    public void afterTest() {
        File f = new File("StoreableTestDir");
        drop(f);
    }

    @Test (expected = IOException.class)
    public void tableTest() throws IOException {
        new CTable(new File("!!!/###").toPath());
    }

    @Test
    public void getTest() throws ParseException {
        Assert.assertNull(table.get("aba"));
    }

    @Test
    public void putTest() throws ColumnFormatException, ParseException {
        table.put("a", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        Assert.assertEquals("[1,0.5,\"aba\"]", parser.serialize(table, table.get("a")));
        table.put("a", parser.deserialize(table, "[-1, 1.5, \"\"]"));
        Assert.assertEquals("[-1,1.5,\"\"]", parser.serialize(table, table.get("a")));
    }

    @Test(expected = ParseException.class)
    public void putExceptionTest() throws ColumnFormatException, ParseException {
        table.put("a", parser.deserialize(table, "[0.5, 0.5, \"aba\"]"));
    }

    @Test
    public void removeTest() throws ParseException {
        table.put("a", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        table.put("b", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        table.remove("a");
        Assert.assertEquals(table.size(), 1);
        Assert.assertNull(table.get("a"));
    }

    @Test
    public void getNameTest() {
        Assert.assertEquals(table.getName(), "t");
    }

    @Test
    public void sizeTest() throws ParseException {
        Assert.assertEquals(table.size(), 0);
        table.put("a", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        table.put("b", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        Assert.assertEquals(table.size(), 2);
        table.remove("a");
        Assert.assertEquals(table.size(), 1);
    }

    @Test
    public void getColumnsCountTest() {
        Assert.assertEquals(table.getColumnsCount(), 3);
    }

    @Test
    public void getColumnTypeTest() throws IndexOutOfBoundsException {
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(classes.get(i), table.getColumnType(i));
        }
    }

    @Test
    public void commitTest() throws IOException, ParseException {
        table.put("a", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        table.commit();
        Assert.assertEquals(table.size(), 1);
        table.put("b", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        Assert.assertEquals(table.size(), 2);
        table.put("b", parser.deserialize(table, "[2, 0.5, \"aba\"]"));
        table.remove("a");
        table.commit();
        Assert.assertEquals(table.size(), 1);
    }

    @Test
    public void rollbackTest() throws ParseException {
        table.put("a", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        table.put("b", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        table.put("b", parser.deserialize(table, "[2, 0.5, \"aba\"]"));
        table.rollback();
        Assert.assertEquals(table.size(), 0);
    }

    @Test
    public void listTest() throws ParseException {
        table.put("a", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        table.put("b", parser.deserialize(table, "[1, 0.5, \"aba\"]"));
        table.put("b", parser.deserialize(table, "[2, 0.5, \"aba\"]"));
        HashSet<String> set = new HashSet<String>(Arrays.asList("a", "b"));
        for (String s : table.list()) {
            Assert.assertTrue(set.contains(s));
        }
        Assert.assertEquals(set.size(), table.list().size());
    }

    @Test
    public void dropTest() throws IOException {
        table.drop();
        File f = new File("StoreableTestDir/t");
        Assert.assertFalse(f.exists());
    }

}
