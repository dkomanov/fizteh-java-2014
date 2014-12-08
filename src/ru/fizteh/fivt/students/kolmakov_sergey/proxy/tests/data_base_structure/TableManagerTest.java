package ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_structure;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_exceptions.DatabaseCorruptedException;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.util.CastMaker;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.util.DirectoryKiller;

public class TableManagerTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DataBaseTestDirectory");
    private final String directoryName = "test";
    private final Path directoryPath = testDir.resolve(directoryName);
    private final String tableName = "table";
    private final String signatureFileName = "signature.tsv";
    private final String stringValue = "string value";
    private List<Class<?>> columnTypes = new ArrayList<>();
    private Table table;
    private TableProvider provider = new TableManager(testDir.toString());
    List<String> values = new ArrayList<>();
    private Storeable testStoreableValue;

    private List<Class<?>> listWhichHoldsAllTypes = new ArrayList<>();
    List<Object> listWhichHoldsAllValues = new ArrayList<>();
    private Storeable megaStoreable;

    @Before
    public void setUp() throws DatabaseCorruptedException {

        testDir.toFile().mkdir();
        listWhichHoldsAllValues.add(1); // int = 0
        listWhichHoldsAllValues.add((long) 2); // long = 1
        listWhichHoldsAllValues.add((byte) 3); // byte = 2
        listWhichHoldsAllValues.add(7.62f); // float = 3
        listWhichHoldsAllValues.add(3.14); // double = 4
        listWhichHoldsAllValues.add(false); // boolean = 5
        listWhichHoldsAllValues.add("string"); // string = 6

        listWhichHoldsAllTypes.add(Integer.class);
        listWhichHoldsAllTypes.add(Long.class);
        listWhichHoldsAllTypes.add(Byte.class);
        listWhichHoldsAllTypes.add(Float.class);
        listWhichHoldsAllTypes.add(Double.class);
        listWhichHoldsAllTypes.add(Boolean.class);
        listWhichHoldsAllTypes.add(String.class);

        Table tableWhichHoldsAllTypes = new TableClass(testDir, "table name", provider, listWhichHoldsAllTypes);
        megaStoreable = new StoreableClass(tableWhichHoldsAllTypes, listWhichHoldsAllValues);


        values.add("\"" + stringValue + "\"");
        columnTypes.add(String.class);
        table = new TableClass(testDir, tableName, provider, columnTypes);
        testStoreableValue = new StoreableClass(table, values);
    }

    @Test
    public void testTableManagerConstructorUnexistentDirectory() {
        new TableManager(directoryPath.toString());
    }

    @Test
    public void testDeserializeMethod() throws ParseException {
        assertEquals(provider.deserialize(table, "[\"" + stringValue + "\"]"), testStoreableValue);
    }

    @Test (expected = ParseException.class)
    public void testDeserializeMethodWithoutBracket1() throws ParseException {
        assertEquals(provider.deserialize(table, stringValue + "\"]"), testStoreableValue);
    }

    @Test (expected = ParseException.class)
    public void testDeserializeMethodWithoutBracket2() throws ParseException {
        assertEquals(provider.deserialize(table, "[\"" + stringValue), testStoreableValue);
    }

    @Test
    public void testCreateForMethod() throws ParseException {
        assertEquals(provider.createFor(table, values), new StoreableClass(table, values));
    }

    @Test
    public void testTableManagerConstructorExistentDirectory() {
        directoryPath.toFile().mkdir();
        new TableManager(directoryPath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerConstructorThrowsExceptionWhenArgumentIsFile() throws IOException {
        directoryPath.toFile().createNewFile();
        new TableManager(directoryPath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionInPathGetMethod() {
        new TableManager("\0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionWhenThereAreBadFilesInRoot() throws IOException {
        directoryPath.toFile().mkdir();
        directoryPath.resolve("fileName").toFile().createNewFile();
        new TableManager(directoryPath.toString());
    }

    @Test(expected = RuntimeException.class)
    public void testTableManagerTrowsExceptionForTableWithoutSignatureFile()
            throws IOException {
        directoryPath.toFile().mkdir();
        directoryPath.resolve(tableName).toFile().mkdir();
        TableProvider test = new TableManager(directoryPath.toString());
        assertNotEquals(null, test.getTable(tableName));
    }

    @Test
    public void testTableManagerCreatedForDirectoryWithExistientTable()
            throws IOException {
        directoryPath.toFile().mkdir();
        directoryPath.resolve(tableName).toFile().mkdir();
        Path currentSignaturePath = directoryPath.resolve(tableName).resolve(signatureFileName);
        File currentFile = currentSignaturePath.toFile();
        currentFile.createNewFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentSignaturePath.toString()))) {
            for (Class<?> currentClass: columnTypes) {
                writer.println(CastMaker.classToString(currentClass));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        TableProvider test = new TableManager(directoryPath.toString());
        assertNotEquals(null, test.getTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionWhenArgumentIsNull() throws IOException {
        TableProvider test = new TableManager(directoryPath.toString());
        test.createTable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForWrongTableName1() throws IOException {
        TableProvider test = new TableManager(directoryPath.toString());
        test.createTable("badName.", columnTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForWrongTableName2() throws IOException {
        TableProvider test = new TableManager(directoryPath.toString());
        test.createTable("bad/name", columnTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForWrongTableName3() throws IOException {
        TableProvider test = new TableManager(directoryPath.toString());
        test.createTable("bad\\name", columnTypes);
    }

    @Test
    public void testCreateTableMakesNewTableProperly() throws IOException {
        TableProvider test = new TableManager(directoryPath.toString());
        assertNotEquals(null, test.createTable(tableName, columnTypes));
        assertTrue(directoryPath.resolve(tableName).toFile().exists());
    }

    @Test
    public void testCreateTableMakesExistentTableProperly() throws IOException {
        directoryPath.resolve(tableName).toFile().mkdirs();
        Path currentSignaturePath = directoryPath.resolve(tableName).resolve(signatureFileName);
        File currentFile = currentSignaturePath.toFile();
        currentFile.createNewFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentSignaturePath.toString()))) {
            for (Class<?> currentClass: columnTypes) {
                writer.println(CastMaker.classToString(currentClass));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        TableProvider test = new TableManager(directoryPath.toString());
        assertEquals(null, test.createTable(tableName, columnTypes));
        assertTrue(directoryPath.resolve(tableName).toFile().exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionWhenArgumentIsNull() {
        TableProvider test = new TableManager(directoryPath.toString());
        test.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionWhenNameIsBad() {
        TableProvider test = new TableManager(directoryPath.toString());
        test.getTable("ab/cd");
    }

    @Test
    public void testGetTableCalledForNonexistentTable() {
        TableProvider test = new TableManager(directoryPath.toString());
        assertEquals(null, test.getTable(tableName));
    }

    @Test
    public void testGetTableCalledForExistentTable() throws IOException {
        TableProvider test = new TableManager(directoryPath.toString());
        assertNotEquals(null, test.createTable(tableName, columnTypes));
        assertNotEquals(null, test.getTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionWhenArgumentIsNull() throws IOException {
        TableProvider test = new TableManager(directoryPath.toString());
        test.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionWhenNameIsBad() throws IOException {
        TableProvider test = new TableManager(directoryPath.toString());
        //Wrong table name contains '.', '/' or '\'.
        test.removeTable("ab\\cd");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableThrowsExceptionWhenTableNotExists() throws IOException {
        TableProvider test = new TableManager(directoryPath.toString());
        test.removeTable(tableName);
    }

    @Test
    public void testRemoveTableCalledForTableWithData() throws IOException {
        TableProvider test = new TableManager(directoryPath.toString());
        assertNotEquals(null, test.createTable(tableName, columnTypes));
        Table testTable = test.getTable(tableName);
        assertNotEquals(null, testTable);
        assertEquals(null, testTable.put("key", testStoreableValue));
        assertEquals(null, testTable.put("key2", testStoreableValue));
        testTable.commit();
        test.removeTable(tableName);
    }

    @Test
    public void testExcludeFolderNumber() {
        assertEquals(2, TableManager.excludeFolderNumber("2.dir"));
        assertEquals(42, TableManager.excludeFolderNumber("42.dir"));
        assertEquals(442, TableManager.excludeFolderNumber("442.dir"));
    }
    @Test
    public void testExcludeFileNumber() {
        assertEquals(2, TableManager.excludeDataFileNumber("2.dat"));
        assertEquals(42, TableManager.excludeDataFileNumber("42.dat"));
        assertEquals(442, TableManager.excludeDataFileNumber("442.dat"));
    }

    @Test
    public void testGetNamesMethod() throws IOException {
        directoryPath.toFile().mkdir();
        directoryPath.resolve(tableName).toFile().mkdir();
        Path currentSignaturePath = directoryPath.resolve(tableName).resolve(signatureFileName);
        File currentFile = currentSignaturePath.toFile();
        currentFile.createNewFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentSignaturePath.toString()))) {
            for (Class<?> currentClass: columnTypes) {
                writer.println(CastMaker.classToString(currentClass));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        TableManager test = new TableManager(directoryPath.toString());
        test.createTable(tableName, columnTypes);
        List<String> expected = new ArrayList<>();
        expected.add(tableName);
        assertEquals(expected, test.getTableNames());
    }

    @Test
    public void testExcludeFromStoreable() {
        try {
            table = new TableClass(testDir, tableName, provider, listWhichHoldsAllTypes);
            assertEquals(provider.serialize(table, megaStoreable), "[1, 2, 3, 7.62, 3.14, false, string]");
        } catch (DatabaseCorruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCloseMethod() throws Exception {
        TableManager test = new TableManager(directoryPath.toString());
        test.close();
    }

    @Test(expected = IllegalStateException.class)
    public void testDoubleCloseMethodThrowsException() throws Exception {
        TableManager test = new TableManager(directoryPath.toString());
        test.close();
        test.close();
    }

    @After
    public void tearDown() {
        DirectoryKiller.delete(testDir.toFile());
    }
}
