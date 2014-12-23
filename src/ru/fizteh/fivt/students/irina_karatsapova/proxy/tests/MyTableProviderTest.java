package ru.fizteh.fivt.students.irina_karatsapova.proxy.tests;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.table_provider_factory.MyTableProviderFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MyTableProviderTest {

    TemporaryFolder tempFolder = new TemporaryFolder();
    String tableName = "table";
    String anotherTableName = "another table name";
    TableProvider provider;
    Table table;
    List<Class<?>> types = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        tempFolder.create();
        File providerDir = tempFolder.newFolder();
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(providerDir.toString());
        Class[] classes = {Integer.class, String.class, Boolean.class};
        for (Class type: classes) {
            types.add(type);
        }
        table = provider.createTable(tableName, types);
    }

    @After
    public void tearDown() throws Exception {
        tempFolder.delete();
    }

    @Test
    public void testCreateTable() throws Exception {
        assertNotNull(provider.createTable(anotherTableName, types));
        Assert.assertNull(provider.createTable(anotherTableName, types));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTable() throws Exception {
        provider.createTable(null, null);
    }

    @Test
    public void testGetTable() throws Exception {
        Assert.assertNull(provider.getTable(anotherTableName));
        Table anotherTable = provider.createTable(anotherTableName, types);
        Assert.assertEquals(anotherTable, provider.getTable(anotherTableName));
    }

    @Test
    public void testCloseAndGet() throws Exception {
        Table sameTable = provider.getTable(tableName);
        assertSame(table, sameTable);
        table.close();
        Table newTable = provider.getTable(tableName);
        assertNotSame(table, newTable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
        provider.getTable(null);
    }

    @Test
    public void testRemoveTable() throws Exception {
        assertNotNull(provider.getTable(tableName));
        provider.removeTable(tableName);
        Assert.assertNull(provider.getTable(tableName));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingTable() throws Exception {
        provider.removeTable(anotherTableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullTable() throws Exception {
        provider.removeTable(null);
    }

    @Test
    public void testSerializeDeserialize() throws Exception {
        Class<?>[] classes = {String.class, Boolean.class};
        Table table = createTable(anotherTableName, classes);
        String simple = "<row><col>hello</col><col>true</col></row>";
        Assert.assertEquals(simple, provider.serialize(table, provider.deserialize(table, simple)));
    }

    @Test
    public void testSerializeDeserializeNull() throws Exception {
        Class<?>[] classes = {Long.class};
        Table table = createTable(anotherTableName, classes);
        String simple = "<row><null/></row>";
        Assert.assertEquals(simple, provider.serialize(table, provider.deserialize(table, simple)));
    }

    @Test
    public void testDeserialize() throws Exception {
        Class<?>[] classes = {String.class, Double.class, Float.class, Float.class, Integer.class, Long.class,
                              Boolean.class, Boolean.class, Byte.class};
        Table table = createTable(anotherTableName, classes);
        String value = "<row><col>hello</col>"
                        + "<col>5.5</col>"
                        + "<col>1.5555</col>"
                        + "<col>0.0001</col>"
                        + "<null/>"
                        + "<col>100000000000</col>"
                        + "<col>whatever-but-not-true</col>"
                        + "<col>True</col>"
                        + "<col>127</col></row>";
        Storeable tableRaw = provider.deserialize(table, value);
        Assert.assertEquals("hello", tableRaw.getStringAt(0));
        Assert.assertEquals(5.5, tableRaw.getDoubleAt(1));
        Assert.assertEquals(1.5555f, tableRaw.getFloatAt(2));
        Assert.assertEquals(1e-4f, tableRaw.getFloatAt(3));
        Assert.assertNull(tableRaw.getIntAt(4));
        Assert.assertEquals((Long) 100000000000L, tableRaw.getLongAt(5));
        Assert.assertEquals((Boolean) false, tableRaw.getBooleanAt(6));
        Assert.assertEquals((Boolean) true, tableRaw.getBooleanAt(7));
        Assert.assertEquals((Byte) (byte) 127, tableRaw.getByteAt(8));
    }

    @Test
    public void testCreateForEmpty() throws Exception {
        Storeable tableRaw = provider.createFor(table);
        for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
            Assert.assertNull(tableRaw.getColumnAt(columnIndex));
        }
    }

    @Test
    public void testCreateForWithValue() throws Exception {
        Object[] valuesArray = {5, "hello", false};
        List<Object> values = new ArrayList<>();
        for (Object value: valuesArray) {
            values.add(value);
        }
        Storeable tableRaw = provider.createFor(table, values);
        for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
            Assert.assertEquals(values.get(columnIndex), tableRaw.getColumnAt(columnIndex));
        }
    }

    @Test(expected = ColumnFormatException.class)
    public void testCreateForWithWrongValuesTypes() throws Exception {
        Object[] valuesArray = {"125", "hello", false};
        List<Object> values = new ArrayList<>();
        for (Object value: valuesArray) {
            values.add(value);
        }
        provider.createFor(table, values);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testCreateForWithMoreThanExpectedValues() throws Exception {
        Object[] valuesArray = {5, "hello", false, 5};
        List<Object> values = new ArrayList<>();
        for (Object value: valuesArray) {
            values.add(value);
        }
        provider.createFor(table, values);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testCreateForWithLessThanExpectedValues() throws Exception {
        Object[] valuesArray = {5, "hello"};
        List<Object> values = new ArrayList<>();
        for (Object value: valuesArray) {
            values.add(value);
        }
        provider.createFor(table, values);
    }

    @Test
    public void testGetTableNames() throws Exception {
        String[] goodTableNames = {"table", "name", "123", "db № 4 with spaces", "null", "1.a"};
        for (String tableName : goodTableNames) {
            provider.createTable(tableName, types);
        }
        List<String> providerTableNames = provider.getTableNames();
        Assert.assertEquals(goodTableNames.length, providerTableNames.size());
        for (String tableName : goodTableNames) {
            assertTrue(providerTableNames.contains(tableName));
        }
    }

    @Test
    public void testToString() throws Exception {
        String providerStr = provider.toString();
        Assert.assertTrue(providerStr.startsWith("MyTableProvider["));
        Assert.assertTrue(providerStr.endsWith("]"));
        String path = providerStr.substring(providerStr.indexOf('[') + 1, providerStr.lastIndexOf(']'));
        File file = Paths.get(path).toFile();
        Assert.assertTrue(file.isAbsolute());
    }

    @Test(expected = IllegalStateException.class)
     public void testCloseForTables() throws Exception {
        provider.close();
        table.get("1");
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseForProvider() throws Exception {
        provider.close();
        provider.getTable(tableName);
    }



    private Table createTable(String tableName, Class[] tableClasses) throws Exception {
        List<Class<?>> tableTypes = new ArrayList<>();
        for (Class type: tableClasses) {
            tableTypes.add(type);
        }
        return provider.createTable(tableName, tableTypes);
    }
}
