package ru.fizteh.fivt.students.andreyzakharov.stringfilemap.test;

import org.junit.Before;
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
    Path root = Paths.get("/home/norrius/Apps/fizteh-java-2014/test/junit");

    int n = 1000;
    String[] keys = new String[n];
    String[] vals = new String[n];

    String[] dbNames = {"test-db", "null", "database-01", "*[::]<>|\\?"};
    String[] keys3 = {"key", "ключ", "", "\\", "key\twith\nspaces"};
    String[] vals3 = {"value", "значение", "", "/", "qwertyuiop"};
    int n3 = keys3.length;

    String[] keys2 = {"dog", "db_ТЕСт", "179"};
    String[] vals2 = {"teleporter", "operation redstone", "value"};
    int n2 = keys2.length;

    @Before
    public void setUp() {
        for (int i = 0; i < n; ++i) {
            keys[i] = UUID.randomUUID().toString();
            vals[i] = UUID.randomUUID().toString();
        }
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
        FileMap f = new FileMap(root.resolve(dbNames[0]));

        assertEquals(0, f.size());
        f.put("key", "value");
        f.remove("key");
        assertEquals(0, f.size());
    }

    @Test
    public void testSizeNonEmpty() throws Exception {
        FileMap f = new FileMap(root.resolve(dbNames[0]));

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
        FileMap f = new FileMap(root.resolve(dbNames[2]));
        FileMap g = new FileMap(root.resolve(dbNames[2]));

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
    public void testPutNullKey() throws Exception {
        FileMap f = new FileMap(root.resolve(dbNames[0]));

        exception.expect(IllegalArgumentException.class);
        f.put(null, "");
    }

    @Test
    public void testPutNullValue() throws Exception {
        FileMap f = new FileMap(root.resolve(dbNames[0]));

        exception.expect(IllegalArgumentException.class);
        f.put("", null);
    }

    @Test
    public void testPut() throws Exception {
        FileMap f = new FileMap(root.resolve(dbNames[0]));

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
        FileMap f = new FileMap(root.resolve(dbNames[0]));

        exception.expect(IllegalArgumentException.class);
        f.get(null);
    }

    @Test
    public void testGet() throws Exception {
        FileMap f = new FileMap(root.resolve(dbNames[0]));

        for (int i = 0; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }
        for (int i = 0; i < n; ++i) {
            assertEquals(vals[i], f.get(keys[i]));
        }
    }

    @Test
    public void testRemoveNull() throws Exception {
        FileMap f = new FileMap(root.resolve(dbNames[0]));

        exception.expect(IllegalArgumentException.class);
        f.remove(null);
    }

    @Test
    public void testRemove() throws Exception {
        FileMap f = new FileMap(root.resolve(dbNames[0]));

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
        FileMap f = new FileMap(root.resolve(dbNames[0]));

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
        FileMap f = new FileMap(root.resolve(dbNames[0]));

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
        FileMap f = new FileMap(root.resolve(dbNames[0]));

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
        FileMap f = new FileMap(root.resolve(dbNames[0]));

        for (int i = 0; i < n; ++i) {
            f.put(keys[i], vals[i]);
        }
        assertEquals(new HashSet<>(Arrays.asList(keys)), new HashSet<>(f.list()));
    }
}
