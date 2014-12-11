package storeable.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import storeable.structured.ColumnFormatException;
import storeable.structured.Storeable;
import storeable.structured.Table;
import storeable.structured.TableProvider;
import storeable.structured.TableProviderFactory;
import storeable.util.MyTableProviderFactory;

public class TableProviderTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public TableProvider provider;
    public TableProviderFactory factory;
    public String providerPath;

    @Before
    public void initProvider() throws IOException {
        factory = new MyTableProviderFactory();
        providerPath = tmpFolder.newFolder().getAbsolutePath();
        provider = factory.create(providerPath);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTable() {
        provider.getTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createNullTable() throws IOException {
        provider.createTable(null, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void removeNullTable() throws IOException {
        provider.removeTable(null);
    }

    @Test
    public void createTableAndReReadSignature() throws IOException {
        Class<?>[] types = {Integer.class, Float.class, Double.class, Boolean.class, String.class, Byte.class};
        provider.createTable("table", Arrays.asList(types));
        TableProvider sameProvider = factory.create(providerPath);
        Table table = sameProvider.getTable("table");
        assertEquals(types.length, table.getColumnsCount());
        for (int i = 0; i < types.length; i++) {
            assertEquals(table.getColumnType(i), types[i]);
        }
    }

    @Test
    public void doubleTableCreate() throws IOException {
        Class<?>[] types = {Integer.class, Float.class, Double.class, Boolean.class, String.class, Byte.class};
        assertNotNull(provider.createTable("table", Arrays.asList(types)));
        assertNull(provider.createTable("table", Arrays.asList(types)));
    }

    @Test
    public void createAndRemoveTable() throws IOException {
        Class<?>[] types = {String.class, Boolean.class};
        provider.createTable("table", Arrays.asList(types));
        assertNotNull(provider.getTable("table"));
        provider.removeTable("table");
        assertNull(provider.getTable("table"));
        TableProvider sameProvider = factory.create(providerPath);
        assertNull(sameProvider.getTable("table"));
    }

    @Test
    public void testSerialize() throws IOException {
        Class<?>[] types = {Integer.class, Float.class, Double.class, Boolean.class, String.class, Byte.class};
        Object[] values = {3, 5.2f, 5.4, true, "hello", null};
        Table table = provider.createTable("table", Arrays.asList(types));
        Storeable storeable = provider.createFor(table, Arrays.asList(values));
        String result = provider.serialize(table, storeable);
        assertEquals(result,
                "<row><col>3</col><col>5.2</col><col>5.4</col><col>true</col><col>hello</col><null/></row>");
    }

    @Test
    public void testDeserialize() throws IOException, ParseException {
        Class<?>[] types = {Integer.class, Float.class, Double.class, Boolean.class, String.class, Byte.class};
        Table table = provider.createTable("table", Arrays.asList(types));
        String xml = "<row><col>3</col><col>5.2</col><col>5.4</col><col>true</col><col>hello</col><null/></row>";
        Storeable storeable = provider.deserialize(table, xml);
        assertEquals(storeable.getIntAt(0), (Integer) 3);
        assertEquals(storeable.getFloatAt(1), (Float) 5.2f);
        assertEquals(storeable.getDoubleAt(2), (Double) 5.4);
        assertEquals(storeable.getBooleanAt(3), true);
        assertEquals(storeable.getStringAt(4), "hello");
        assertEquals(storeable.getByteAt(5), null);
    }

    @Test (expected = ColumnFormatException.class)
    public void testSerializeIncorrectNumberOfValues() throws IOException {
        Class<?>[] types = {Integer.class, Float.class, Double.class, Boolean.class, String.class, Byte.class};
        Object[] values = {3, 5.2f, 5.4, true, "hello", null, null};
        Table table = provider.createTable("table", Arrays.asList(types));
        Class<?>[] anotherTypes = {Integer.class, Float.class, Double.class,
                            Boolean.class, String.class, Byte.class, Byte.class};
        Table anotherTable = provider.createTable("anotherTable", Arrays.asList(anotherTypes));
        Storeable storeable = provider.createFor(anotherTable, Arrays.asList(values));
        provider.serialize(table, storeable);
    }

    @Test (expected = ColumnFormatException.class)
    public void testCreateForIncorrectTypes() throws IOException {
        Class<?>[] types = {Integer.class, Boolean.class};
        Object[] values = {3, 5.2f};
        Table table = provider.createTable("table", Arrays.asList(types));
        provider.createFor(table, Arrays.asList(values));
    }

    @Test (expected = ColumnFormatException.class)
    public void testSerializeIncorrectTypes() throws IOException {
        Class<?>[] types = {Integer.class, Boolean.class};
        Object[] values = {3, 5.2f};
        Table table = provider.createTable("table", Arrays.asList(types));
        Class<?>[] anotherTypes = {Integer.class, Float.class};
        Table anotherTable = provider.createTable("anotherTable", Arrays.asList(anotherTypes));
        Storeable storeable = provider.createFor(anotherTable, Arrays.asList(values));
        provider.serialize(table, storeable);
    }

    @Test (expected = ParseException.class)
    public void testDeserializeWithParseExceptionIncorrectNumber() throws IOException, ParseException {
        Class<?>[] types = {Integer.class, Float.class, Double.class, Boolean.class, String.class, Byte.class};
        Table table = provider.createTable("table", Arrays.asList(types));
        String xml = "<row><col>3</col><col>5.2</col><col>5.4</col><col>true</col><col>hello</col></row>";
        provider.deserialize(table, xml);
    }

    @Test (expected = ParseException.class)
    public void testDeserializeWithParseExceptionIncorrectXml() throws IOException, ParseException {
        Class<?>[] types = {Integer.class, Float.class, Double.class, Boolean.class, String.class, Byte.class};
        Table table = provider.createTable("table", Arrays.asList(types));
        String xml = "<vasya></vasya>";
        provider.deserialize(table, xml);
    }

    @Test
    public void testCreateFor() throws IOException {
        Class<?>[] types = {Integer.class, Float.class, Double.class, Boolean.class, String.class, Byte.class};
        Table table = provider.createTable("table", Arrays.asList(types));
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(2, 5.2);
        assertEquals(storeable.getDoubleAt(2), (Double) 5.2);
    }

    @Test
    public void testListOfTables() throws IOException {
        Class<?>[] types = {Integer.class};
        provider.createTable("table", Arrays.asList(types)).commit();
        provider.createTable("таблица", Arrays.asList(types));
        provider.createTable("табличка", Arrays.asList(types));
        assertEquals(provider.getTableNames().size(), 3);
        provider.removeTable("table");
        assertEquals(provider.getTableNames().size(), 2);
        assertTrue(provider.getTableNames().containsAll(
                new LinkedList<String>(Arrays.asList("таблица", "табличка"))));
    }
}
