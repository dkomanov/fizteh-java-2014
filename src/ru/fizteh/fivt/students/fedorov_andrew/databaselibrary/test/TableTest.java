package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.StoreableTableImpl;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TableTest extends TestBase {
    private static final List<Class<?>> DEFAULT_COLUMN_TYPES = Arrays.asList(String.class);
    private static final String TABLE_NAME = "table";
    private static TableProviderFactory factory;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private Table table;
    private TableProvider provider;

    @BeforeClass
    public static void globalPrepare() {
        factory = TestUtils.obtainFactory();
    }

    @Before
    public void prepare() throws IOException {
        provider = factory.create(DB_ROOT.toString());
        table = provider.createTable(TABLE_NAME, DEFAULT_COLUMN_TYPES);
    }

    @After
    public void cleanup() throws IOException {
        provider = null;
        table = null;
        cleanDBRoot();
    }

    private String extractString(String value) {
        if (value == null) {
            return null;
        }
        int leftBracket = value.indexOf("\"");
        int rightBracket = value.lastIndexOf("\"");

        return value.substring(leftBracket + 1, rightBracket);
    }

    private String get(String key) {
        Storeable valueObj = table.get(key);
        return valueObj == null ? null : extractString(provider.serialize(table, valueObj));
    }

    private String put(String key, String value) throws ParseException {
        Storeable oldValueObj = table.put(key, provider.deserialize(table, "[\"" + value + "\"]"));
        String oldValue = oldValueObj == null ? null : provider.serialize(table, oldValueObj);
        return extractString(oldValue);
    }

    private String remove(String key) {
        Storeable oldValueObj = table.remove(key);
        String oldValue = oldValueObj == null ? null : provider.serialize(table, oldValueObj);
        return extractString(oldValue);
    }

    @Test
    public void testPutWithNullKey() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Key must not be null");

        table.put(null, provider.createFor(table));
    }

    @Test
    public void testPutWithNullValue() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Value must not be null");

        table.put("key", null);
    }

    @Test
    public void testOpenTableWithValuesNotMatchingTypes() throws IOException, ParseException {
        put("key", "value");
        table.commit();

        try (PrintWriter writer = new PrintWriter(
                DB_ROOT.resolve(TABLE_NAME).resolve("signature.tsv").toString())) {
            writer.print("int int int");
        }

        provider = factory.create(DB_ROOT.toString());

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(containsString("Value of improper format found"));

        table = provider.getTable(TABLE_NAME);
    }

    @Test
    public void testNumberOfUncommittedChanges() throws ParseException {
        put("key", "value");
        put("key2", "value2");

        assertEquals(2, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges1() throws ParseException {
        put("key", "value");
        put("key", "value2");

        assertEquals(1, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges2() throws ParseException {
        put("key", "value");
        remove("key");

        assertEquals(0, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges3() throws ParseException, IOException {
        put("key", "value");
        table.commit();
        remove("key");

        assertEquals(1, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges4() throws ParseException, IOException {
        put("key", "value");
        table.commit();
        remove("key");
        put("key", "value");

        assertEquals(0, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges5() throws ParseException, IOException {
        put("key", "value");
        table.commit();
        remove("key");
        put("key", "value2");

        assertEquals(1, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testPutOneStoreableToAnotherTable() throws IOException {
        Table table2 = provider.createTable(TABLE_NAME + "2", DEFAULT_COLUMN_TYPES);
        Storeable storeable = provider.createFor(table);

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Cannot put storeable assigned to one table to another table");

        table2.put("key", storeable);
    }

    @Test
    public void testGetColumnTypeAtBadIndex() {
        exception.expect(IndexOutOfBoundsException.class);

        table.getColumnType(table.getColumnsCount());
    }

    @Test
    public void testGetColumnTypeAtBadIndex1() {
        exception.expect(IndexOutOfBoundsException.class);

        table.getColumnType(-1);
    }

    @Test
    public void testParseUnsupportedColumnType() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(
                wrongTypeMatcherAndAllOf(containsString("no_such_type not supported")));

        StoreableTableImpl.parseColumnTypes("no_such_type");
    }

    @Test
    public void testGetExistent() throws ParseException {
        put("key", "value");
        String value = get("key");
        assertEquals("Existent key must return proper value", "value", value);
    }

    @Test
    public void testGetNotExistent() {
        String value = get("not_existent");
        assertNull("Not existent key must give null value", value);
    }

    @Test
    public void testGetNullKey() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Key must not be null");
        get(null);
    }

    @Test
    public void testPutNew() throws ParseException {
        int sizeBefore = table.size();
        String oldValue = put("new", "value");
        int sizeAfter = table.size();

        assertNull("When inserting new (key,value), old value must be null", oldValue);
        assertEquals("WHen inserting new key, size must increase by 1", sizeBefore + 1, sizeAfter);
    }

    @Test
    public void testPutExistent() throws ParseException {
        put("existent", "value1");
        int sizeBefore = table.size();
        String oldValue = put("existent", "value2");
        int sizeAfter = table.size();

        assertEquals("Wrong old value when putting existent key", "value1", oldValue);
        assertEquals("When putting existent key, size must not change", sizeBefore, sizeAfter);
    }

    @Test
    public void testRemoveNotExistent() {
        int sizeBefore = table.size();
        String oldValue = remove("not existent");
        int sizeAfter = table.size();

        assertNull("When removing not existent key, old value must be null", oldValue);
        assertEquals("When removing not existent key, size must not change", sizeBefore, sizeAfter);
    }

    @Test
    public void testRussianSymbols() throws ParseException {
        put("ключ", "значение");

        assertEquals("Russian symbols: put + get not synchronized", "значение", get("ключ"));
    }

    @Test
    public void testRemoveExistent() throws ParseException {
        put("key", "value1");
        int sizeBefore = table.size();
        String oldValue = remove("key");
        int sizeAfter = table.size();

        assertEquals("When removing existent key, old value must be returned", "value1", oldValue);
        assertEquals(
                "When removing existent key, size must decrease by one", sizeBefore - 1, sizeAfter);
    }
}
