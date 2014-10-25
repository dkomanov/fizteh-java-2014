package ru.fizteh.fivt.students.andreyzakharov.stringfilemap.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.students.andreyzakharov.stringfilemap.FileMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FileMapTest {
    static Path root = Paths.get("/home/norrius/Apps/fizteh-java-2014/test/junit");

    static int n = 10000;
    static String[] keys = new String[n];
    static String[] vals = new String[n];

    static String[] dbNames = {"test-db", "null", "database-01", "*[::]<>|\\?"};
    static String[] keys3 = {"key", "ключ", "", "\\", "key\twith\nspaces"};
    static String[] vals3 = {"value", "значение", "", "/", "qwertyuiop"};
    static int n3 = keys3.length;

    static String[] keys2 = {"dog", "db_ТЕСт", "179"};
    static String[] vals2 = {"teleporter", "operation redstone", "value"};
    static int n2 = keys2.length;

    FileMap f;
    FileMap g;

    @BeforeClass
    public static void setUp() {
        for (int i = 0; i < n; ++i) {
            keys[i] = UUID.randomUUID().toString();
            vals[i] = UUID.randomUUID().toString();
        }
    }

    @Before
    public void preRun() {
        f = new FileMap(root.resolve(dbNames[0]));
        g = new FileMap(root.resolve(dbNames[0]));
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetName() throws Exception {
        for (String name : dbNames) {
            FileMap f = new FileMap(root.resolve(name));
            assertEquals(name, f.getName());
            f.unload();
        }
    }

    @Test
    public void testSizeEmpty() throws Exception {
        assertEquals(0, f.size());
        f.put("key", "value");
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
        f.put(null, "");
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
        assertEquals(n, f.pending());
        assertEquals(n, f.commit());
        for (int i = 0; i < n / 2; ++i) {
            f.put(keys[i], vals[n - i - 1]);
        }
        assertEquals(n / 2, f.pending());
        assertEquals(n / 2, f.commit());
        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
        }
        assertEquals(n, f.pending());
        assertEquals(n, f.commit());

        for (int i = 0; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }
        assertEquals(n, f.pending());
        assertEquals(n, f.commit());
        for (int i = 0; i < n; ++i) {
            f.remove(keys[i]);
            f.put(keys[i], vals[i]);
            assertEquals(0, f.pending());
            assertEquals(0, f.commit());
        }
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], "something-probably-non-existent");
            f.put(keys[i], vals[i]);
            assertEquals(0, f.pending());
            assertEquals(0, f.commit());
        }
    }

    @Test
    public void testRollback() throws Exception {
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }
        assertEquals(n, f.pending());
        assertEquals(n, f.rollback());
        for (int i = 0; i < n / 2; ++i) {
            f.put(keys[i], vals[n - i - 1]);
        }
        assertEquals(n / 2, f.pending());
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
            assertEquals(0, f.pending());
            assertEquals(0, f.rollback());
        }
        for (int i = 0; i < n; ++i) {
            f.put(keys[i], "something-probably-non-existent");
            f.put(keys[i], vals[i]);
            assertEquals(0, f.pending());
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
