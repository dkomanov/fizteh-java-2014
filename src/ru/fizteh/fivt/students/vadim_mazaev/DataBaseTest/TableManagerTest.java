package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
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
    @Before
    public void setUp() {
        TestHelper.TEST_DIR.toFile().mkdir();
    }

    @Test
    public void testTableManagerCreatedForNonexistentDirectory() throws DataBaseIOException {
        new TableManager(TestHelper.TEST_DIR.toString());
    }
    
    @Test
    public void testTableManagerCreatedForExistentDirectory() throws DataBaseIOException {
        TestHelper.TEST_DIR.toFile().mkdir();
        new TableManager(TestHelper.TEST_DIR.toString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionCreatedNotForDirectory() throws IOException {
        Path file = TestHelper.TEST_DIR.resolve("notDirectory");
        file.toFile().createNewFile();
        new TableManager(file.toString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionCreatedForInvalidPath()
            throws DataBaseIOException {
        new TableManager("\0");
    }
    
    @Test(expected = DataBaseIOException.class)
    public void testTableManagerThrowsExceptionCreatedForDirectoryWithNondirectoryFile()
            throws IOException {
        TestHelper.TEST_DIR.toFile().mkdir();
        TestHelper.TEST_DIR.resolve("fileName").toFile().createNewFile();
        new TableManager(TestHelper.TEST_DIR.toString());
    }
    
    @Test
    public void testTableManagerCreatedForDirectoryContainedDirectory()
            throws IOException {
        TestHelper.TEST_DIR.toFile().mkdir();
        Path tablePath = TestHelper.TEST_DIR.resolve(TestHelper.TEST_TABLE_NAME);
        tablePath.toFile().mkdir();
        Path signatureFile = tablePath.resolve(Helper.SIGNATURE_FILE_NAME);
        try (PrintWriter printer = new PrintWriter(signatureFile.toString())) {
            printer.print(TestHelper.STRUCTURE_STRING);
        }
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.getTable(TestHelper.TEST_TABLE_NAME));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForNullTableName() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        test.createTable(null, TestHelper.STRUCTURE);
    }
    
    @Test(expected = DataBaseIOException.class)
    public void testCreateTableThrowsExceptionCalledForNullTableStructureList()
            throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        test.createTable(TestHelper.TEST_TABLE_NAME, null);
    }
    
    @Test(expected = DataBaseIOException.class)
    public void testCreateTableThrowsExceptionCalledForEmptyTableStructureList()
            throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        test.createTable(TestHelper.TEST_TABLE_NAME, new ArrayList<Class<?>>());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForWrongTableName() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        test.createTable("..", TestHelper.STRUCTURE);
    }
    
    @Test
    public void testCreateTableCalledForNewTable() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        assertTrue(TestHelper.TEST_DIR.resolve(TestHelper.TEST_TABLE_NAME).toFile().exists());
    }
    
    @Test
    public void testCreateTableCalledForExistentOnDiskTable() throws IOException {
        TestHelper.TEST_DIR.toFile().mkdir();
        Path tablePath = TestHelper.TEST_DIR.resolve(TestHelper.TEST_TABLE_NAME);
        tablePath.toFile().mkdir();
        Path signatureFile = tablePath.resolve(Helper.SIGNATURE_FILE_NAME);
        try (PrintWriter printer = new PrintWriter(signatureFile.toString())) {
            printer.print(TestHelper.STRUCTURE_STRING);
        }
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        assertTrue(TestHelper.TEST_DIR.resolve(TestHelper.TEST_TABLE_NAME).toFile().exists());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionCalledForNullTableName() throws Exception {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        test.getTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionCalledForWrongTableName() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        test.getTable("ab/cd");
    }
    
    @Test
    public void testGetTableCalledForNonexistentTable() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNull(test.getTable(TestHelper.TEST_TABLE_NAME));
    }
    
    @Test
    public void testGetTableCalledForExistentTable() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        assertNotNull(test.getTable(TestHelper.TEST_TABLE_NAME));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionCalledForNullTableName() throws Exception {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        test.removeTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionCalledForWrongTableName() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        test.removeTable("ab\\cd");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveTableThrowsExceptionCalledForNonexistentTable() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        test.removeTable(TestHelper.TEST_TABLE_NAME);
    }
    
    @Test
    public void testRemoveTableCalledForExistentFullTable() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        assertNotNull(testTable);    
        assertNull(testTable.put("key", test.createFor(testTable,
                TestHelper.TEST_VALUES)));
        assertNull(testTable.put("key2", test.createFor(testTable,
                TestHelper.TEST_VALUES)));
        testTable.commit();
        test.removeTable(TestHelper.TEST_TABLE_NAME);
    }
    
    @Test
    public void testCreateForMethodCallForExistentTable() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        Storeable store = test.createFor(testTable);
        for (int i = 0; i < testTable.getColumnsCount(); i++) {
            store.setColumnAt(i, null);
        }
    }
    
    @Test
    public void testGetTableNames() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME + "2",
                TestHelper.STRUCTURE));
        String[] tableNames = {TestHelper.TEST_TABLE_NAME, TestHelper.TEST_TABLE_NAME + "2"};
        assertArrayEquals(tableNames, test.getTableNames().toArray());
    }
    
    @Test
    public void testSerializeStoreableContainedNullColumns() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        List<Object> nullsContainingList = new ArrayList<>();
        for (int i = 0; i < TestHelper.TEST_VALUES.size(); i++) {
            nullsContainingList.add(null);
        }
        
        Storeable store = test.createFor(testTable, nullsContainingList);
        String serialized = test.serialize(testTable, store);
        assertEquals(TestHelper.SERIALIZED_NULL_VALUES, serialized);
    }
    
    @Test(expected = ColumnFormatException.class)
    public void testSerializeThrowsExceptionForStoreableNotForThisTable() throws IOException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME,
                TestHelper.MIXED_STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        Storeable store = test.createFor(testTable, TestHelper.TEST_VALUES);
        test.serialize(testTable, store);
    }
    
    @Test
    public void testDeserializeSerializedStoreable() throws IOException, ParseException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        Storeable store = test.createFor(testTable, TestHelper.TEST_VALUES);
        String serialized = test.serialize(testTable, store);
        assertEquals(TestHelper.SERIALIZED_VALUES, serialized);
        Storeable newStore = test.deserialize(testTable, serialized);
        for (int i = 0; i < testTable.getColumnsCount(); i++) {
            assertEquals(store.getColumnAt(i), newStore.getColumnAt(i));
        }
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForInvalidJSONStringWithoutOpenBracket()
            throws IOException, ParseException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        test.deserialize(testTable, TestHelper.SERIALIZED_VALUES.substring(1));
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForInvalidJSONStringWithoutCloseBracket()
            throws IOException, ParseException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        test.deserialize(testTable,
                TestHelper.SERIALIZED_VALUES.substring(0,
                        TestHelper.SERIALIZED_VALUES.length() - 1));
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForInvalidStringWithLessTokensThanRequired()
            throws IOException, ParseException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        //Required number of tokens is equal to size of STRUCTURE (7).
        test.deserialize(testTable, "[\"token 1\", \"token 2\"]");
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForStringWithInvalidBooleanToken()
            throws IOException, ParseException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        test.deserialize(testTable,
                TestHelper.SERIALIZED_STRING_WITH_INCORRECT_BOOL_TOKEN);
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForStringWithInvalidNumberToken()
            throws IOException, ParseException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        test.deserialize(testTable,
                TestHelper.SERIALIZED_STRING_WITH_INCORRECT_NUMBER_TOKEN);
    }
    
    @Test(expected = ParseException.class)
    public void testDeserializeThrowsExceptionForStringWithInvalidStringToken()
            throws IOException, ParseException {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        test.deserialize(testTable,
                TestHelper.SERIALIZED_STRING_WITH_INCORRECT_STRING_TOKEN);
    }
    
    @Test
    public void testDeserializeStringContainedNullColumns() throws Exception {
        TableProvider test = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE));
        Table testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        
        test.deserialize(testTable, TestHelper.SERIALIZED_NULL_VALUES);
    }
    
    @After
    public void tearDown() throws IOException {
        Helper.recoursiveDelete(TestHelper.TEST_DIR.toFile());
    }
}
