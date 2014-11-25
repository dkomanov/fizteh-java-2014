package ru.fizteh.fivt.students.alexpodkin.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.alexpodkin.Storeable.StoreableTableProviderFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 25.11.14.
 */
public class StoreableTableProviderTest {
    private static TableProviderFactory tableProviderFactory;
    private static TableProvider tableProvider;
    private static List<Class<?>> signature;
    private static List<Object> valuesList;
    private static String nullXml;
    private static String correctXml;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        tableProviderFactory = new StoreableTableProviderFactory();

        signature = new ArrayList<>();
        signature.add(Integer.class);
        signature.add(Long.class);
        signature.add(Byte.class);
        signature.add(Float.class);
        signature.add(Double.class);
        signature.add(Boolean.class);
        signature.add(String.class);
        valuesList = new ArrayList<>();
        valuesList.add(1);
        valuesList.add((long) 2);
        valuesList.add((byte) 3);
        valuesList.add((float) 4);
        valuesList.add((double) 5);
        valuesList.add(true);
        valuesList.add("six");
        nullXml = "<row><null/><null/><null/><null/><null/><null/><null/></row>";
        correctXml = "<row><col>1</col><col>2</col><col>3</col><col>4.0</col>"
                + "<col>5.0</col><col>true</col><col>six</col></row>";
    }

    @Before
    public void before() throws IOException {
        tableProvider = tableProviderFactory.create(temporaryFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull() throws IOException {
        tableProvider.createTable(null, signature);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullSignature() throws IOException {
        tableProvider.createTable("table", null);
    }

    @Test
    public void createNotExist() throws IOException {
        Assert.assertNotNull(tableProvider.createTable("table", signature));
    }

    @Test
    public void createExist() throws IOException {
        tableProvider.createTable("table", signature);
        Assert.assertNull(tableProvider.createTable("table", signature));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNull() throws IOException {
        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void removeNotExist() throws IOException {
        tableProvider.removeTable("table");
    }

    @Test
    public void removeExist() throws IOException {
        tableProvider.createTable("table", signature);
        tableProvider.removeTable("table");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        tableProvider.getTable(null);
    }

    @Test
    public void getNotExist() {
        Assert.assertNull(tableProvider.getTable("table"));
    }

    @Test
    public void getExist() throws IOException {
        tableProvider.createTable("table", signature);
        Assert.assertNotNull(tableProvider.getTable("table"));
    }

    @Test
    public void get() throws IOException {
        tableProvider.createTable("table", signature);
        tableProvider.createTable("table2", signature);
        tableProvider.removeTable("table");
        Assert.assertNull(tableProvider.getTable("table"));
        Assert.assertNotNull(tableProvider.getTable("table2"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyCreateForOutOfBounds() throws IOException {
        Table table = tableProvider.createTable("table", signature);
        Storeable storeable = tableProvider.createFor(table);
        storeable.getColumnAt(7);
    }

    @Test
    public void createFor() throws IOException {
        Table table = tableProvider.createTable("table", signature);
        Storeable storeable = tableProvider.createFor(table, valuesList);
        Assert.assertEquals(valuesList.get(0), storeable.getIntAt(0));
        Assert.assertEquals(valuesList.get(1), storeable.getLongAt(1));
        Assert.assertEquals(valuesList.get(2), storeable.getByteAt(2));
        Assert.assertEquals(valuesList.get(3), storeable.getFloatAt(3));
        Assert.assertEquals(valuesList.get(4), storeable.getDoubleAt(4));
        Assert.assertEquals(valuesList.get(5), storeable.getBooleanAt(5));
        Assert.assertEquals(valuesList.get(6), storeable.getStringAt(6));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void createForOutOfBounds() throws IOException {
        Table table = tableProvider.createTable("table", signature);
        Storeable storeable = tableProvider.createFor(table, valuesList);
        storeable.getColumnAt(7);
    }

    @Test
    public void serialize() throws IOException {
        Table table = tableProvider.createTable("table", signature);
        Storeable storeable = tableProvider.createFor(table, valuesList);
        String serialized = tableProvider.serialize(table, storeable);
        Assert.assertEquals(serialized, correctXml);
    }

    @Test
    public void deserialize() throws IOException, ParseException {
        Table table = tableProvider.createTable("table", signature);
        Storeable storeable = tableProvider.deserialize(table, correctXml);
        for (int columnNumber = 0; columnNumber < valuesList.size(); columnNumber++) {
            Assert.assertEquals(valuesList.get(columnNumber), storeable.getColumnAt(columnNumber));
        }
    }

    @Test
    public void getTableNamesTest() throws IOException{
        tableProvider.createTable("table", signature);
        tableProvider.createTable("table2", signature);
        List<String> result = tableProvider.getTableNames();
        Assert.assertTrue((result.get(0).equals("table") && result.get(1).equals("table2"))
                || (result.get(0).equals("table2") && result.get(1).equals("table")));
    }
}
