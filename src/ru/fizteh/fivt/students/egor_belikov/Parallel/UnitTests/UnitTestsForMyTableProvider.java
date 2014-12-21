package ru.fizteh.fivt.students.egor_belikov.Parallel.UnitTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTableProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;

public class UnitTestsForMyTableProvider {
    private final Path testDirectory
            = Paths.get(System.getProperty("java.io.tmpdir"));
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

    @After
    public final void tearDown() throws Exception {

    }
}
