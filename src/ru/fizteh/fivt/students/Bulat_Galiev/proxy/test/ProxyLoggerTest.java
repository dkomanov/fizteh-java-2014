package ru.fizteh.fivt.students.Bulat_Galiev.proxy.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
    private String correctBeginProvider = "<?xml version=\"1.0\" ?><invoke timestamp=\"\" "
            + "class=\"ru.fizteh.fivt.students.Bulat_Galiev.proxy.TabledbProvider\" name=";
    private String correctBeginTable = "<?xml version=\"1.0\" ?><invoke timestamp=\"\" "
            + "class=\"ru.fizteh.fivt.students.Bulat_Galiev.proxy.Tabledb\" name=";
    private StringWriter writer = new StringWriter();

    private String get(final String key) {
        Storeable storeableValue = table.get(key);
        if (storeableValue == null) {
            return null;
        }
        String stringValue = provider.serialize(table, storeableValue);
        return stringValue.substring(2, stringValue.length() - 2);
    }

    public final String getAnswer() {
        String answerString = writer.toString();
        answerString = answerString.replaceAll("timestamp=\"\\d*\"",
                "timestamp=\"\"");
        return answerString.replaceAll("\\[.*\\]", "[]");
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
        String correct = correctBeginProvider
                + "\"createTable\">"
                + "<arguments>"
                + "<argument>testTable</argument>"
                + "<argument><list><value>class java.lang.String</value></list></argument>"
                + "</arguments>" + "<return>Tabledb[]</return>" + "</invoke>";
        assertEquals(correct, getAnswer());
    }

    @Test
    public final void testRemoveNotExistingTable() throws Exception {
        String correct = correctBeginProvider
                + "\"removeTable\">"
                + "<arguments>"
                + "<argument>notExistingTable</argument>"
                + "</arguments>"
                + "<thrown>java.lang.IllegalStateException: notExistingTable does not exist</thrown>"
                + "</invoke>";
        try {
            provider.removeTable("notExistingTable");
        } catch (IllegalStateException e) {
            assertEquals(correct, getAnswer());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetTableThrowsExceptionCalledForNullTableName()
            throws IOException {
        String correct = correctBeginProvider
                + "\"getTable\">"
                + "<arguments>"
                + "<argument><null/></argument>"
                + "</arguments>"
                + "<thrown>java.lang.IllegalArgumentException: Null name</thrown>"
                + "</invoke>";
        try {
            provider.getTable(null);
        } catch (IllegalStateException e) {
            assertEquals(correct, getAnswer());
        }
    }

    @Test
    public final void testGetReturnsNullIfKeyIsNotFound() throws IOException {
        String correct = correctBeginTable + "\"get\">" + "<arguments>"
                + "<argument>key</argument>" + "</arguments>"
                + "<return><null/></return>" + "</invoke>";
        get("key");
        assertEquals(correct, getAnswer());
    }

    @Test
    public final void testGetThrowsIllegalArgumentExceptionCalledForNullKey()
            throws IOException {
        String correct = correctBeginTable
                + "\"get\">"
                + "<arguments>"
                + "<argument><null/></argument>"
                + "</arguments>"
                + "<thrown>java.lang.IllegalArgumentException: Null key</thrown>"
                + "</invoke>";
        try {
            table.get(null);
        } catch (IllegalArgumentException e) {
            assertEquals(correct, getAnswer());
        }
    }

    @After
    public final void tearDown() throws Exception {
        Cleaner.clean(testDir.toFile());
    }

}
