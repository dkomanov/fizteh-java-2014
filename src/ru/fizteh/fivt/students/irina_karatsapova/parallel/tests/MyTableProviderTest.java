package ru.fizteh.fivt.students.irina_karatsapova.parallel.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.table_provider_factory.MyTableProviderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        assertNull(provider.createTable(anotherTableName, types));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTable() throws Exception {
        provider.createTable(null, null);
    }

    @Test
    public void testGetTable() throws Exception {
        assertNull(provider.getTable(anotherTableName));
        Table anotherTable = provider.createTable(anotherTableName, types);
        assertEquals(anotherTable, provider.getTable(anotherTableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
        provider.getTable(null);
    }

    @Test
    public void testRemoveTable() throws Exception {
        assertNotNull(provider.getTable(tableName));
        provider.removeTable(tableName);
        assertNull(provider.getTable(tableName));
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
        assertEquals(simple, provider.serialize(table, provider.deserialize(table, simple)));
    }

    @Test
    public void testSerializeDeserializeNull() throws Exception {
        Class<?>[] classes = {Long.class};
        Table table = createTable(anotherTableName, classes);
        String simple = "<row><null/></row>";
        assertEquals(simple, provider.serialize(table, provider.deserialize(table, simple)));
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
        assertEquals("hello", tableRaw.getStringAt(0));
        assertEquals(5.5, tableRaw.getDoubleAt(1));
        assertEquals(1.5555f, tableRaw.getFloatAt(2));
        assertEquals(1e-4f, tableRaw.getFloatAt(3));
        assertNull(tableRaw.getIntAt(4));
        assertEquals((Long) 100000000000L, tableRaw.getLongAt(5));
        assertEquals((Boolean) false, tableRaw.getBooleanAt(6));
        assertEquals((Boolean) true, tableRaw.getBooleanAt(7));
        assertEquals((Byte) (byte) 127, tableRaw.getByteAt(8));
    }

    @Test
    public void testCreateForEmpty() throws Exception {
        Storeable tableRaw = provider.createFor(table);
        for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
            assertNull(tableRaw.getColumnAt(columnIndex));
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
            assertEquals(values.get(columnIndex), tableRaw.getColumnAt(columnIndex));
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
        assertEquals(goodTableNames.length, providerTableNames.size());
        for (String tableName : goodTableNames) {
            assertTrue(providerTableNames.contains(tableName));
        }
    }

    private Table createTable(String tableName, Class[] tableClasses) throws Exception {
        List<Class<?>> tableTypes = new ArrayList<>();
        for (Class type: tableClasses) {
            tableTypes.add(type);
        }
        return provider.createTable(tableName, tableTypes);
    }
}
