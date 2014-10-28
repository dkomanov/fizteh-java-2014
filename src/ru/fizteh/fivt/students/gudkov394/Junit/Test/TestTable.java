package ru.fizteh.fivt.students.gudkov394.Junit.Test;

/**
 * Created by kagudkov on 25.10.14.
 */

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.gudkov394.Junit.src.Junit;

import java.io.File;
import java.io.IOException;

public class TestTable {
    private static Table table;
    @Rule
    public TemporaryFolder rootDBDirectory = new TemporaryFolder();

    @Before
    public void createTable() throws IOException {
        File newTable = rootDBDirectory.newFolder("testTable");
        TableProviderFactory factory = new Junit();
        TableProvider provider = factory.create("/home/kagudkov/fizteh-java-2014/test1");
        provider.createTable("testTable");
        table = provider.getTable("testTable");
    }

    @Test
    public void getName() {
        Assert.assertEquals("testTable", table.getName());
    }

    @Test
    public void getNotExistingKey() {
        Assert.assertNull(table.get("notExistingKey"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullKey() {
        table.get(null);
    }

    @Test
    public void putNewKey() {
        Assert.assertNull(table.put("newTestKey", "newValue"));
        table.remove("newTestKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKey() {
        table.put(null, "value");
    }

    @Test
    public void removeNotExistingKey() {
        Assert.assertNull(table.remove("notExistingKey"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKey() {
        table.remove(null);
    }

    @Test
    public void sizeWork() {
        Assert.assertEquals(0, table.size());
        table.put("1", "1");
        table.put("2", "2");
        table.put("3", "3");
        Assert.assertEquals(3, table.size());
        table.remove("1");
        Assert.assertEquals(2, table.size());
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void commitWork() {
        table.put("1", "1");
        table.put("2", "2");
        table.put("3", "3");
        Assert.assertEquals(3, table.commit());
        table.remove("1");
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void rollBackWork() {
        table.put("new1", "1");
        table.put("new2", "2");
        table.put("new3", "3");
        Assert.assertEquals(3, table.rollback());
        Assert.assertEquals(0, table.rollback());
        table.put("new1", "1");
        table.commit();
        table.remove("new1");
        table.rollback();
        Assert.assertEquals("1", table.get("new1"));
        String oldValue = table.put("new2", "2");
        Assert.assertNull(oldValue);
    }

    @Test
    public void bigTest() {
        for (int i = 0; i < 100; ++i) {
            table.put("new" + ((Integer) i).toString(), ((Integer) i).toString());
        }
        Assert.assertEquals(table.commit(), 100);
        Assert.assertEquals(table.size(), 100);
        for (int i = 0; i < 100; ++i) {
            table.remove("new" + ((Integer) i).toString());
        }
        Assert.assertEquals(0, table.size());
    }

}
