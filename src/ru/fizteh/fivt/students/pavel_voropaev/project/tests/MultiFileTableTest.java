package ru.fizteh.fivt.students.pavel_voropaev.project.tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.ContainsWrongFilesException;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.TableDoesNotExistException;
import ru.fizteh.fivt.students.pavel_voropaev.project.database.DatabaseFactory;
import ru.fizteh.fivt.students.pavel_voropaev.project.database.MultiFileTable;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class MultiFileTableTest {
    private Path databasePath;
    private final String databaseDirectory = "test";
    private final String tableName = "testTable";
    private final String key = "testKey";
    private final String key2 = "ключ";
    private final String valueJSON = "[42, \"This is string.\"]";
    private Storeable valueStoreable; // Go to @Before to see initialization
    private Storeable value2Storeable;

    private final String encoding = "UTF-8";
    private final String keyToRead = "a";
    private final String directoryToRead = "1.dir";
    private final String goodFileToRead = "6.dat";
    private final String badFileToRead = "1.dat";

    private final String stringWithBannedSymbols = "Моя<Папка";
    private static TableProviderFactory factory;
    private TableProvider provider;
    private Table table;

    @Rule
    public TemporaryFolder testDirectory = new TemporaryFolder();

    @BeforeClass
    public static void setUpClass() throws IOException {
        factory = new DatabaseFactory();
    }

    @Before
    public void setUp() throws IOException {
        databasePath = testDirectory.newFolder(databaseDirectory).toPath();
        provider = factory.create(databasePath.toString());
        table = provider.createTable(tableName, Arrays.asList(Integer.class, String.class));
        valueStoreable = provider.createFor(table, Arrays.asList(42, "This is string."));
        value2Storeable = provider.createFor(table, Arrays.asList(-100, "Значение"));
    }

    @Test
    public void tableInitializationForExistingEmptyDirectory() throws IOException {
        Table table = new MultiFileTable(databasePath, tableName, provider);
        assertNotNull(table);
    }

    @Test
    public void tableInitializationForExistingDirectoryWithCorrectFiles() throws IOException {
        Path directoryInTable = databasePath.resolve(tableName).resolve(directoryToRead);
        Files.createDirectory(directoryInTable);
        Path fileInTable = directoryInTable.resolve(goodFileToRead);
        testWriter(fileInTable, keyToRead, valueJSON);

        Table table = new MultiFileTable(databasePath, tableName, provider);
        assertNotNull(table);
    }

    @Test(expected = ContainsWrongFilesException.class)
    public void tableInitializationForExistingDirectoryWithWrongFiles() throws IOException {
        Path directoryInTable = databasePath.resolve(tableName).resolve(directoryToRead);
        Files.createDirectory(directoryInTable);
        Path fileInTable = directoryInTable.resolve(badFileToRead);
        testWriter(fileInTable, keyToRead, valueJSON);

        new MultiFileTable(databasePath, tableName, provider);
    }

    @Test(expected = ContainsWrongFilesException.class)
    public void tableInitializationForExistingDirectoryWithWrongStructure() throws IOException {
        Path directoryInTable = databasePath.resolve(tableName).resolve(directoryToRead);
        Files.createFile(directoryInTable);

        new MultiFileTable(databasePath, tableName, provider);
    }

    @Test(expected = RuntimeException.class)
    public void tableInitializationWrongDatabase() throws IOException {
        databasePath = databasePath.resolve("notExistingFile");

        new MultiFileTable(databasePath, tableName, provider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getForNullKey() throws IOException {
        table.get(null);
    }

    @Test
    public void getForNotExistingKey() throws IOException {
        assertEquals(null, table.get(key));
    }

    @Test
    public void getForExistingInDiffKey() throws IOException {
        table.put(key, valueStoreable);
        assertEquals(valueStoreable, table.get(key));
    }

    @Test
    public void putNewKeyCommitAndGet() throws IOException {
        table.put(key, valueStoreable);
        table.commit();
        assertEquals(1, table.size());
        assertEquals(valueStoreable, table.get(key));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putForNullKey() throws IOException {
        table.put(null, valueStoreable);
    }

    @Test
    public void putTheSameKey() throws IOException {
        table.put(key, valueStoreable);
        table.commit();
        table.put(key, value2Storeable);
        assertEquals(value2Storeable, table.get(key));
        assertEquals(1, table.size());
    }

    @Test
    public void putForExistingInDiffKey() throws IOException {
        table.put(key, valueStoreable);
        table.put(key, value2Storeable);
        assertEquals(value2Storeable, table.get(key));
        assertEquals(1, table.size());
    }

    @Test
    public void putForWrittenOnDiskKey() throws IOException {
        table.put(key, valueStoreable);
        table.commit();
        table.put(key, value2Storeable);
        assertEquals(value2Storeable, table.get(key));
        assertEquals(1, table.size());
    }

    @Test
    public void putForWrittenOnDiskKeyTheSameValue() throws IOException {
        table.put(key, valueStoreable);
        table.commit();
        table.put(key, valueStoreable);
        assertEquals(1, table.size());
        assertEquals(0, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void removeForNotExistingKey() throws IOException {
        assertEquals(null, table.remove(key));
        assertEquals(0, table.size());
    }

    @Test
    public void removeForWrittenOnDiskKey() throws IOException {
        table.put(key, valueStoreable);
        table.commit();
        assertEquals(valueStoreable, table.remove(key));
        assertEquals(0, table.size());
    }

    @Test
    public void removeForExistingInDiffButNotOnDiskKey() throws IOException {
        table.put(key, valueStoreable);
        assertEquals(valueStoreable, table.remove(key));
        assertEquals(0, table.size());
    }

    @Test
    public void removeForExistingInDiffAndOnDiskKey() throws IOException {
        table.put(key, valueStoreable);
        table.commit();
        table.put(key, value2Storeable);
        assertEquals(value2Storeable, table.remove(key));
        assertEquals(0, table.size());
    }

    @Test
    public void rollbackAfterPut() throws IOException {
        table.put(key, valueStoreable);
        table.rollback();
        assertEquals(0, table.size());
        assertEquals(null, table.get(key));
    }

    @Test
    public void rollbackAfterRemove() throws IOException {
        table.put(key, valueStoreable);
        table.commit();
        table.remove(key);
        table.rollback();
        assertEquals(1, table.size());
        assertEquals(valueStoreable, table.get(key));
    }

    @Test
    public void listForEmptyTable() throws IOException {
        assertTrue(table.list().isEmpty());
    }

    @Test
    public void listForKeysInDiff() throws IOException {
        table.put(key, valueStoreable);
        table.put(key2, value2Storeable);

        Set<String> expectedSet = new HashSet<>();
        expectedSet.add(key);
        expectedSet.add(key2);
        Set<String> returnedSet = new HashSet<>();
        returnedSet.addAll(table.list());
        assertEquals(expectedSet, returnedSet);
    }

    @Test
    public void listForKeysInDiffAndWrittenOnDisk() throws IOException {
        table.put(key, valueStoreable);
        table.put(key2, value2Storeable);
        table.commit();
        table.remove(key2);

        Set<String> expectedSet = new HashSet<>();
        expectedSet.add(key);
        Set<String> returnedSet = new HashSet<>();
        returnedSet.addAll(table.list());
        assertEquals(expectedSet, returnedSet);
    }

    @Test
    public void getName() {
        assertEquals(tableName, table.getName());
    }

    @Test(expected = TableDoesNotExistException.class)
    public void getColumnsCountWasRemoved() throws IOException {
        provider.removeTable(tableName);
        table.getColumnsCount();
    }

    @Test(expected = TableDoesNotExistException.class)
    public void getWasRemoved() throws IOException {
        provider.removeTable(tableName);
        table.get(key);
    }

    @Test(expected = TableDoesNotExistException.class)
    public void getNumberOfUncommittedChangesWasRemoved() throws IOException {
        provider.removeTable(tableName);
        table.getNumberOfUncommittedChanges();
    }

    @Test(expected = TableDoesNotExistException.class)
    public void getNameWasRemoved() throws IOException {
        provider.removeTable(tableName);
        table.getName();
    }

    @Test(expected = TableDoesNotExistException.class)
    public void commitWasRemoved() throws IOException {
        provider.removeTable(tableName);
        table.commit();
    }

    @Test(expected = TableDoesNotExistException.class)
    public void listWasRemoved() throws IOException {
        provider.removeTable(tableName);
        table.list();
    }

    @Test(expected = TableDoesNotExistException.class)
    public void keyWasRemoved() throws IOException {
        provider.removeTable(tableName);
        table.put(key, valueStoreable);
    }

    @Test(expected = TableDoesNotExistException.class)
    public void removeWasRemoved() throws IOException {
        provider.removeTable(tableName);
        table.remove(key);
    }

    @Test(expected = TableDoesNotExistException.class)
    public void sizeWasRemoved() throws IOException {
        provider.removeTable(tableName);
        table.size();
    }

    private void testWriter(Path fileInTable, String keyToWrite, String valueToWrite) throws IOException {
        try (DataOutputStream file = new DataOutputStream(new FileOutputStream(fileInTable.toString()))) {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            byte[] keyByte = keyToWrite.getBytes(encoding);
            byte[] valueByte = valueToWrite.getBytes(encoding);

            file.write(buffer.putInt(0, keyByte.length).array());
            file.write(keyByte);

            file.write(buffer.putInt(0, valueByte.length).array());
            file.write(valueByte);
        }
    }
}
