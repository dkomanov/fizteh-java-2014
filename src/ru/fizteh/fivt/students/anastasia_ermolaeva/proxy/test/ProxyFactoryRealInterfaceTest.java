package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.DBTable;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.ProxyFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.Record;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.TableHolder;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ProxyFactoryRealInterfaceTest {
    private static LoggingProxyFactory proxyFactory = new ProxyFactory();
    private static TableProvider testProvider;
    private static Table testTable;
    private static StringWriter writer = new StringWriter();

    private Path testDirectory;
    private static final String TABLE_NAME = "Таблица1";
    private static Path tableDirectoryPath;
    private static Path tableSignaturePath;
    private static final String TEST_KEY1 = "ключ1";

    private static List<Class<?>> types;
    private static Storeable testValue1;
    private static Storeable testValue2;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        types = new ArrayList<>();
        types.add(Integer.class);
        testValue1 = new Record(types);
        testValue1.setColumnAt(0, 1);
        testValue2 = new Record(types);
        testValue2.setColumnAt(0, 5);

        testDirectory = tempFolder.newFolder().toPath();
        tableDirectoryPath = testDirectory.resolve(TABLE_NAME);
        tableSignaturePath = tableDirectoryPath.resolve(Utility.TABLE_SIGNATURE);
        Files.createDirectory(tableDirectoryPath);
        Files.createFile(tableSignaturePath);
        try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
            String s1 = Utility.WRAPPERS_TO_PRIMITIVE.get(Integer.class);
            writeSig.write(s1.getBytes(Utility.ENCODING));
        }
        testProvider = (TableProvider) proxyFactory.wrap(writer,
                new TableHolder(testDirectory.toString()), TableProvider.class);

        testTable = (Table) proxyFactory.wrap(writer,
                new DBTable(testDirectory, TABLE_NAME, new HashMap<>(), types, testProvider), Table.class);
    }

    @After
    public void tearDown() throws Exception {
        Utility.recursiveDeleteCopy(testDirectory);
    }

    /*
    Tests from task description
     */
    @Test
    public final void testCreateTable() throws IOException {
        types.add(String.class);
        testProvider.createTable("name", types);

        System.out.println(writer.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetTableThrowsExceptionCalledForNullTableName() throws IOException {
        try {
            testProvider.getTable(null);
        } finally {
            System.out.println(writer.toString());
        }
    }

    @Test
    public final void testSizeCalledForNonEmptyNonCommitedTable()
            throws IOException {

        assertNull(testTable.put(TEST_KEY1, testValue1));

        assertEquals(1, testTable.size());

        System.out.println(writer.toString());
    }

    /*
    * Other.
     */
    @Test
    public final void testGetReturnsNullIfKeyIsNotFound()
            throws IOException {

        assertNull(testTable.get(TEST_KEY1));

        System.out.println(writer.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetThrowsIllegalArgumentExceptionCalledForNullKey()
            throws IOException {
        try {
            testTable.get(null);
        } catch (IllegalArgumentException e) {
            System.out.println(writer.toString());
            throw e;
        }
    }


    @Test
    public final void testGetCalledForDeletedKeyBeforeCommit()
            throws IOException {

        assertNull(testTable.put(TEST_KEY1, testValue1));

        assertEquals(testValue1.getIntAt(0), testTable.remove(TEST_KEY1).getIntAt(0));

        assertNull(testTable.get(TEST_KEY1));

        System.out.println(writer.toString());
    }


    @Test
    public final void testPutReturnsNullIfKeyHasNotBeenWrittenYet()
            throws IOException {

        assertNull(testTable.put(TEST_KEY1, testValue1));

        System.out.println(writer.toString());
    }

    @Test
    public final void testPutReturnsOldValueIfKeyExists()
            throws IOException {
        testTable.put(TEST_KEY1, testValue1);

        assertEquals(testValue1.getIntAt(0), testTable.put(TEST_KEY1, testValue2).getIntAt(0));

        System.out.println(writer.toString());
    }


    @Test
    public final void testCommitReturnsNonZeroChangesPuttingNewRecord()
            throws IOException {

        assertNull(testTable.put(TEST_KEY1, testValue1));

        assertEquals(1, testTable.commit());

        System.out.println(writer.toString());
    }


    @Test
    public final void testRollbackNoChanges()
            throws IOException {

        assertNull(testTable.put(TEST_KEY1, testValue1));

        testTable.rollback();

        assertEquals(0, testTable.size());

        assertEquals(0, testTable.rollback());

        System.out.println(writer.toString());
    }

    @Test
    public void testGetColumnsCount() throws IOException {

        assertEquals(1, testTable.getColumnsCount());

        System.out.println(writer.toString());
    }

    @Test
    public void testGetColumnType() throws IOException {

        assertEquals(Integer.class, testTable.getColumnType(0));

        System.out.println(writer.toString());
    }

    @Test
    public final void testListCalledForEmptyTable()
            throws IOException {

        assertTrue(testTable.list().isEmpty());

        System.out.println(writer.toString());
    }

    @Test
    public final void testListCalledForNonEmptyNewTable()
            throws IOException {

        assertNull(testTable.put(TEST_KEY1, testValue1));

        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(TEST_KEY1);
        Set<String> tableKeySet = new HashSet<>();
        tableKeySet.addAll(testTable.list());

        assertEquals(expectedKeySet, tableKeySet);

        System.out.println(writer.toString());
    }
}
