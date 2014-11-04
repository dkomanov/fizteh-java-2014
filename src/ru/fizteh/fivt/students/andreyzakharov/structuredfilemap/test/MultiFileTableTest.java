package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MultiFileTableTest {
    static Path root = Paths.get("/home/norrius/Apps/fizteh-java-2014/test/junit");
    static String[] dbNames = {"test-db", "null", "database-01", "*[::]<>|\\?"};

    static int n = 10000;
    static String[] keys = new String[n];
    static String[] strs = new String[n];

    static Storeable[] vals = new Storeable[n];
    static String[] keys3 = {"key", "ключ", "", "\\", "key\twith\nspaces"};
    static String[] strs3 = {"value", "значение", "", "/", "qwertyuiop"};
    static int n3 = keys3.length;
    static Storeable[] vals3 = new Storeable[n3];

    static String[] keys2 = {"dog", "db_ТЕСт", "179"};
    static String[] strs2 = {"teleporter", "operation redstone", "value"};
    static int n2 = keys2.length;
    static Storeable[] vals2 = new Storeable[n2];

    static Storeable nonExistent;

    MultiFileTable f;
    MultiFileTable g;

    static TableEntrySerializer serializer = new TableEntryJsonSerializer();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUp() {
        TableProviderFactory dummyFactory = new MultiFileTableProviderFactory();
        TableProvider dummyProvider;
        Table dummyTable;
        try {
            dummyProvider = dummyFactory.create(root.toString());
            dummyTable = dummyProvider.createTable(dbNames[0], null);
        } catch (IOException e) {
            return;
        }
        for (int i = 0; i < n; ++i) {
            keys[i] = UUID.randomUUID().toString();
            strs[i] = UUID.randomUUID().toString();
            try {
                vals[i] = dummyProvider.deserialize(dummyTable, strs[i]);
            } catch (ParseException e) {
                return;
            }
        }
        for (int i = 0; i < n2; i++) {
            try {
                vals2[i] = dummyProvider.deserialize(dummyTable, strs2[i]);
            } catch (ParseException e) {
                return;
            }
        }
        for (int i = 0; i < n3; i++) {
            try {
                vals3[i] = dummyProvider.deserialize(dummyTable, strs3[i]);
            } catch (ParseException e) {
                return;
            }
        }
        try {
            nonExistent = dummyProvider.deserialize(dummyTable, "something-probably-non-existent");
        } catch (ParseException e) {
            return;
        }
    }

    @Before
    public void preRun() {
        try {
            f = new MultiFileTable(root.resolve(dbNames[0]), serializer);
            g = new MultiFileTable(root.resolve(dbNames[0]), serializer);
        } catch (ConnectionInterruptException e) {
            //
        }
    }

    @Test
    public void testGetName() throws Exception {
        for (String name : dbNames) {
            MultiFileTable f = new MultiFileTable(root.resolve(name), serializer);
            assertEquals(name, f.getName());
            f.unload();
        }
    }

    @Test
    public void testSizeEmpty() throws Exception {
        assertEquals(0, f.size());
        f.put("key", vals[0]);
        f.remove("key");
        assertEquals(0, f.size());
    }

    @Test
    public void testSizeNonEmpty() throws Exception {
        for (int i = 0; i < n3; ++i) {
            f.put(keys3[i], vals3[i]);
            assertEquals(i + 1, f.size());
        }
        for (int i = n3 - 1; i >= 0; --i) {
            f.remove(keys3[i]);
            assertEquals(i, f.size());
        }
    }

    @Test
    public void testIO() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }

        f.commit();
        g.load();

        assertEquals(new HashSet<>(f.list()), new HashSet<>(g.list()));
        for (String key : f.list()) {
            assertEquals(f.get(key), g.get(key));
        }
    }

    @Test
    public void testIOAppend() throws Exception {
        for (int i = 0; i < n / 2; ++i) {
            f.put(keys[i], vals[i]);
        }
        f.commit();

        for (int i = n / 2; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }
        f.commit();
        g.load();

        assertEquals(new HashSet<>(f.list()), new HashSet<>(g.list()));
        for (String key : f.list()) {
            assertEquals(f.get(key), g.get(key));
        }
    }

    @Test
    public void testPutNullKey() throws Exception {
        exception.expect(IllegalArgumentException.class);
        f.put(null, vals[0]);
    }

    @Test
    public void testPutNullValue() throws Exception {
        exception.expect(IllegalArgumentException.class);
        f.put("", null);
    }

    @Test
    public void testPut() throws Exception {
        for (int i = 0; i < n3; ++i) {
            assertNull(f.put(keys3[i], vals3[i]));
        }
        for (int i = 0; i < n3; ++i) {
            assertEquals(vals3[i], f.put(keys3[i], vals3[n3 - i - 1]));
        }
        for (int i = 0; i < n3; ++i) {
            assertEquals(vals3[n3 - i - 1], f.put(keys3[i], vals3[i]));
        }
        for (int i = 0; i < n2; ++i) {
            assertNull(f.put(keys2[i], vals2[i]));
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
            f.put(keys[i], vals[i]);
        }
        for (int i = 0; i < n; ++i) {
            assertEquals(vals[i], f.get(keys[i]));
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
            f.put(keys[i], vals[i]);
        }

        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
            assertNull(f.get(keys[i]));
        }
    }

    @Test
    public void testJuggle() throws Exception {
        for (int i = 0; i < n; ++i) {
            assertNull(f.put(keys[i], vals[i]));
        }
        assertEquals(f.commit(), n);
        for (int i = 0; i < n; ++i) {
            assertEquals(vals[i], f.put(keys[i], vals[n - i - 1]));
        }
        for (int i = 0; i < n; ++i) {
            assertEquals(vals[n - i - 1], f.get(keys[i]));
        }
        for (int i = 0; i < n; i += 2) {
            f.remove(keys[i]);
        }
        for (int i = 0; i < n; i += 2) {
            assertNull(f.put(keys[i], vals[i]));
        }
    }

    @Test
    public void testCommit() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }
        assertEquals(n, f.getPending());
        assertEquals(n, f.commit());
        for (int i = 0; i < n / 2; ++i) {
            f.put(keys[i], vals[n - i - 1]);
        }
        assertEquals(n / 2, f.getPending());
        assertEquals(n / 2, f.commit());
        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
        }
        assertEquals(n, f.getPending());
        assertEquals(n, f.commit());

        for (int i = 0; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }
        assertEquals(n, f.getPending());
        assertEquals(n, f.commit());
        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
            f.put(keys[i], vals[i]);
            assertEquals(0, f.getPending());
            assertEquals(0, f.commit());
        }
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], nonExistent);
            f.put(keys[i], vals[i]);
            assertEquals(0, f.getPending());
            assertEquals(0, f.commit());
        }
    }

    @Test
    public void testRollback() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }
        assertEquals(n, f.getPending());
        assertEquals(n, f.rollback());
        for (int i = 0; i < n / 2; ++i) {
            f.put(keys[i], vals[n - i - 1]);
        }
        assertEquals(n / 2, f.getPending());
        assertEquals(n / 2, f.rollback());

        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
        }
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }
        f.commit();

        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
            f.put(keys[i], vals[i]);
            assertEquals(0, f.getPending());
            assertEquals(0, f.rollback());
        }
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], nonExistent);
            f.put(keys[i], vals[i]);
            assertEquals(0, f.getPending());
            assertEquals(0, f.rollback());
        }
    }

    @Test
    public void testList() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }
        assertEquals(new HashSet<>(Arrays.asList(keys)), new HashSet<>(f.list()));
    }
}
