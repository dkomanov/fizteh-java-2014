package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.DBTableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.StringTableImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TableProviderTest extends TestBase {
    private static final List<Class<?>> DEFAULT_COLUMN_TYPES = Arrays.asList(String.class);

    private static TableProviderFactory factory;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private TableProvider provider;

    @BeforeClass
    public static void globalPrepare() {
        factory = new DBTableProviderFactory();
    }

    @Before
    public void prepareProvider() throws IOException {
        provider = factory.create(DB_ROOT.toString());
    }

    @After
    public void cleanupProvider() throws IOException {
        cleanDBRoot();
        provider = null;
    }

    private Table createTable(Class<?>... columnTypes) throws IOException {
        return provider.createTable("table", Arrays.asList(columnTypes));
    }

    private void checkSerialize(Table table, Storeable storeable) throws ParseException {
        String serialized = provider.serialize(table, storeable);
        Storeable deserialized = provider.deserialize(table, serialized);
        assertTrue(Objects.equals(storeable, deserialized));
    }

    @Test
    public void testSerializeDeserializeSync() throws IOException, ParseException {
        Table table = createTable(String.class, Double.class, Boolean.class);
        Storeable storeable = provider.createFor(table, Arrays.asList("a", 15.8, true));
        checkSerialize(table, storeable);
    }

    @Test
    public void testSerializeDeserializeSyncNulls() throws IOException, ParseException {
        Table table = createTable(String.class, Double.class, Boolean.class);
        Storeable storeable = provider.createFor(table, Arrays.asList(null, null, null));
        checkSerialize(table, storeable);
    }

    private void expectJSONRegexMatchFailure() {
        exception.expect(ParseException.class);
        exception.expectMessage(
                wrongTypeMatcherAndAllOf(
                        containsString(
                                "Does not match JSON simple list regular expression")));
    }

    @Test
    public void testDeserialize() throws IOException, ParseException {
        Table table = createTable(Long.class, Byte.class, String.class);
        expectJSONRegexMatchFailure();

        provider.deserialize(table, "3, 1, null");
    }

    @Test
    public void testDeserialize1() throws IOException, ParseException {
        Table table = createTable(Long.class, Byte.class, String.class);
        expectJSONRegexMatchFailure();

        provider.deserialize(table, "[,,]");
    }

    @Test
    public void testDeserialize2() throws IOException, ParseException {
        Table table = createTable(Long.class, Byte.class, String.class);
        expectJSONRegexMatchFailure();

        provider.deserialize(table, "[\"]");
    }

    @Test
    public void testDeserialize3() throws IOException, ParseException {
        Table table = createTable(Long.class, Byte.class, String.class);
        expectJSONRegexMatchFailure();

        provider.deserialize(table, "[\"/say\"]");
    }

    @Test
    public void testDeserializeTooManyElements() throws IOException, ParseException {
        Table table = createTable(Long.class, Byte.class, Integer.class);

        exception.expect(ParseException.class);
        exception.expectMessage(
                wrongTypeMatcherAndAllOf(
                        containsString(
                                "Too many elements in the list; expected: " + table.getColumnsCount())));

        provider.deserialize(table, "[ 1, 2, 3, 4, 5, \"6\" ]");
    }

    @Test
    public void testDeserializeTooFewElements() throws IOException, ParseException {
        Table table = createTable(String.class, Long.class, Boolean.class);

        exception.expect(ParseException.class);
        exception.expectMessage(
                wrongTypeMatcherAndAllOf(
                        containsString(
                                "Too few elements in the list; expected: " + table.getColumnsCount())));

        provider.deserialize(table, "[ \"lonely element\" ]");
    }

    @Test
    public void testSerialize() throws IOException {
        Table table = createTable(Boolean.class);
        Storeable storable = provider.createFor(table, Arrays.asList(true));
        String serialized = provider.serialize(table, storable);

        String regex = "(\\s*)\\[(\\s*)true(\\s*)\\](\\s*)";
        assertTrue(serialized.matches(regex));
    }

    @Test
    public void testSerialize1() throws IOException {
        Table table = createTable(Boolean.class, Double.class, String.class, String.class);
        Storeable storable = provider.createFor(table, Arrays.asList(false, -2.41, null, "a//b ///\" \" c"));
        String serialized = provider.serialize(table, storable);

        System.err.println(serialized);

        String regex =
                "(\\s*)\\[(\\s*)false,(\\s*)-2\\.41,null,(\\s*)\"a////b ///////\" /\" c\"(\\s*)\\](\\s*)";
        assertTrue(serialized.matches(regex));
    }

    @Test
    public void testGetTableNull() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Table name must not be null");
        provider.getTable(null);
    }

    private void expectTableNameIsNotCorrect() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Table name is not correct");
    }

    private void expectTableCorruptAndAllOf(String tableName, Matcher<String>... matchers) {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(allOf(containsString("Table " + tableName + " is corrupt"), allOf(matchers)));
    }

    @Test
    public void testObtainTableWithInvalidSignatureFile() throws IOException {
        String tableName = "table";
        Table table = provider.createTable(tableName, DEFAULT_COLUMN_TYPES);

        table.put("key", provider.createFor(table, Arrays.asList("value")));
        table.commit();

        try (PrintWriter writer = new PrintWriter(
                DB_ROOT.resolve(tableName).resolve("signature.tsv").toString())) {
            writer.print("Integer Integer");
        }

        prepareProvider();

        expectTableCorruptAndAllOf(
                tableName,
                containsString("wrong type (Invalid type description file for table"));

        provider.getTable(tableName);
    }

    @Test
    public void testObtainTableWithExtraFile() throws IOException {
        String tableName = "table";

        Files.createDirectories(DB_ROOT.resolve(tableName).resolve("1.dir"));
        Files.createFile(DB_ROOT.resolve(tableName).resolve("1.dir").resolve("file.txt"));

        prepareProvider();
        expectTableCorruptAndAllOf(tableName, containsString("Invalid database element format"));

        provider.getTable(tableName);
    }

    @Test
    public void testObtainTableWithMissingSignatureFile() throws IOException {
        String tableName = "table";

        Files.createDirectory(DB_ROOT.resolve(tableName));

        prepareProvider();

        expectTableCorruptAndAllOf(tableName, containsString("Failed to open types description file"));

        provider.getTable(tableName);
    }

    @Test
    public void testObtainTableWithBadIDOfPartFile() throws IOException {
        String tableName = "table";

        Files.createDirectories(DB_ROOT.resolve(tableName).resolve("1.dir"));
        Files.createFile(DB_ROOT.resolve(tableName).resolve("1.dir").resolve("10000.dat"));

        prepareProvider();
        expectTableCorruptAndAllOf(tableName, containsString("Invalid database element id"));

        provider.getTable(tableName);
    }

    @Test
    public void testGetTableBadName() {
        expectTableNameIsNotCorrect();
        provider.getTable(Paths.get("..", "t").toString());
    }

    @Test
    public void testGetTableBadName1() {
        expectTableNameIsNotCorrect();
        provider.getTable(Paths.get("..", DB_ROOT.getFileName().toString(), "t").toString());
    }

    @Test
    public void testGetTableBadName2() {
        expectTableNameIsNotCorrect();
        provider.getTable(Paths.get("subdir", "t1").toString());
    }

    @Test
    public void testGetTableBadName3() {
        expectTableNameIsNotCorrect();
        provider.getTable("..");
    }

    @Test
    public void testGetTableBadName4() {
        expectTableNameIsNotCorrect();
        provider.getTable(".");
    }

    @Test
    public void testGetTableBadName5() {
        expectTableNameIsNotCorrect();
        provider.getTable(Paths.get("..", "..").toString());
    }

    @Test
    public void testGetTableBadName6() {
        expectTableNameIsNotCorrect();
        provider.getTable(Paths.get("..", "..", "t1").toString());
    }

    @Test
    public void testCreateTableGoodName() throws IOException {
        provider.createTable("fizteh.students", DEFAULT_COLUMN_TYPES);
    }

    @Test
    public void testGetTableNotExistent() {
        Table table = provider.getTable("not existent");
        assertNull("Not existent table should be null", table);
    }

    @Test
    public void testCreateTableExistent() throws IOException {
        provider.createTable("table", DEFAULT_COLUMN_TYPES);
        Table table = provider.createTable("table", DEFAULT_COLUMN_TYPES);
        assertNull("Cannot create duplicate table, should be null", table);
    }

    @Test
    public void testRemoveTableExistent() throws IOException {
        String name = "table";

        Table table = provider.createTable(name, DEFAULT_COLUMN_TYPES);
        provider.removeTable(name);

        exception.expect(IllegalStateException.class);
        exception.expectMessage(name + " is invalidated");

        // Even calling to this simple method can cause IllegalStateException if the table has been removed.
        table.getName();
    }

    @Test
    public void testPutOneStoreableToAnotherTable() throws IOException {
        String nameA = "tableA";
        String nameB = "tableB";

        Table tableA = provider.createTable(nameA, Arrays.asList(Integer.class, String.class));
        Table tableB = provider.createTable(nameB, DEFAULT_COLUMN_TYPES);

        Storeable storeableA = provider.createFor(tableA, Arrays.asList(5, "Just a string"));

        exception.expect(ColumnFormatException.class);
        exception.expectMessage(wrongTypeMatcherAndAllOf(containsString("Got more columns then expected")));

        tableB.put("key", storeableA);
    }

    @Test
    public void testPutOneStoreableToAnotherTable1() throws IOException {
        String nameA = "tableA";
        String nameB = "tableB";

        Table tableA = provider.createTable(nameA, Arrays.asList(Integer.class, String.class));
        Table tableB = provider.createTable(nameB, DEFAULT_COLUMN_TYPES);

        Storeable storeableB = provider.createFor(tableB, Arrays.asList("Just a string"));

        exception.expect(ColumnFormatException.class);
        exception.expectMessage(wrongTypeMatcherAndAllOf(containsString("Got less columns then expected")));

        tableA.put("key", storeableB);
    }

    @Test
    public void testPutOneStoreableToAnotherTable2() throws IOException {
        String nameA = "tableA";
        String nameB = "tableB";

        Table tableA = provider.createTable(nameA, Arrays.asList(Integer.class, String.class));
        Table tableB = provider.createTable(nameB, Arrays.asList(Long.class, String.class));

        Storeable storeableB = provider.createFor(tableB, Arrays.asList(12L, "Just a string"));

        exception.expect(ColumnFormatException.class);
        exception.expectMessage(
                wrongTypeMatcherAndAllOf(
                        containsString(
                                "Column #0 expected to have type " + INT_TYPE + ", but actual type is "
                                + LONG_TYPE)));

        tableA.put("key", storeableB);
    }

    @Test
    public void testCreateTableWithNullTypesList() throws IOException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Column types list must not be null");

        provider.createTable("table", null);
    }

    @Test
    public void testCreateTableWithEmptyTypesList() throws IOException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Column types list must not be empty");

        provider.createTable("table", Collections.emptyList());
    }

    @Test
    public void testRemoveTableNotExistent() throws IOException {
        exception.expect(IllegalStateException.class);
        exception.expectMessage(endsWith("not exists"));
        provider.removeTable("!not existent!");
    }

    @Test
    public void testFailCreateTable() throws IOException {
        String table = "table";

        Files.createFile(DB_ROOT.resolve(table));

        exception.expect(IOException.class);
        exception.expectMessage("Failed to create table directory: " + table);

        provider.createTable(table, DEFAULT_COLUMN_TYPES);
    }

    @Test
    public void testStoreKeysInImproperPlaces() throws Exception {
        String tmpTable = "tmp";

        StringTableImpl table = StringTableImpl.createTable(DB_ROOT.resolve(tmpTable));

        table.put("a", "b");
        table.put("c", "d");
        table.commit();

        List<Path> paths = new LinkedList<>();

        try (DirectoryStream<Path> partDirs = Files.newDirectoryStream(DB_ROOT.resolve(tmpTable))) {
            for (Path path : partDirs) {
                paths.add(path);
            }
        }

        // Suppose we have 0.dir, 1.dir, ..., 15.dir; Let's shift them left:
        // 1.dir becomes 0.dir, 0.dir becomes 15.dir and so on.
        Path tmpPath = DB_ROOT.resolve(tmpTable).resolve("tmp");

        Files.move(paths.get(0), tmpPath);
        Iterator<Path> pathIterator = paths.listIterator(1);

        Path prev = paths.get(0);

        while (pathIterator.hasNext()) {
            Path next = pathIterator.next();
            Files.move(next, prev);
            prev = next;
        }

        Files.move(tmpPath, paths.get(paths.size() - 1));

        prepareProvider();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(
                "Table " + tmpTable + " is corrupt: Some keys are stored in improper places");
        provider.getTable(tmpTable);
    }
}
