package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.AdvancedTableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.MyRemoteTableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ServerLogic;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MyRemoteTableProviderTest {
    RemoteTableProviderFactory factory;
    private ServerLogic server;
    private RemoteTableProvider provider;
    String address = "localhost";
    int portNumber = 3000;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() throws Exception {
        factory = new MyRemoteTableProviderFactory();
        TableProviderFactory serverFactory = new AdvancedTableProviderFactory();
        TableProvider serverProvider = serverFactory.create(tmpFolder.newFolder().getAbsolutePath());
        server = new ServerLogic(serverProvider);
        server.start(portNumber);
        provider = factory.connect(address, portNumber);
    }

    @After
    public void after() throws Exception {
        server.stop();
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
        TableProvider sameProvider = factory.connect(address, portNumber);
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
        TableProvider sameProvider = factory.connect(address, portNumber);
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
    public void testShowTables() throws Exception {
        Class<?>[] types = {Integer.class, Float.class, Double.class, Boolean.class, String.class, Byte.class};
        provider.createTable("table", Arrays.asList(types));
        provider.createTable("table1", Arrays.asList(types));
        provider.createTable("table2", Arrays.asList(types));
        List<String> result = provider.getTableNames();
        assertEquals(result.size(), 3);
        assertTrue(result.containsAll(new LinkedList<>(Arrays.asList("table1", "table", "table2"))));
    }
}
