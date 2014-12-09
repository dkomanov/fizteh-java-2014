package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.DBTable;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.Record;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.TableHolder;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.DatabaseFormatException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.*;

public class TableHolderTest {
    private Path testDirectory;
    private Path tableDirPath;
    private Path tableSubdirPath;
    private Path tableSignaturePath;

    private final String TEST_FILE = "filename.txt";
    private final String VALID_FILE_NAME = "1.dat";
    private final String TEST_TABLE_NAME1 = "MyTable1";
    private final String TEST_TABLE_NAME2 = "MyTable2";
    private final String WRONG_TABLE_NAME = ".";


    private static List<Class<?>> TYPES = Arrays.asList(Integer.class);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public final void setUp() throws IOException {
        testDirectory = tempFolder.newFolder().toPath();
        tableDirPath = testDirectory.resolve(TEST_TABLE_NAME1);
        tableSignaturePath = tableDirPath.resolve(Utility.TABLE_SIGNATURE);
        tableSubdirPath = tableDirPath.resolve("1.dir");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testHolderThrowsExceptionCreatedNotForDirectory()
            throws IOException {
        Path newFilePath = testDirectory.resolve(TEST_FILE);
        Files.createFile(newFilePath);
        new TableHolder(newFilePath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testTableHolderThrowsExceptionCreatedForInvalidPath() throws IOException {
        new TableHolder("\0");
    }

    /*
    * Tests on given database's format.
     */
    @Test(expected = DatabaseFormatException.class)
    public final void
    testTableHolderThrowsExceptionCreatedForDirectoryWithNonDirectories()
            throws IOException {
        Path newFilePath = testDirectory.resolve(TEST_FILE);
        Files.createFile(newFilePath);
        new TableHolder(testDirectory.toString());
    }

    @Test(expected = DatabaseFormatException.class)
    public final void
    testTableHolderThrowsExceptionCreatedForTableDirectoriesWithoutSignature()
            throws IOException {
        Files.createDirectory(tableDirPath);
        new TableHolder(testDirectory.toString());
    }

    @Test(expected = DatabaseFormatException.class)
    public final void
    testTableHolderThrowsExceptionCreatedForTableDirectoriesWithUnexpectedFiles()
            throws IOException {
        Files.createDirectory(tableDirPath);
        Path newFilePath = tableDirPath.resolve(TEST_FILE);
        Files.createFile(newFilePath);
        new TableHolder(testDirectory.toString());
    }

    @Test(expected = DatabaseFormatException.class)
    public final void
    testTableHolderThrowsExceptionCreatedForTableDirectoriesWithEmptySignature()
            throws IOException {
        Files.createDirectory(tableDirPath);
        Files.createFile(tableSignaturePath);
        new TableHolder(testDirectory.toString());
    }

    @Test(expected = DatabaseFormatException.class)
    public final void
    testTableHolderThrowsExceptionCreatedForTableDirectoriesWithInvalidSignature()
            throws IOException {
        Files.createDirectory(tableDirPath);
        Files.createFile(tableSignaturePath);
        try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
            String s1 = Utility.wrappersToPrimitive.get(Integer.class) + " ";
            String s2 = ArrayList.class.getSimpleName() + " ";
            writeSig.write(s1.getBytes(Utility.ENCODING));
            writeSig.write(s2.getBytes(Utility.ENCODING));
        }
        new TableHolder(testDirectory.toString());
    }

    @Test(expected = DatabaseFormatException.class)
    public final void
    testTableHolderThrowsExceptionCreatedForTableDirectoriesWithValidButEmptyDirectory()
            throws IOException {
        Files.createDirectory(tableDirPath);
        Path tableSignaturePath = tableDirPath.resolve(Utility.TABLE_SIGNATURE);
        Files.createFile(tableSignaturePath);
        try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
            String s1 = Utility.wrappersToPrimitive.get(Integer.class) + " ";
            writeSig.write(s1.getBytes(Utility.ENCODING));
        }
        Files.createDirectory(tableSubdirPath);
        new TableHolder(testDirectory.toString());
    }

    @Test(expected = DatabaseFormatException.class)
    public final void
    testTableHolderThrowsExceptionCreatedForTableDirectoriesWithValidDirectoryContainsInvalidNamedFiles()
            throws IOException {
        Files.createDirectory(tableDirPath);
        Path tableSignaturePath = tableDirPath.resolve(Utility.TABLE_SIGNATURE);
        Files.createFile(tableSignaturePath);
        try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
            String s1 = Utility.wrappersToPrimitive.get(Integer.class) + " ";
            writeSig.write(s1.getBytes(Utility.ENCODING));
        }
        Files.createDirectory(tableSubdirPath);
        Path newFilePath = tableSubdirPath.resolve(TEST_FILE);
        Files.createFile(newFilePath);
        new TableHolder(testDirectory.toString());
    }

    @Test(expected = DatabaseFormatException.class)
    public final void
    testTableHolderThrowsExceptionCreatedForTableDirectoriesWithDirectoryContainsValidNamedButEmptyFile()
            throws IOException {
        Files.createDirectory(tableDirPath);
        Path tableSignaturePath = tableDirPath.resolve(Utility.TABLE_SIGNATURE);
        Files.createFile(tableSignaturePath);
        try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
            String s1 = Utility.wrappersToPrimitive.get(Integer.class) + " ";
            writeSig.write(s1.getBytes(Utility.ENCODING));
        }
        Files.createDirectory(tableSubdirPath);
        Path newFilePath = tableSubdirPath.resolve(VALID_FILE_NAME);
        Files.createFile(newFilePath);
        new TableHolder(testDirectory.toString());
    }

    @Test(expected = DatabaseFormatException.class)
    public final void
    testTableHolderThrowsExceptionCreatedForTableDirectoriesWithDirectoryContainsInvalidContentFiles()
            throws IOException {
        Files.createDirectory(tableDirPath);
        Path tableSignaturePath = tableDirPath.resolve(Utility.TABLE_SIGNATURE);
        Files.createFile(tableSignaturePath);
        try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
            String s1 = Utility.wrappersToPrimitive.get(Integer.class) + " ";
            writeSig.write(s1.getBytes(Utility.ENCODING));
            Files.createDirectory(tableSubdirPath);
            Path newFilePath = tableSubdirPath.resolve(VALID_FILE_NAME);
            Files.createFile(newFilePath);
            try (RandomAccessFile writer = new RandomAccessFile(newFilePath.toString(), "rw")) {
                s1 = "some stuff";
                writer.write(s1.getBytes(Utility.ENCODING));
            }
        }
        new TableHolder(testDirectory.toString());
    }

    @Test(expected = DatabaseFormatException.class)
    public final void
    testTableHolderThrowsExceptionCreatedForTableDirectoriesWithDirectoryContainsRecordsNotCompatibleWithSignature()
            throws IOException {
        Files.createDirectory(tableDirPath);
        Path tableSignaturePath = tableDirPath.resolve(Utility.TABLE_SIGNATURE);
        Files.createFile(tableSignaturePath);
        try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
            String s1 = Utility.wrappersToPrimitive.get(Integer.class) + " ";
            writeSig.write(s1.getBytes(Utility.ENCODING));
        }
        String key = "key";
        String value = "[1.5]";
        int expectedNDirectory = Math.abs(key.getBytes(Utility.ENCODING)[0]
                % DBTable.DIR_AMOUNT);
        int expectedNFile = Math.abs((key.getBytes(Utility.ENCODING)[0] / DBTable.DIR_AMOUNT)
                % DBTable.FILES_AMOUNT);
        Path expectedDirectoryPath = tableDirPath.resolve(tableDirPath.toAbsolutePath().toString()
                + expectedNDirectory
                + DBTable.DIR_SUFFIX);
        Files.createDirectory(expectedDirectoryPath);
        Path expectedFilePath = expectedDirectoryPath.resolve(expectedNFile
                + DBTable.FILE_SUFFIX);
        Files.createFile(expectedFilePath);
        try (RandomAccessFile writer = new RandomAccessFile(expectedFilePath.toString(), "rw")) {
            Utility.writeUtil(key, writer);
            Utility.writeUtil(value, writer);
        }
        new TableHolder(testDirectory.toString());
    }

    /*
    * GetTable tests.
     */
    @Test
    public final void testGetTableReturnsNullIfTableNameDoesNotExist() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());

        assertNull(test.getTable(TEST_TABLE_NAME1));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetTableThrowsExceptionCalledForNullTableName() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetTableThrowsExceptionCalledForWrongTableName() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.getTable(WRONG_TABLE_NAME);
    }

    @Test
    public final void testGetTableReturnsSameObjectsCalledTwice() throws IOException {
        List<Class<?>> valuesTypes = new ArrayList<>();
        valuesTypes.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(TEST_TABLE_NAME1, valuesTypes);

        assertTrue(test.getTable(TEST_TABLE_NAME1) == test.getTable(TEST_TABLE_NAME1));
    }

    @Test
    public final void testGetTableReturnsNewValidExampleOfPreviouslyClosedTable() throws IOException {
        List<Class<?>> valuesTypes = new ArrayList<>();
        valuesTypes.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(TEST_TABLE_NAME1, valuesTypes);

        Table createdTable = test.getTable(TEST_TABLE_NAME1);
        ((DBTable)createdTable).close();
        assertNotNull(test.getTable(TEST_TABLE_NAME1));
    }

    /*
    * GetTableNames tests.
     */
    @Test
    public final void testGetTableNamesReturnNamesOfTwoCreatedTables() throws IOException {
        List<Class<?>> valuesTypes = new ArrayList<>();
        valuesTypes.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(TEST_TABLE_NAME1, valuesTypes);
        test.createTable(TEST_TABLE_NAME2, valuesTypes);

        Set<String> expectedTableNames = new HashSet<>();
        expectedTableNames.add(TEST_TABLE_NAME1);
        expectedTableNames.add(TEST_TABLE_NAME2);
        Set<String> resultTableNames = new HashSet<>();
        resultTableNames.addAll(test.getTableNames());

        assertEquals(resultTableNames, expectedTableNames);
    }

    @Test
    public final void
    testGetTableNamesWhenTwoTablesCreatedAndThenOneDropped()
            throws IOException {
        List<Class<?>> valuesTypes = new ArrayList<>();
        valuesTypes.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(TEST_TABLE_NAME1, valuesTypes);
        test.createTable(TEST_TABLE_NAME2, valuesTypes);

        Set<String> expectedTableNames = new HashSet<>();
        expectedTableNames.add(TEST_TABLE_NAME1);
        expectedTableNames.add(TEST_TABLE_NAME2);

        Set<String> resultTableNames = new HashSet<>();
        resultTableNames.addAll(test.getTableNames());
        assertEquals(expectedTableNames, resultTableNames);

        test.removeTable(TEST_TABLE_NAME1);
        expectedTableNames.remove(TEST_TABLE_NAME1);
        resultTableNames.clear();
        resultTableNames.addAll(test.getTableNames());

        assertEquals(expectedTableNames, resultTableNames);
    }

    /*
    * CreateTable tests.
    */
    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableThrowsExceptionCalledForNullTableName() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(null, types);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableThrowsExceptionCalledForWrongTableName() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(WRONG_TABLE_NAME, types);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableThrowsExceptionCalledForNullColumnTypeList() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(TEST_TABLE_NAME1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableThrowsExceptionCalledForWrongColumnTypeList() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(ArrayList.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(TEST_TABLE_NAME1, types);
    }

    @Test
    public final void testCreateTableOnTheDiskCalledForValidTableName() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(TEST_TABLE_NAME1, types);
        Path newTablePath = testDirectory.resolve(TEST_TABLE_NAME1);

        assertTrue(Files.exists(newTablePath)
                        && Files.isDirectory(newTablePath)
                        && Files.exists(newTablePath.resolve(Utility.TABLE_SIGNATURE)));

    }

    @Test
    public final void testCreateTableReturnsNullCalledForExistentOnDiskTable()
            throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Files.createDirectory(tableDirPath);
        Files.createFile(tableSignaturePath);
        try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
            String s1 = Utility.wrappersToPrimitive.get(Integer.class) + " ";
            writeSig.write(s1.getBytes(Utility.ENCODING));
        }
        TableHolder test = new TableHolder(testDirectory.toString());

        assertNull(test.createTable(TEST_TABLE_NAME1, types));
    }

    @Test
    public final void testCreateTableRewritesClosedTable()
            throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);

        TableHolder test = new TableHolder(testDirectory.toString());
        Table createdTable = test.createTable(TEST_TABLE_NAME1, types);
        ((DBTable)createdTable).close();
        assertNotNull(test.createTable(TEST_TABLE_NAME1, types));
    }

    /*
    * RemoveTable tests.
     */
    @Test
    public final void testRemoveTableFromTheDiskCalledForValidTableName() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(TEST_TABLE_NAME1, types);
        Path newTablePath = testDirectory.resolve(TEST_TABLE_NAME1);
        test.removeTable(TEST_TABLE_NAME1);

        assertFalse(Files.exists(newTablePath));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveTableThrowsExceptionCalledForNullTableName() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public final void testRemoveTableThrowsExceptionIfTableNameDoesNotExist() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.removeTable(TEST_TABLE_NAME1);
    }
    /*
    * Deserialize tests.
     */

    @Test(expected = ParseException.class)
    public final void firstTestDeserializeThrowsExceptionIfValueTypesDoesNotFitTable() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        test.createTable(TEST_TABLE_NAME1, types);
        String value = "[0.5]";
        test.deserialize(test.getTable(TEST_TABLE_NAME1), value);
    }

    @Test(expected = ParseException.class)
    public final void secondTestDeserializeThrowsExceptionIfValueTypesDoesNotFitTable() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        test.createTable(TEST_TABLE_NAME1, types);
        String value = "[true]";
        test.deserialize(test.getTable(TEST_TABLE_NAME1), value);
    }

    @Test(expected = ParseException.class)
    public final void thirdTestDeserializeThrowsExceptionIfValueTypesDoesNotFitTable() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        test.createTable(TEST_TABLE_NAME1, types);
        String value = "[5]";
        test.deserialize(test.getTable(TEST_TABLE_NAME1), value);
    }

    @Test(expected = ParseException.class)
    public final void fourthTestDeserializeThrowsExceptionIfValueTypesDoesNotFitTable() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(Boolean.class);
        test.createTable(TEST_TABLE_NAME1, types);
        String value = "[\"true\"]";
        test.deserialize(test.getTable(TEST_TABLE_NAME1), value);
    }

    @Test
    public final void firstTestDeserializeReturnsStoreableIfAllFits() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        types.add(String.class);
        test.createTable(TEST_TABLE_NAME1, types);
        String value = "[null, null]";
        Storeable result = test.deserialize(test.getTable(TEST_TABLE_NAME1), value);
        Storeable expected = new Record(types);
        expected.setColumnAt(0, null);
        expected.setColumnAt(1, null);

        assertEquals(result.getStringAt(0), expected.getStringAt(0));
        assertEquals(result.getStringAt(1), expected.getStringAt(1));
    }

    @Test
    public final void secondTestDeserializeReturnsStoreableIfAllFits() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        test.createTable(TEST_TABLE_NAME1, types);
        String value = "[1, \"Et cetera\"]";
        Storeable result = test.deserialize(test.getTable(TEST_TABLE_NAME1), value);
        Storeable expected = new Record(types);
        expected.setColumnAt(0, 1);
        expected.setColumnAt(1, "Et cetera");

        assertEquals(result.getIntAt(0), expected.getIntAt(0));

        assertEquals(result.getStringAt(1), expected.getStringAt(1));
    }

    @Test
    public final void thirdTestDeserializeReturnsStoreableIfAllFits() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        test.createTable(TEST_TABLE_NAME1, types);
        String value = "[1, \"Et , 1, cetera\"]";
        Storeable result = test.deserialize(test.getTable(TEST_TABLE_NAME1), value);
        Storeable expected = new Record(types);
        expected.setColumnAt(0, 1);
        expected.setColumnAt(1, "Et , 1, cetera");

        assertEquals(result.getIntAt(0), expected.getIntAt(0));

        assertEquals(result.getStringAt(1), expected.getStringAt(1));
    }

    @Test
    public final void fourthTestDeserializeReturnsStoreableIfAllFits() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        test.createTable(TEST_TABLE_NAME1, types);
        String value = "[1, \"Et \"1\" cetera\"]";
        Storeable result = test.deserialize(test.getTable(TEST_TABLE_NAME1), value);
        Storeable expected = new Record(types);
        expected.setColumnAt(0, 1);
        expected.setColumnAt(1, "Et \"1\" cetera");

        assertEquals(result.getIntAt(0), expected.getIntAt(0));

        assertEquals(result.getStringAt(1), expected.getStringAt(1));
    }

    /*
    * Serialize tests.
     */
    @Test(expected = ColumnFormatException.class)
    public final void firstTestSerializeThrowsExceptionIfStoreableDoesNotFitTable() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Integer.class);
        test.createTable(TEST_TABLE_NAME1, tableTypes);
        List<Class<?>> storeableTypes = new ArrayList<>();
        storeableTypes.add(String.class);
        storeableTypes.add(Boolean.class);
        Storeable storeable = new Record(storeableTypes);
        storeable.setColumnAt(0, null);
        storeable.setColumnAt(1, true);
        test.serialize(test.getTable(TEST_TABLE_NAME1), storeable);
    }

    @Test
    public final void firstTestSerializeReturnsStringIfAllFits() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Integer.class);
        test.createTable(TEST_TABLE_NAME1, tableTypes);
        Storeable storeable = new Record(tableTypes);
        storeable.setColumnAt(0, null);
        storeable.setColumnAt(1, 1);
        String expected = "[null, 1]";
        String result = test.serialize(test.getTable(TEST_TABLE_NAME1), storeable);

        assertEquals(result, expected);
    }

    @Test
    public final void secondTestSerializeReturnsStringIfAllFits() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(Double.class);
        tableTypes.add(String.class);
        test.createTable(TEST_TABLE_NAME1, tableTypes);
        Storeable storeable = new Record(tableTypes);
        storeable.setColumnAt(0, 5);
        storeable.setColumnAt(1, "stuff");
        String expected = "[5.0, \"stuff\"]";
        String result = test.serialize(test.getTable(TEST_TABLE_NAME1), storeable);

        assertEquals(result, expected);
    }

    @Test
    public final void thirdTestSerializeReturnsStringIfAllFits() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Boolean.class);
        test.createTable(TEST_TABLE_NAME1, tableTypes);
        Storeable storeable = new Record(tableTypes);
        storeable.setColumnAt(0, "true");
        storeable.setColumnAt(1, true);
        String expected = "[\"true\", true]";
        String result = test.serialize(test.getTable(TEST_TABLE_NAME1), storeable);

        assertEquals(result, expected);
    }


    /*
    * CreateFor(Table, Values) tests.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public final void testCreateForThrowsExceptionIfColumnsAndValuesAmountAreNotEqual() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Boolean.class);
        test.createTable(TEST_TABLE_NAME1, tableTypes);
        List<String> values = new ArrayList<>();
        values.add("stuff");
        test.createFor(test.getTable(TEST_TABLE_NAME1), values);
    }

    @Test(expected = ColumnFormatException.class)
    public final void testCreateForThrowsExceptionIfColumnAndValuesTypesAreNotCompatible() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Boolean.class);
        test.createTable(TEST_TABLE_NAME1, tableTypes);
        List<Object> values = new ArrayList<>();
        values.add(0, "stuff");
        values.add(1, null);
        test.createFor(test.getTable(TEST_TABLE_NAME1), values);
    }

    @Test
    public final void testCreateForReturnsStringIfAllFits() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Boolean.class);
        test.createTable(TEST_TABLE_NAME1, tableTypes);
        List<Object> values = new ArrayList<>();
        values.add(0, "stuff, stuff");
        values.add(1, false);
        Storeable expected = new Record(tableTypes);
        expected.setColumnAt(0, "stuff, stuff");
        expected.setColumnAt(1, false);
        Storeable result = test.createFor(test.getTable(TEST_TABLE_NAME1), values);

        assertEquals(result.getStringAt(0), expected.getStringAt(0));

        assertEquals(result.getBooleanAt(1), expected.getBooleanAt(1));
    }
    /*
    Close tests
     */

    @Test(expected = IllegalStateException.class)
    public final void TableProviderThrowsIllegalStateExceptionCallPutAfterClose() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.close();
        test.createTable(TEST_TABLE_NAME1, TYPES);
    }

    @Test(expected = IllegalStateException.class)
    public final void TableProviderThrowsIllegalStateExceptionCallGetAfterClose() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.close();
        test.getTable(TEST_TABLE_NAME1);
    }

    @Test(expected = IllegalStateException.class)
    public final void TableProviderThrowsIllegalStateExceptionCallRemoveAfterClose() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.close();
        test.removeTable(TEST_TABLE_NAME1);
    }

    @Test(expected = IllegalStateException.class)
    public final void TableProviderThrowsIllegalStateExceptionCallCreateForAfterClose() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        Table testTable = test.createTable(TEST_TABLE_NAME1, TYPES);
        test.close();
        test.createFor(testTable);
    }

    @Test(expected = IllegalStateException.class)
    public final void TableProviderThrowsIllegalStateExceptionCallCreateFor2AfterClose() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        Table testTable = test.createTable(TEST_TABLE_NAME1, TYPES);
        List<?> list = Arrays.asList(5);
        test.close();
        test.createFor(testTable, list);
    }

    @Test(expected = IllegalStateException.class)
    public final void TableProviderThrowsIllegalStateExceptionCallGetTableNamesAfterClose() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.close();
        test.getTableNames();
    }

    @Test(expected = IllegalStateException.class)
    public final void TableProviderThrowsIllegalStateExceptionCallDeserializeAfterClose() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        Table testTable = test.createTable(TEST_TABLE_NAME1, TYPES);
        String s = "string";
        test.close();
        test.deserialize(testTable, s);
    }

    @Test(expected = IllegalStateException.class)
    public final void TableProviderThrowsIllegalStateExceptionCallSerializeAfterClose() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        Table testTable = test.createTable(TEST_TABLE_NAME1, TYPES);
        Storeable testValue1 = new Record(TYPES);
        testValue1.setColumnAt(0, 1);
        test.close();
        test.serialize(testTable, testValue1);
    }

    @Test
    public final void TableProviderCloseAllCreatedTables() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        Table testTable1 = test.createTable(TEST_TABLE_NAME1, TYPES);
        Table testTable2 = test.createTable(TEST_TABLE_NAME2, TYPES);
        test.close();
        try {
            testTable1.list();
        } catch (IllegalStateException e1) {
            assertEquals("Table " + TEST_TABLE_NAME1 + " was closed\n", e1.getMessage());
        }
        try {
            testTable2.list();
        } catch (IllegalStateException e2) {
            assertEquals("Table " + TEST_TABLE_NAME2 + " was closed\n", e2.getMessage());
        }
    }

    @After
    public final void tearDown() throws IOException {
        Utility.recursiveDeleteCopy(testDirectory);
    }
}