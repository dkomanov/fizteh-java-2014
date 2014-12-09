package ru.fizteh.fivt.students.anastasia_ermolaeva.storeable.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.DatabaseFormatException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.storeable.DBTable;
import ru.fizteh.fivt.students.anastasia_ermolaeva.storeable.Record;
import ru.fizteh.fivt.students.anastasia_ermolaeva.storeable.TableHolder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TableHolderTest {
    private final Path testDirectory =
            Paths.get(System.getProperty("fizteh.db.dir"));
    private final String tableDirectoryName = "Тест";
    private final String testFile = "filename.txt";
    private final Path tableDirPath = testDirectory.resolve(tableDirectoryName);
    private final Path tableSubdirPath = tableDirPath.resolve("1.dir");
    private final String validFileName = "1.dat";
    private final Path tableSignaturePath = tableDirPath.resolve(Utility.TABLE_SIGNATURE);
    private final String testTableName = "Тестовая таблица";
    private final String wrongTableName = ".";

    @Before
    public final void setUp() throws IOException {
        if (!Files.exists(testDirectory)) {
            Files.createDirectory(testDirectory);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testHolderThrowsExceptionCreatedNotForDirectory()
            throws IOException {
        Path newFilePath = testDirectory.resolve(testFile);
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
        Path newFilePath = testDirectory.resolve(testFile);
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
        Path newFilePath = tableDirPath.resolve(testFile);
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
        Path newFilePath = tableSubdirPath.resolve(testFile);
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
        Path newFilePath = tableSubdirPath.resolve(validFileName);
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
            Path newFilePath = tableSubdirPath.resolve(validFileName);
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

        assertNull(test.getTable(testTableName));
    }

    @Test(expected = IllegalStateException.class)
    public final void testGetTableThrowsExceptionIfTableWasRemoved() throws IOException {
        List<Class<?>> valuesTypes = new ArrayList<>();
        valuesTypes.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(testTableName, valuesTypes);
        test.removeTable(testTableName);
        test.getTable(testTableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetTableThrowsExceptionCalledForNullTableName() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetTableThrowsExceptionCalledForWrongTableName() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.getTable(wrongTableName);
    }

    @Test
    public final void testGetTableReturnsSameObjectsCalledTwice() throws IOException {
        List<Class<?>> valuesTypes = new ArrayList<>();
        valuesTypes.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(testTableName, valuesTypes);

        assertTrue(test.getTable(testTableName) == test.getTable(testTableName));
    }

    /*
    * GetTableNames tests.
     */
    @Test
    public final void testGetTableNamesReturnNamesOfTwoCreatedTables() throws IOException {
        List<Class<?>> valuesTypes = new ArrayList<>();
        valuesTypes.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(testTableName, valuesTypes);
        test.createTable(tableDirectoryName, valuesTypes);

        List<String> expectedTableNames = new ArrayList<>();
        expectedTableNames.add(testTableName);
        expectedTableNames.add(tableDirectoryName);

        assertEquals(test.getTableNames(), expectedTableNames);
    }

    @Test
    public final void
    testGetTableNamesWhenTwoTablesCreatedAndThenOneDropped()
            throws IOException {
        List<Class<?>> valuesTypes = new ArrayList<>();
        valuesTypes.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(testTableName, valuesTypes);
        test.createTable(tableDirectoryName, valuesTypes);

        List<String> expectedTableNames = new ArrayList<>();
        expectedTableNames.add(testTableName);
        expectedTableNames.add(tableDirectoryName);

        assertEquals(test.getTableNames(), expectedTableNames);

        test.removeTable(tableDirectoryName);
        expectedTableNames.remove(tableDirectoryName);

        assertEquals(test.getTableNames(), expectedTableNames);
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
        test.createTable(wrongTableName, types);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableThrowsExceptionCalledForNullColumnTypeList() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(tableDirectoryName, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableThrowsExceptionCalledForWrongColumnTypeList() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(ArrayList.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(tableDirectoryName, types);
    }

    @Test
    public final void testCreateTableOnTheDiskCalledForValidTableName() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(testTableName, types);
        Path newTablePath = testDirectory.resolve(testTableName);

        assertTrue(newTablePath.toFile().exists()
                        && newTablePath.toFile().isDirectory()
                        && newTablePath.resolve(Utility.TABLE_SIGNATURE).toFile().exists()
        );
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

        assertNull(test.createTable(tableDirectoryName, types));
    }

    /*
    * RemoveTable tests.
     */
    @Test
    public final void testRemoveTableFromTheDiskCalledForValidTableName() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        TableHolder test = new TableHolder(testDirectory.toString());
        test.createTable(testTableName, types);
        Path newTablePath = testDirectory.resolve(testTableName);
        test.removeTable(testTableName);

        assertFalse(newTablePath.toFile().exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveTableThrowsExceptionCalledForNullTableName() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public final void testRemoveTableThrowsExceptionIfTableNameDoesNotExist() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        test.removeTable(testTableName);
    }
    /*
    * Deserialize tests.
     */

    @Test(expected = ParseException.class)
    public final void firstTestDeserializeThrowsExceptionIfValueTypesDoesNotFitTable() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        test.createTable(testTableName, types);
        String value = "[0.5]";
        test.deserialize(test.getTable(testTableName), value);
    }

    @Test(expected = ParseException.class)
    public final void secondTestDeserializeThrowsExceptionIfValueTypesDoesNotFitTable() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        test.createTable(testTableName, types);
        String value = "[true]";
        test.deserialize(test.getTable(testTableName), value);
    }

    @Test(expected = ParseException.class)
    public final void thirdTestDeserializeThrowsExceptionIfValueTypesDoesNotFitTable() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        test.createTable(testTableName, types);
        String value = "[5]";
        test.deserialize(test.getTable(testTableName), value);
    }

    @Test(expected = ParseException.class)
    public final void fourthTestDeserializeThrowsExceptionIfValueTypesDoesNotFitTable() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(Boolean.class);
        test.createTable(testTableName, types);
        String value = "[\"true\"]";
        test.deserialize(test.getTable(testTableName), value);
    }

    @Test
    public final void firstTestDeserializeReturnsStoreableIfAllFits() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        types.add(String.class);
        test.createTable(testTableName, types);
        String value = "[null, null]";
        Storeable result = test.deserialize(test.getTable(testTableName), value);
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
        test.createTable(testTableName, types);
        String value = "[1, \"Et cetera\"]";
        Storeable result = test.deserialize(test.getTable(testTableName), value);
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
        test.createTable(testTableName, types);
        String value = "[1, \"Et , 1, cetera\"]";
        Storeable result = test.deserialize(test.getTable(testTableName), value);
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
        test.createTable(testTableName, types);
        String value = "[1, \"Et \"1\" cetera\"]";
        Storeable result = test.deserialize(test.getTable(testTableName), value);
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
        test.createTable(testTableName, tableTypes);
        List<Class<?>> storeableTypes = new ArrayList<>();
        storeableTypes.add(String.class);
        storeableTypes.add(Boolean.class);
        Storeable storeable = new Record(storeableTypes);
        storeable.setColumnAt(0, null);
        storeable.setColumnAt(1, true);
        test.serialize(test.getTable(testTableName), storeable);
    }

    @Test
    public final void firstTestSerializeReturnsStringIfAllFits() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Integer.class);
        test.createTable(testTableName, tableTypes);
        Storeable storeable = new Record(tableTypes);
        storeable.setColumnAt(0, null);
        storeable.setColumnAt(1, 1);
        String expected = "[null, 1]";
        String result = test.serialize(test.getTable(testTableName), storeable);

        assertEquals(result, expected);
    }

    @Test
    public final void secondTestSerializeReturnsStringIfAllFits() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(Double.class);
        tableTypes.add(String.class);
        test.createTable(testTableName, tableTypes);
        Storeable storeable = new Record(tableTypes);
        storeable.setColumnAt(0, 5);
        storeable.setColumnAt(1, "stuff");
        String expected = "[5.0, \"stuff\"]";
        String result = test.serialize(test.getTable(testTableName), storeable);

        assertEquals(result, expected);
    }

    @Test
    public final void thirdTestSerializeReturnsStringIfAllFits() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Boolean.class);
        test.createTable(testTableName, tableTypes);
        Storeable storeable = new Record(tableTypes);
        storeable.setColumnAt(0, "true");
        storeable.setColumnAt(1, true);
        String expected = "[\"true\", true]";
        String result = test.serialize(test.getTable(testTableName), storeable);

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
        test.createTable(testTableName, tableTypes);
        List<String> values = new ArrayList<>();
        values.add("stuff");
        test.createFor(test.getTable(testTableName), values);
    }

    @Test(expected = ColumnFormatException.class)
    public final void testCreateForThrowsExceptionIfColumnAndValuesTypesAreNotCompatible() throws IOException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Boolean.class);
        test.createTable(testTableName, tableTypes);
        List<Object> values = new ArrayList<>();
        values.add(0, "stuff");
        values.add(1, null);
        test.createFor(test.getTable(testTableName), values);
    }

    @Test
    public final void testCreateForReturnsStringIfAllFits() throws IOException, ParseException {
        TableHolder test = new TableHolder(testDirectory.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Boolean.class);
        test.createTable(testTableName, tableTypes);
        List<Object> values = new ArrayList<>();
        values.add(0, "stuff, stuff");
        values.add(1, false);
        Storeable expected = new Record(tableTypes);
        expected.setColumnAt(0, "stuff, stuff");
        expected.setColumnAt(1, false);
        Storeable result = test.createFor(test.getTable(testTableName), values);

        assertEquals(result.getStringAt(0), expected.getStringAt(0));

        assertEquals(result.getBooleanAt(1), expected.getBooleanAt(1));
    }


    @After
    public final void tearDown() throws IOException {
        Utility.recursiveDeleteCopy(testDirectory);
    }
}