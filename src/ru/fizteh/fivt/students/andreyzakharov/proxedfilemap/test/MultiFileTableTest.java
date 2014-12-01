package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.test;

import org.junit.*;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MultiFileTableTest {
    static Path root = Paths.get("test/junit-parallel");
    static String[] dbNames = {"test-db", "null", "database-01", "*[::]<>|\\?"};
    static List<Class<?>> signature = Arrays.asList(String.class, Integer.class, Long.class,
            Float.class, Double.class, Byte.class, Boolean.class);

    static int n = 100;
    static String[] keys = new String[n];
    static Storeable[] values = new Storeable[n];
    static Storeable nonExistentValue;
    static Random random = new Random();

    MultiFileTable f;
    MultiFileTable g;

    static TableEntrySerializer serializer = new TableEntryJsonSerializer();

    static String maybeNull(String string) {
        return random.nextInt(5) == 0 ? "null" : string;
    }

    static String randomValue() {
        return "["
                + maybeNull("\"" + UUID.randomUUID().toString() + "\"") + ","
                + maybeNull(Integer.toString(random.nextInt())) + ","
                + maybeNull(Long.toString(random.nextLong())) + ","
                + maybeNull(Float.toString(random.nextFloat())) + ","
                + maybeNull(Double.toString(random.nextDouble())) + ","
                + maybeNull(Byte.toString((byte) random.nextInt(128))) + ","
                + maybeNull(Boolean.toString(random.nextBoolean())) + "]";
    }

    static String nonExistentValueString = "[\"string\",0,0,0,0,0,false]";

    void assertStoreableEquals(Storeable a, Storeable b, int n) {
        for (int i = 0; i < n; ++i) {
            assertEquals(a.getColumnAt(i), b.getColumnAt(i));
        }
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUp() throws Exception {
        TableProviderFactory factory = new MultiFileTableProviderFactory();
        TableProvider dummyProvider;
        Table dummyTable;

        dummyProvider = factory.create(root.toString());
        dummyTable = dummyProvider.createTable(dbNames[0], signature);

        for (int i = 0; i < n; ++i) {
            keys[i] = UUID.randomUUID().toString();
            values[i] = dummyProvider.deserialize(dummyTable, randomValue());
        }
        nonExistentValue = dummyProvider.deserialize(dummyTable, nonExistentValueString);
        dummyProvider.removeTable(dummyTable.getName());
    }

    @Before
    public void preRun() {
        try {
            f = new MultiFileTable(root.resolve(dbNames[0]), signature, serializer);
            g = new MultiFileTable(root.resolve(dbNames[0]), serializer);
        } catch (ConnectionInterruptException e) {
            //
        }
    }

    @After
    public void postRun() {
        for (String ignored : dbNames) {
            try {
                f.delete();
                g.delete();
            } catch (IllegalStateException | ConnectionInterruptException e) {
                //
            }
        }
    }

    @Test
    public void testGetName() throws Exception {
        for (String name : dbNames) {
            MultiFileTable f = new MultiFileTable(root.resolve(name), signature, serializer);
            assertEquals(name, f.getName());
            f.unload();
            f.delete();
        }
    }

    @Test
    public void testSizeEmpty() throws Exception {
        assertEquals(0, f.size());
        f.put("key", values[0]);
        f.remove("key");
        assertEquals(0, f.size());
    }

    @Test
    public void testSizeNonEmpty() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], values[i]);
            assertEquals(i + 1, f.size());
        }
        for (int i = n - 1; i >= 0; --i) {
            f.remove(keys[i]);
            assertEquals(i, f.size());
        }
    }

    @Test
    public void testIO() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], values[i]);
        }

        f.commit();
        g.load();

        assertEquals(new HashSet<>(f.list()), new HashSet<>(g.list()));
        for (String key : f.list()) {
            assertStoreableEquals(f.get(key), g.get(key), f.getColumnsCount());
        }
    }

    @Test
    public void testIOAppend() throws Exception {
        for (int i = 0; i < n / 2; ++i) {
            f.put(keys[i], values[i]);
        }
        f.commit();

        for (int i = n / 2; i < n; ++i) {
            f.put(keys[i], values[i]);
        }
        f.commit();
        g.load();

        assertEquals(new HashSet<>(f.list()), new HashSet<>(g.list()));
        for (String key : f.list()) {
            assertStoreableEquals(f.get(key), g.get(key), f.getColumnsCount());
        }
    }

    @Test
    public void testPutNullKey() throws Exception {
        exception.expect(IllegalArgumentException.class);
        f.put(null, values[0]);
    }

    @Test
    public void testPutNullValue() throws Exception {
        exception.expect(IllegalArgumentException.class);
        f.put("", null);
    }

    @Test
    public void testPut() throws Exception {
        for (int i = 0; i < n; ++i) {
            assertNull(f.put(keys[i], values[i]));
        }
        for (int i = 0; i < n; ++i) {
            assertStoreableEquals(values[i], f.put(keys[i], values[n - i - 1]), f.getColumnsCount());
        }
        for (int i = 0; i < n; ++i) {
            assertStoreableEquals(values[n - i - 1], f.put(keys[i], values[i]), f.getColumnsCount());
        }
    }

    @Test
    public void testGetNull() throws Exception {
        exception.expect(IllegalArgumentException.class);
        f.get(null);
    }

    @Test
    public void testGet() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], values[i]);
        }
        for (int i = 0; i < n; ++i) {
            assertStoreableEquals(values[i], f.get(keys[i]), f.getColumnsCount());
        }
    }

    @Test
    public void testRemoveNull() throws Exception {
        exception.expect(IllegalArgumentException.class);
        f.remove(null);
    }

    @Test
    public void testRemove() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], values[i]);
        }

        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
            assertNull(f.get(keys[i]));
        }
    }

    @Test
    public void testJuggle() throws Exception {
        for (int i = 0; i < n; ++i) {
            assertNull(f.put(keys[i], values[i]));
        }
        assertEquals(f.commit(), n);
        for (int i = 0; i < n; ++i) {
            assertStoreableEquals(values[i], f.put(keys[i], values[n - i - 1]), f.getColumnsCount());
        }
        for (int i = 0; i < n; ++i) {
            assertStoreableEquals(values[n - i - 1], f.get(keys[i]), f.getColumnsCount());
        }
        for (int i = 0; i < n; i += 2) {
            f.remove(keys[i]);
        }
        for (int i = 0; i < n; i += 2) {
            assertNull(f.put(keys[i], values[i]));
        }
    }

    @Test
    public void testCommit() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], values[i]);
        }
        assertEquals(n, f.getNumberOfUncommittedChanges());
        assertEquals(n, f.commit());
        for (int i = 0; i < n / 2; ++i) {
            f.put(keys[i], values[n - i - 1]);
        }
        assertEquals(n / 2, f.getNumberOfUncommittedChanges());
        assertEquals(n / 2, f.commit());
        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
        }
        assertEquals(n, f.getNumberOfUncommittedChanges());
        assertEquals(n, f.commit());

        for (int i = 0; i < n; ++i) {
            f.put(keys[i], values[i]);
        }
        assertEquals(n, f.getNumberOfUncommittedChanges());
        assertEquals(n, f.commit());
        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
            f.put(keys[i], values[i]);
            assertEquals(0, f.getNumberOfUncommittedChanges());
            assertEquals(0, f.commit());
        }
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], nonExistentValue);
            f.put(keys[i], values[i]);
            assertEquals(0, f.getNumberOfUncommittedChanges());
            assertEquals(0, f.commit());
        }
    }

    @Test
    public void testRollback() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], values[i]);
        }
        assertEquals(n, f.getNumberOfUncommittedChanges());
        assertEquals(n, f.rollback());
        for (int i = 0; i < n / 2; ++i) {
            f.put(keys[i], values[n - i - 1]);
        }
        assertEquals(n / 2, f.getNumberOfUncommittedChanges());
        assertEquals(n / 2, f.rollback());

        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
        }
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], values[i]);
        }
        f.commit();

        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
            f.put(keys[i], values[i]);
            assertEquals(0, f.getNumberOfUncommittedChanges());
            assertEquals(0, f.rollback());
        }
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], nonExistentValue);
            f.put(keys[i], values[i]);
            assertEquals(0, f.getNumberOfUncommittedChanges());
            assertEquals(0, f.rollback());
        }
    }

    @Test
    public void testList() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], values[i]);
        }
        assertEquals(new HashSet<>(Arrays.asList(keys)), new HashSet<>(f.list()));
    }
}
