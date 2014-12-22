package ru.fizteh.fivt.students.Bulat_Galiev.proxy.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.proxy.LoggerFactory;
import ru.fizteh.fivt.students.Bulat_Galiev.proxy.Tabledb;
import ru.fizteh.fivt.students.Bulat_Galiev.proxy.TabledbProvider;

public class ProxyLoggerTest {
    private static List<Class<?>> typeList;
    private TableProvider provider;
    private Table table;
    private Path testDir;
    private StringWriter writer = new StringWriter();

    private String get(final String key) {
        Storeable storeableValue = table.get(key);
        if (storeableValue == null) {
            return null;
        }
        String stringValue = provider.serialize(table, storeableValue);
        return stringValue.substring(2, stringValue.length() - 2);
    }

    private String put(final String key, final String value)
            throws ParseException {
        Storeable storeableValue = table.put(key,
                provider.deserialize(table, "[\"" + value + "\"]"));
        if (storeableValue == null) {
            return null;
        }
        String stringValue = provider.serialize(table, storeableValue);
        return stringValue.substring(2, stringValue.length() - 2);
    }

    private String remove(final String key) {
        Storeable storeableValue = table.remove(key);
        if (storeableValue == null) {
            return null;
        }
        String stringValue = provider.serialize(table, storeableValue);
        return stringValue.substring(2, stringValue.length() - 2);
    }

    @Before
    public final void setUp() throws Exception {
        typeList = new ArrayList<Class<?>>();
        typeList.add(String.class);
        String tmpDirPrefix = "Swing_";
        testDir = Files.createTempDirectory(tmpDirPrefix);
        provider = (TableProvider) new LoggerFactory().wrap(writer,
                new TabledbProvider(testDir.toString()), TableProvider.class);

        Path newTablePath = testDir.resolve("table");
        newTablePath.toFile().mkdir();
        Path signature = newTablePath.resolve("signature.tsv");
        signature.toFile().createNewFile();
        String typeString = "class java.lang.String";
        try (RandomAccessFile writeSig = new RandomAccessFile(
                signature.toString(), "rw")) {
            writeSig.write(typeString.getBytes("UTF-8"));
        }
        table = (Table) new LoggerFactory().wrap(writer, new Tabledb(
                newTablePath, "table", provider, typeList), Table.class);
    }

    @Test
    public final void testCreateNormalTable() throws Exception {
        provider.createTable("testTable", typeList);
        System.out.println(writer.toString());
    }

    @Test(expected = IllegalStateException.class)
    public final void testRemoveNotExistingTable() throws Exception {
        provider.removeTable("notExistingTable");
        System.out.println(writer.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetTableThrowsExceptionCalledForNullTableName()
            throws IOException {
        try {
            provider.getTable(null);
        } finally {
            System.out.println(writer.toString());
        }
    }

    @Test
    public final void testSizeCalledForNonEmptyNonCommitedTable()
            throws IOException, ParseException {

        put("key", "value");

        assertEquals(1, table.size());

        System.out.println(writer.toString());
    }

    /*
     * Other.
     */
    @Test
    public final void testGetReturnsNullIfKeyIsNotFound() throws IOException {

        assertNull(get("key"));

        System.out.println(writer.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetThrowsIllegalArgumentExceptionCalledForNullKey()
            throws IOException {
        try {
            table.get(null);
        } catch (IllegalArgumentException e) {
            System.out.println(writer.toString());
            throw e;
        }
    }

    @Test
    public final void testGetCalledForDeletedKeyBeforeCommit()
            throws IOException, ParseException {

        put("key", "value");

        assertEquals("value", remove("key"));

        assertNull(get("key"));

        System.out.println(writer.toString());
    }

    @Test
    public final void testPutReturnsNullIfKeyHasNotBeenWrittenYet()
            throws IOException, ParseException {

        put("key", "value");

        System.out.println(writer.toString());
    }

    @Test
    public final void testCommitReturnsNonZeroChangesPuttingNewRecord()
            throws IOException, ParseException {

        put("key", "value");

        assertEquals(1, table.commit());

        System.out.println(writer.toString());
    }

    @Test
    public final void testRollbackNoChanges() throws IOException,
            ParseException {

        put("key", "value");

        table.rollback();

        assertEquals(0, table.size());

        assertEquals(0, table.rollback());

        System.out.println(writer.toString());
    }

    @Test
    public final void testGetColumnsCount() throws IOException {

        assertEquals(1, table.getColumnsCount());

        System.out.println(writer.toString());
    }

    @Test
    public final void testGetColumnType() throws IOException {

        assertEquals(String.class, table.getColumnType(0));

        System.out.println(writer.toString());
    }

    @Test
    public final void testListCalledForEmptyTable() throws IOException {

        assertTrue(table.list().isEmpty());

        System.out.println(writer.toString());
    }

    @Test
    public final void testListCalledForNonEmptyNewTable() throws IOException,
            ParseException {
        put("key", "value");
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add("key");
        Set<String> tableKeySet = new HashSet<>();
        tableKeySet.addAll(table.list());

        assertEquals(expectedKeySet, tableKeySet);

        System.out.println(writer.toString());
    }

    @After
    public final void tearDown() throws Exception {
        Cleaner.clean(testDir.toFile());
    }

}
