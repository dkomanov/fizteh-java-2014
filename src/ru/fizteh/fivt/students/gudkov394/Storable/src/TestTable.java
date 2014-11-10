package ru.fizteh.fivt.students.gudkov394.Storable.src;

/**
 * Created by kagudkov on 25.10.14.
 */

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.gudkov394.Storable.src.Junit;
import ru.fizteh.fivt.students.gudkov394.Storable.src.TableProviderClass;
import ru.fizteh.fivt.students.gudkov394.Storable.src.Utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class TestTable {
    private static Table table;
    private TableProviderClass provider = null;
    @Rule
    public TemporaryFolder rootDBDirectory = new TemporaryFolder();

    @Before
    public void createTable() throws IOException {
        File newTable = rootDBDirectory.newFolder("testTable");
        provider = new Junit().create("/home/kagudkov/fizteh-java-2014/test1");
        Utils utils = new Utils();
        provider.createTable("testTable", utils.signature("int int"));
        table = provider.getTable("testTable");
    }

    @Test
    public void getName() {
        Assert.assertEquals("testTable", table.getName());
    }

    @Test
    public void getNotExistingKey() {
        Assert.assertNull(table.get("[1, 1]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullKey() {
        table.get(null);
    }

    @Test
    public void putNewKey() throws ParseException {
        Assert.assertNull(table.put("newTestKey", provider.deserialize(table, "[1,1]")));
        String valueOfNewTestKey = provider.serialize(table, table.get("newTestKey"));
        Assert.assertEquals("[1,1]", valueOfNewTestKey);
        table.remove("newTestKey");
        Assert.assertNull(table.get("newTestKey"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKey() throws ParseException {
        table.put(null, provider.deserialize(table, "[1 , 2]"));
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
    public void sizeWork() throws ParseException {
        Assert.assertEquals(0, table.size());
        table.put("1", provider.deserialize(table, "[1,1]"));
        table.put("2", provider.deserialize(table, "[1,2]"));
        table.put("3", provider.deserialize(table, "[1,3]"));
        Assert.assertEquals(3, table.size());
        table.remove("1");
        Assert.assertEquals(2, table.size());
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void commitWork() throws ParseException, IOException {
        table.put("1", provider.deserialize(table, "[1,1]"));
        table.put("2", provider.deserialize(table, "[1,2]"));
        table.put("3", provider.deserialize(table, "[1,3]"));
        Assert.assertEquals(3, table.commit());
        table.remove("1");
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void rollBackWork() throws ParseException, IOException {
        table.put("new1", provider.deserialize(table, "[2,1]"));
        table.put("new2", provider.deserialize(table, "[2,2]"));
        table.put("new3", provider.deserialize(table, "[2,3]"));
        Assert.assertEquals(3, table.rollback());
        Assert.assertEquals(0, table.rollback());
        table.put("new1", provider.deserialize(table, "[2,1]"));
        table.commit();
        table.remove("new1");
        table.rollback();
        Assert.assertEquals("[2,1]", provider.serialize(table, table.get("new1")));
        Storeable oldValue = table.put("new2", provider.deserialize(table, "[2,2]"));
        Assert.assertNull(oldValue);
    }
}
