package ru.fizteh.fivt.students.olga_chupakhina.storeable.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.olga_chupakhina.storeable.OTableProvider;
import ru.fizteh.fivt.students.olga_chupakhina.storeable.OStoreable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestOTableProvider {
    private final Path testDirectory
            = Paths.get(System.getProperty("user.dir") + File.separator + "db");
    private final String testTableName = "testTable";
    private final String fileInTableDirectory = "unnecessaryFiles";
    private List<Class<?>> sig;
    private List<Object> obj;
    private String s = "[1,\"1\"]";
    private String wrongNumS = "[1,\"1\", true]";
    private String wrongValueS = "[1, true]";

    @Before
    public final void setUp()
            throws Exception {
        if (!testDirectory.toFile().exists()) {
            testDirectory.toFile().mkdir();
        }
        sig = new ArrayList<>();
        sig.add(Integer.class);
        sig.add(String.class);
        obj = new ArrayList<>();
        obj.add(1);
        obj.add("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableProvaderWithNullDirectoryName() {
        TableProvider pv = new OTableProvider(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateTableWithEmptyName() throws IOException {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.createTable(null, sig);
    }

    @Test
    public final void testCreateTableIfItAlreadyExist() throws IOException {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.createTable(testTableName, sig);
        assertNull(pv.createTable(testTableName, sig));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetTableWithNullName() {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.getTable(null);
    }

    @Test
    public final void testGetTableIfItNotExist() {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        assertNull(pv.getTable(testTableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveTableWithNullName() throws IOException {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public final void testRemoveTableIfItNotExist() throws IOException {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.removeTable(testTableName);
    }

    @Test
    public final void testSerialiser() throws IOException, ParseException {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        Storeable storeable = new OStoreable(obj, sig);
        String p = pv.serialize(pv.createTable(testTableName, sig), storeable);
        assertEquals(s, p);
    }

    @Test(expected = ColumnFormatException.class)
    public final void testSerialiserWithExceprion() throws IOException, ParseException {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        obj.add(true);
        Storeable storeable = new OStoreable(obj, sig);
        String p = pv.serialize(pv.createTable(testTableName, sig), storeable);
        assertEquals(s, p);
    }

    @Test
    public final void testDeerialiser() throws IOException, ParseException {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        Storeable storeable = new OStoreable(obj, sig);
        Storeable storeable1 = pv.deserialize(pv.createTable(testTableName, sig), s);
        assertEquals(storeable.getIntAt(0), storeable1.getIntAt(0));
        assertEquals(storeable.getStringAt(1), storeable1.getStringAt(1));
    }

    @Test(expected = ParseException.class)
    public final void testDeerialiserParseExceptionWithWrongNumbersOfColumns() throws IOException, ParseException {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.deserialize(pv.createTable(testTableName, sig), wrongNumS);
    }

    @Test(expected = ParseException.class)
    public final void testDeerialiserParseExceptionWithWrongValues() throws IOException, ParseException {
        TableProvider pv = new OTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.deserialize(pv.createTable(testTableName, sig), wrongValueS);
    }

    @After
    public final void tearDown() throws Exception {
        for (File currentFile : testDirectory.toFile().listFiles()) {
            if (currentFile.isDirectory()) {
                for (File subFile : currentFile.listFiles()) {
                    subFile.delete();
                }
            }
            currentFile.delete();
        }
        testDirectory.toFile().delete();
    }
}