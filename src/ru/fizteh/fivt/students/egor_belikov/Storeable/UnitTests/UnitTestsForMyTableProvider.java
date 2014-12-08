package ru.fizteh.fivt.students.egor_belikov.Storeable.UnitTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.egor_belikov.Storeable.MyTableProvider;
import ru.fizteh.fivt.students.egor_belikov.Storeable.MyStoreable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UnitTestsForMyTableProvider {
    private final Path testDirectory
            = Paths.get(System.getProperty("user.dir"));
    private final String testTableName = "tableForTesting";
    private String tempString = "[1,\"1\"]";
    private List<Class<?>> sig;
    private List<Object> obj;

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
    public final void createNull() {
        TableProvider pv = new MyTableProvider(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void getNull() {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void removeNull() throws IOException {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void createEmpty() throws IOException {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.createTable(null, sig);
    }

    @Test
    public final void createExists() throws IOException {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.createTable(testTableName, sig);
        assertNull(pv.createTable(testTableName, sig));
    }

    @Test
    public final void getNotExists() {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        assertNull(pv.getTable(testTableName));
    }

    @Test(expected = IllegalStateException.class)
    public final void removeNotExists() throws IOException {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        pv.removeTable(testTableName);
    }

    @Test
    public final void serializerGood() throws IOException, ParseException {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        Storeable storeable = new MyStoreable(obj, sig);
        String p = pv.serialize(pv.createTable(testTableName, sig), storeable);
        assertEquals(tempString, p);
    }

    @Test
    public final void deserializerGood() throws IOException, ParseException {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        Storeable storeable = new MyStoreable(obj, sig);
        Storeable storeable1 = pv.deserialize(pv.createTable(testTableName, sig), tempString);
        assertEquals(storeable.getIntAt(0), storeable1.getIntAt(0));
        assertEquals(storeable.getStringAt(1), storeable1.getStringAt(1));
    }

    @Test(expected = ColumnFormatException.class)
    public final void serializerException() throws IOException, ParseException {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        obj.add(true);
        Storeable storeable = new MyStoreable(obj, sig);
        String p = pv.serialize(pv.createTable(testTableName, sig), storeable);
        assertEquals(tempString, p);
    }

    @Test(expected = ParseException.class)
    public final void testDeerialiserParseExceptionWithWrongNumbersOfColumns() throws IOException, ParseException {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        String wrongNumS = "[1,\"1\", true]";
        pv.deserialize(pv.createTable(testTableName, sig), wrongNumS);
    }

    @Test(expected = ParseException.class)
    public final void testDeerialiserParseExceptionWithWrongValues() throws IOException, ParseException {
        TableProvider pv = new MyTableProvider(testDirectory.toFile().getAbsolutePath());
        String wrongValueS = "[1, true]";
        pv.deserialize(pv.createTable(testTableName, sig), wrongValueS);
    }

    @After
    public final void tearDown() throws Exception {

    }
}
