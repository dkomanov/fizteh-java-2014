package ru.fizteh.fivt.students.PotapovaSofia.storeable.Tests;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.DbTable;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.DbTableProvider;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.StoreableImpl;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.StoreableMain;

public class DbTableProviderTest {

    private final Path testFolder = Paths.get(System.getProperty("java.io.tmpdir"), "testFolder");
    private final String dirName = "test";
    private final Path dirPath = testFolder.resolve(dirName);
    private final Path signaturePath = dirPath.resolve(StoreableMain.TABLE_SIGNATURE);
    private final Path validPath = dirPath.resolve("1.dir");
    private final Path validFile = validPath.resolve("1.dat");

    private final String testTableName = "testTable";
    private final String testFile = "filename.txt";

    @Before
    public void setUp() {
        testFolder.toFile().mkdir();
    }

    @Test
    public void testOnCreatingForNonexistentDirectory() {
        new DbTableProvider(dirPath.toString());
        assertTrue(dirPath.toFile().exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnCreatingNotForDirectory() throws IOException {
        dirPath.toFile().createNewFile();
        new DbTableProvider(dirPath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnCreatingForInvalidPath() {
        new DbTableProvider("\0");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testOnCreatingTableWithoutSignature() throws IOException {
        dirPath.toFile().createNewFile();
        new DbTableProvider(testFolder.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testOnCreateingTableWithEmptySignature() throws IOException {
        dirPath.toFile().mkdir();
        signaturePath.toFile().createNewFile();
        new DbTableProvider(testFolder.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testOnCreatingTableWithInvalidSignature() throws IOException {
        dirPath.toFile().mkdir();
        signaturePath.toFile().createNewFile();
        try (RandomAccessFile writeSig = new RandomAccessFile(signaturePath.toString(), "rw")) {
            String s1 = StoreableMain.AVAILABLE_CLASSES.get(Integer.class) + " ";
            String s2 = ArrayList.class.getSimpleName() + " ";
            writeSig.write(s1.getBytes(StoreableMain.ENCODING));
            writeSig.write(s2.getBytes(StoreableMain.ENCODING));
        }
        new DbTableProvider(testFolder.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testOnCreatingTableWithUnexpectedFiles() throws IOException {
        dirPath.toFile().mkdir();
        Path newFilePath = dirPath.resolve(testFile);
        newFilePath.toFile().createNewFile();
        new DbTableProvider(testFolder.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testOnCreatingTableWithEmptyDirectory() throws IOException {
        dirPath.toFile().mkdir();
        signaturePath.toFile().createNewFile();
        try (RandomAccessFile writeSig = new RandomAccessFile(signaturePath.toString(), "rw")) {
            String s1 = StoreableMain.AVAILABLE_CLASSES.get(Integer.class) + " ";
            writeSig.write(s1.getBytes(StoreableMain.ENCODING));
        }
        validPath.toFile().mkdir();
        new DbTableProvider(testFolder.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testOnCreatingTableWithEmptyFile() throws IOException {
        dirPath.toFile().mkdir();
        signaturePath.toFile().createNewFile();
        try (RandomAccessFile writeSig = new RandomAccessFile(signaturePath.toString(), "rw")) {
            String s1 = StoreableMain.AVAILABLE_CLASSES.get(Integer.class) + " ";
            writeSig.write(s1.getBytes(StoreableMain.ENCODING));
        }
        validPath.toFile().mkdir();
        validFile.toFile().createNewFile();
        new DbTableProvider(testFolder.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testOnCreatingTableWhenValuesNotCompatibleWithSignature() throws IOException {
        dirPath.toFile().mkdir();
        signaturePath.toFile().createNewFile();
        DataOutputStream out = new DataOutputStream(new FileOutputStream(signaturePath.toString()));
        String s = StoreableMain.AVAILABLE_CLASSES.get(Integer.class) + " ";
        byte[] byteWord = s.getBytes(StoreableMain.ENCODING);
        out.writeInt(byteWord.length);
        out.write(byteWord);

        String key = "key";
        String value = "[1.5]";
        Integer hashCode = Math.abs(key.hashCode());
        Integer dirInt = hashCode % DbTable.DIR_COUNT;
        Integer fileInt = hashCode / DbTable.DIR_COUNT % DbTable.FILE_COUNT;
        Path dir = dirPath.resolve(dirPath.toString() + dirInt + ".dir");
        Path file = dir.resolve(fileInt + ".dat");
        dir.toFile().mkdir();
        file.toFile().createNewFile();

        out = new DataOutputStream(new FileOutputStream(file.toString()));
        byteWord = key.getBytes(StoreableMain.ENCODING);
        out.writeInt(byteWord.length);
        out.write(byteWord);
        byteWord = value.getBytes(StoreableMain.ENCODING);
        out.writeInt(byteWord.length);
        out.write(byteWord);
        new DbTableProvider(testFolder.toString());
    }

    @Test
    public final void testGetTableWhenTableDoesNotExist() throws IOException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        assertNull(test.getTable(testTableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetTableCalledForNull() throws IOException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        test.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableCalledForNullTable() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        test.createTable(null, types);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableCalledForNullTypeList() throws IOException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        test.createTable(dirName, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableCalledForWrongTypeList() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(ArrayList.class);
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        test.createTable(dirName, types);
    }

    @Test
    public final void testCreateTableCalledForValidTableName() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        test.createTable(testTableName, types);
        Path newTablePath = testFolder.resolve(testTableName);
        assertTrue(newTablePath.toFile().exists() && newTablePath.toFile().isDirectory()
                        && newTablePath.resolve(StoreableMain.TABLE_SIGNATURE).toFile().exists()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveTableCalledForNull() throws IOException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        test.removeTable(null);
    }

    @Test
    public final void testRemoveTableCalledForValidTable() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        test.createTable(testTableName, types);
        Path newTablePath = testFolder.resolve(testTableName);
        test.removeTable(testTableName);
        assertFalse(newTablePath.toFile().exists());
    }

    @Test(expected = ParseException.class)
    public final void firstTestDeserialize() throws IOException, ParseException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        test.createTable(testTableName, types);
        String value = "[true]";
        test.deserialize(test.getTable(testTableName), value);
    }

    @Test(expected = ParseException.class)
    public final void secondTestDeserialize() throws IOException, ParseException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        test.createTable(testTableName, types);
        String value = "[true]";
        test.deserialize(test.getTable(testTableName), value);
    }

    @Test(expected = ParseException.class)
    public final void thirdTestDeserialize() throws IOException, ParseException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(Boolean.class);
        test.createTable(testTableName, types);
        String value = "[\"true\"]";
        test.deserialize(test.getTable(testTableName), value);
    }

    @Test
    public final void fourthTestDeserializeIfAllFits() throws IOException, ParseException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);
        types.add(Boolean.class);
        test.createTable(dirName, types);
        String value = "[\"value\", 10, true]";
        Storeable result = test.deserialize(test.getTable(dirName), value);
        Storeable expected = new StoreableImpl(types);
        expected.setColumnAt(0, "value");
        expected.setColumnAt(1, 10);
        expected.setColumnAt(2, true);
        assertEquals(result.getStringAt(0), expected.getStringAt(0));
        assertEquals(result.getIntAt(1), expected.getIntAt(1));
        assertEquals(result.getBooleanAt(2), expected.getBooleanAt(2));
    }

    @Test(expected = ColumnFormatException.class)
    public final void firstTestSerialize() throws IOException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Integer.class);
        test.createTable(dirName, tableTypes);
        List<Class<?>> storeableTypes = new ArrayList<>();
        storeableTypes.add(String.class);
        storeableTypes.add(Boolean.class);
        Storeable storeable = new StoreableImpl(storeableTypes);
        storeable.setColumnAt(0, null);
        storeable.setColumnAt(1, true);
        test.serialize(test.getTable(dirName), storeable);
    }

    @Test
    public final void secondTestSerialize() throws IOException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(Integer.class);
        tableTypes.add(String.class);
        tableTypes.add(Boolean.class);
        test.createTable(dirName, tableTypes);
        Storeable storeable = new StoreableImpl(tableTypes);
        storeable.setColumnAt(0, 5);
        storeable.setColumnAt(1, "value");
        storeable.setColumnAt(2, true);
        String expected = "[5, \"value\", true]";
        String result = test.serialize(test.getTable(dirName), storeable);
        assertEquals(result, expected);
    }

    @Test(expected = ColumnFormatException.class)
    public final void testCreateForIfColumnAndValuesTypesAreNotCompatible() throws IOException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Boolean.class);
        test.createTable(dirName, tableTypes);
        List<Object> values = new ArrayList<>();
        values.add(0, "value");
        values.add(1, 11);
        test.createFor(test.getTable(dirName), values);
    }

    @Test
    public final void testCreateForWhenAllFits() throws IOException, ParseException {
        DbTableProvider test = new DbTableProvider(testFolder.toString());
        List<Class<?>> tableTypes = new ArrayList<>();
        tableTypes.add(String.class);
        tableTypes.add(Boolean.class);
        test.createTable(dirName, tableTypes);
        List<Object> values = new ArrayList<>();
        values.add(0, "value");
        values.add(1, false);
        Storeable expected = new StoreableImpl(tableTypes);
        expected.setColumnAt(0, "value");
        expected.setColumnAt(1, false);
        Storeable result = test.createFor(test.getTable(dirName), values);
        assertEquals(result.getStringAt(0), expected.getStringAt(0));
        assertEquals(result.getBooleanAt(1), expected.getBooleanAt(1));
    }

    @After
    public void tearDown() throws IOException {
        DbTableProvider.recoursiveDelete(testFolder.toFile());
    }
}
