package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.DataBaseIOException;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.Helper;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManager;

public class TableManagerTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DbTestDir");
    private final String dirName = "test";
    private final Path dirPath = testDir.resolve(dirName);
    private final String testTableName = "testTable";
    private final String structureString = "int long byte float double boolean String";
    private static final List<Class<?>> STRUCTURE;
    static {
        List<Class<?>> unitializerList = new ArrayList<>();
        unitializerList.add(Integer.class);
        unitializerList.add(Long.class);
        unitializerList.add(Byte.class);
        unitializerList.add(Float.class);
        unitializerList.add(Double.class);
        unitializerList.add(Boolean.class);
        unitializerList.add(String.class);
        STRUCTURE = Collections.unmodifiableList(unitializerList);
    }
    private static final List<Class<?>> MIXED_STRUCTURE;
    static {
        List<Class<?>> unitializerList = new ArrayList<>();
        unitializerList.add(Integer.class);
        unitializerList.add(Float.class);
        unitializerList.add(Long.class);
        unitializerList.add(Boolean.class);
        unitializerList.add(String.class);
        unitializerList.add(Byte.class);
        unitializerList.add(Double.class);
        MIXED_STRUCTURE = Collections.unmodifiableList(unitializerList);
    }
    private static final List<Object> TEST_VALUES;
    static {
        List<Object> unitializerList = new ArrayList<>();
        unitializerList.add((int) Integer.MAX_VALUE);
        unitializerList.add((long) Long.MAX_VALUE);
        unitializerList.add((byte) Byte.MAX_VALUE);
        unitializerList.add((float) Float.MAX_VALUE);
        unitializerList.add((double) Double.MAX_VALUE);
        unitializerList.add(Boolean.TRUE);
        unitializerList.add("test_value");
        TEST_VALUES = Collections.unmodifiableList(unitializerList);
    }
    private static final String SERIALIZED_VALUES = "[" + Integer.MAX_VALUE
            + ", " + Long.MAX_VALUE + ", " + Byte.MAX_VALUE
            + ", " + Float.MAX_VALUE + ", " + Double.MAX_VALUE
            + ", " + Boolean.TRUE + ", " + "\"test_value\"" + "]";
    private static final String SERIALIZED_STRING_WITH_INCORRECT_BOOL_TOKEN
            = "[0, 0, 0, 0.0, 0.0, notBoolean, \"string\"]";
    private static final String SERIALIZED_STRING_WITH_INCORRECT_NUMBER_TOKEN
            = "[0, 0, 0.5, 0.0, 0.0, true, \"string\"]";
    private static final String SERIALIZED_STRING_WITH_INCORRECT_STRING_TOKEN
            = "[0, 0, 0, 0.0, 0.0, true, unquoted]";
    private static final String SERIALIZED_NULL_VALUES
        = "[null, null, null, null, null, null, null]";
    
    @Before
    public void setUp() {
        testDir.toFile().mkdir();
    }

    @Test
    public void testTableManagerCreatedForNonexistentDirectory() throws DataBaseIOException {
        new TableManager(dirPath.toString());
    }
    
    @Test
    public void testTableManagerCreatedForExistentDirectory() throws DataBaseIOException {
        dirPath.toFile().mkdir();
        new TableManager(dirPath.toString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionCreatedNotForDirectory() throws IOException {
        dirPath.toFile().createNewFile();
        new TableManager(dirPath.toString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionCreatedForInvalidPath() throws DataBaseIOException {
        new TableManager("\0");
    }
    
    @Test(expected = DataBaseIOException.class)
    public void testTableManagerThrowsExceptionCreatedForDirectoryWithNondirectoryFile()
            throws IOException {
        dirPath.toFile().mkdir();
        dirPath.resolve("fileName").toFile().createNewFile();
        new TableManager(dirPath.toString());
    }
    
    @Test
    public void testTableManagerCreatedForDirectoryContainedDirectory()
            throws IOException {
        dirPath.toFile().mkdir();
        Path tablePath = dirPath.resolve(testTableName);
        tablePath.toFile().mkdir();
        Path signatureFile = tablePath.resolve(Helper.SIGNATURE_FILE_NAME);
        try (PrintWriter printer = new PrintWriter(signatureFile.toString())) {
            printer.print(structureString);
        }
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.getTable(testTableName));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForNullTableName() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        test.createTable(null, STRUCTURE);
    }
    
    @Test(expected = DataBaseIOException.class)
    public void testCreateTableThrowsExceptionCalledForNullTableStructureList() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        test.createTable(testTableName, null);
    }
    
    @Test(expected = DataBaseIOException.class)
    public void testCreateTableThrowsExceptionCalledForEmptyTableStructureList() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        test.createTable(testTableName, new ArrayList<Class<?>>());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForWrongTableName() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        test.createTable("..", STRUCTURE);
    }
    
    @Test
    public void testCreateTableCalledForNewTable() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        assertTrue(dirPath.resolve(testTableName).toFile().exists());
    }
    
    @Test
    public void testCreateTableCalledForExistentOnDiskTable() throws IOException {
        dirPath.toFile().mkdir();
        Path tablePath = dirPath.resolve(testTableName);
        tablePath.toFile().mkdir();
        Path signatureFile = tablePath.resolve(Helper.SIGNATURE_FILE_NAME);
        try (PrintWriter printer = new PrintWriter(signatureFile.toString())) {
            printer.print(structureString);
        }
        TableProvider test = new TableManager(dirPath.toString());
        assertEquals(null, test.createTable(testTableName, STRUCTURE));
        assertTrue(dirPath.resolve(testTableName).toFile().exists());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionCalledForNullTableName() throws Exception {
        TableProvider test = new TableManager(dirPath.toString());
        test.getTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionCalledForWrongTableName() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        test.getTable("ab/cd");
    }
    
    @Test
    public void testGetTableCalledForNonexistentTable() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        assertEquals(null, test.getTable(testTableName));
    }
    
    @Test
    public void testGetTableCalledForExistentTable() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        assertNotEquals(null, test.getTable(testTableName));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionCalledForNullTableName() throws Exception {
        TableProvider test = new TableManager(dirPath.toString());
        test.removeTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionCalledForWrongTableName() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        test.removeTable("ab\\cd");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveTableThrowsExceptionCalledForNonexistentTable() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        test.removeTable(testTableName);
    }
    
    @Test
    public void testRemoveTableCalledForExistentFullTable() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        assertNotEquals(null, testTable);    
        assertEquals(null, testTable.put("key", test.createFor(testTable, TEST_VALUES)));
        assertEquals(null, testTable.put("key2", test.createFor(testTable, TEST_VALUES)));
        testTable.commit();
        test.removeTable(testTableName);
    }
    
    @Test
    public void testCreateForMethodCallForExistentTable() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        Storeable store = test.createFor(testTable);
        for (int i = 0; i < testTable.getColumnsCount(); i++) {
            store.setColumnAt(i, null);
        }
    }
    
    @Test
    public void testGetTableNames() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        assertNotEquals(null, test.createTable(testTableName + "2", STRUCTURE));
        String[] tableNames = {testTableName, testTableName + "2"};
        assertArrayEquals(tableNames, test.getTableNames().toArray());
    }
    
    @Test
    public void testSerializeStoreableContainedNullColumns() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        List<Object> nullsContainingList = new ArrayList<>();
        for (int i = 0; i < TEST_VALUES.size(); i++) {
            nullsContainingList.add(null);
        }
        
        Storeable store = test.createFor(testTable, nullsContainingList);
        String serialized = test.serialize(testTable, store);
        assertEquals(SERIALIZED_NULL_VALUES, serialized);
    }
    
    @Test(expected = ColumnFormatException.class)
    public void testSerializeThrowsExceptionForStoreableNotForThisTable() throws IOException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, MIXED_STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        Storeable store = test.createFor(testTable, TEST_VALUES);
        test.serialize(testTable, store);
    }
    
    @Test
    public void testDeserializeSerializedStoreable() throws IOException, ParseException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        Storeable store = test.createFor(testTable, TEST_VALUES);
        String serialized = test.serialize(testTable, store);
        assertEquals(SERIALIZED_VALUES, serialized);
        Storeable newStore = test.deserialize(testTable, serialized);
        for (int i = 0; i < testTable.getColumnsCount(); i++) {
            assertEquals(store.getColumnAt(i), newStore.getColumnAt(i));
        }
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForInvalidJSONStringWithoutOpenBracket()
            throws IOException, ParseException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        test.deserialize(testTable, SERIALIZED_VALUES.substring(1));
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForInvalidJSONStringWithoutCloseBracket()
            throws IOException, ParseException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        test.deserialize(testTable,
                SERIALIZED_VALUES.substring(0, SERIALIZED_VALUES.length() - 1));
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForInvalidStringWithLessTokensThanRequired()
            throws IOException, ParseException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        //Required number of tokens is equal to size of STRUCTURE (7).
        test.deserialize(testTable, "[\"token 1\", \"token 2\"]");
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForStringWithInvalidBooleanToken()
            throws IOException, ParseException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        test.deserialize(testTable, SERIALIZED_STRING_WITH_INCORRECT_BOOL_TOKEN);
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForStringWithInvalidNumberToken()
            throws IOException, ParseException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        test.deserialize(testTable, SERIALIZED_STRING_WITH_INCORRECT_NUMBER_TOKEN);
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForStringWithInvalidStringToken()
            throws IOException, ParseException {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        test.deserialize(testTable, SERIALIZED_STRING_WITH_INCORRECT_STRING_TOKEN);
    }
    
    @Test
    public void testDeserializeStringContainedNullColumns() throws Exception {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName, STRUCTURE));
        Table testTable = test.getTable(testTableName);
        
        test.deserialize(testTable, SERIALIZED_NULL_VALUES);
    }
    
    @After
    public void tearDown() throws IOException {
        Helper.recoursiveDelete(testDir.toFile());
    }
}
