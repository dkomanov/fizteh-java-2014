package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.ContainsWrongFilesException;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.Table;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultiFileTableTest {
    private Path databasePath;
    private final String databaseDirectory = "test";
    private final String tableName = "testTable";
    private final String fileName = "testFile";
    private final String key = "testKey";
    private final String key2 = "ключ";
    private final String value = "testValue";
    private final String value2 = "значение";

    private final String encoding = "UTF-8";
    private final String keyToRead = "a";
    private final String directoryToRead = "1.dir";
    private final String goodFileToRead = "6.dat";
    private final String badFileToRead = "1.dat";

    private final String stringWithBannedSymbols = "Где\\Моя|Любимая<Папка?";

    @Rule
    public TemporaryFolder testDirectory = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        databasePath = testDirectory.newFolder(databaseDirectory).toPath();
    }

    @Test
    public void tableInitializationForNewDirectory() throws IOException {
        new MultiFileTable(databasePath, tableName);
    }

    @Test
    public void tableInitializationForExistingEmptyDirectory() throws IOException {
        Files.createDirectory(databasePath.resolve(tableName));
        new MultiFileTable(databasePath, tableName);
    }

    @Test
    public void tableInitializationForExistingDirectoryWithCorrectFiles() throws IOException {
        Path tablePath = databasePath.resolve(tableName);
        Files.createDirectory(tablePath);
        Path directoryInTable = tablePath.resolve(directoryToRead);
        Files.createDirectory(directoryInTable);
        Path fileInTable = directoryInTable.resolve(goodFileToRead);

        localWriter(fileInTable, keyToRead);

        new MultiFileTable(databasePath, tableName);
    }

    @Test(expected = ContainsWrongFilesException.class)
    public void tableInitializationForExistingDirectoryWithWrongFiles() throws IOException {
        Path tablePath = databasePath.resolve(tableName);
        Files.createDirectory(tablePath);
        Path directoryInTable = tablePath.resolve(directoryToRead);
        Files.createDirectory(directoryInTable);
        Path fileInTable = directoryInTable.resolve(badFileToRead);

        localWriter(fileInTable, keyToRead);
        new MultiFileTable(databasePath, tableName);
    }

    @Test(expected = ContainsWrongFilesException.class)
    public void tableInitializationForExistingDirectoryWithWrongStructure() throws IOException {
        Path tablePath = databasePath.resolve(tableName);
        Files.createDirectory(tablePath);
        Path directoryInTable = tablePath.resolve(directoryToRead);
        Files.createFile(directoryInTable);

        new MultiFileTable(databasePath, tableName);
    }

    @Test(expected = RuntimeException.class)
    public void tableInitializationWrongDatabase() throws IOException {
        databasePath = databasePath.resolve("notExistingFile");
        new MultiFileTable(databasePath, tableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getForNullKey() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.get(null);
    }

    @Test
    public void getForNotExistingKey() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        assertEquals(null, test.get(key));
    }

    @Test
    public void getForExistingInDiffKey() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(key, value);
        assertEquals(value, test.get(key));
    }

    @Test
    public void putNewKeyCommitAndGet() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(key, value);
        test.commit();
        assertEquals(1, test.size());
        assertEquals(value, test.get(key));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putForNullKey() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(null, value);
    }

    @Test
    public void putForExistingInDiffKey() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(key, value);
        test.put(key, value2);
        assertEquals(value2, test.get(key));
        assertEquals(1, test.size());
    }

    @Test
    public void putForWrittenOnDiskKey() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(key, value);
        test.commit();
        test.put(key, value2);
        assertEquals(value2, test.get(key));
        assertEquals(1, test.size());
    }

    @Test
    public void removeForWrittenOnDiskKey() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(key, value);
        test.commit();
        assertEquals(value, test.remove(key));
        assertEquals(0, test.size());
    }

    @Test
    public void removeForExistingInDiffKey() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(key, value);
        assertEquals(value, test.remove(key));
        assertEquals(0, test.size());
    }

    @Test
    public void rollbackAfterPut() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(key, value);
        test.rollback();
        assertEquals(0, test.size());
        assertEquals(null, test.get(key));
    }

    @Test
    public void rollbackAfterRemove() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(key, value);
        test.commit();
        test.remove(key);
        test.rollback();
        assertEquals(1, test.size());
        assertEquals(value, test.get(key));
    }

    @Test
    public void listForEmptyTable() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        assertTrue(test.list().isEmpty());
    }

    @Test
    public void listForKeysInDiff() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(key, value);
        test.put(key2, value2);

        Set<String> expectedSet = new HashSet<>();
        expectedSet.add(key);
        expectedSet.add(key2);
        Set<String> returnedSet = new HashSet<>();
        returnedSet.addAll(test.list());
        assertEquals(expectedSet, returnedSet);
    }

    @Test
    public void listForKeysInDiffAndWrittenOnDisk() throws IOException {
        Table test = new MultiFileTable(databasePath, tableName);
        test.put(key, value);
        test.put(key2, value2);
        test.commit();
        test.remove(key2);

        Set<String> expectedSet = new HashSet<>();
        expectedSet.add(key);
        Set<String> returnedSet = new HashSet<>();
        returnedSet.addAll(test.list());
        assertEquals(expectedSet, returnedSet);
    }

    private void localWriter(Path fileInTable, String keyToRead) throws IOException {
        try (DataOutputStream file = new DataOutputStream(
                new FileOutputStream(fileInTable.toString()))) {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            byte[] keyByte = keyToRead.getBytes(encoding);
            byte[] valueByte = value.getBytes(encoding);

            file.write(buffer.putInt(0, keyByte.length).array());
            file.write(keyByte);

            file.write(buffer.putInt(0, valueByte.length).array());
            file.write(valueByte);
        }
    }

}
