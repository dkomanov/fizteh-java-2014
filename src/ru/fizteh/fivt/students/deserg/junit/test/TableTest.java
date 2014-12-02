package ru.fizteh.fivt.students.deserg.junit.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.deserg.junit.DbTable;
import ru.fizteh.fivt.students.deserg.junit.DbTableProvider;
import ru.fizteh.fivt.students.deserg.junit.DbTableProviderFactory;
import ru.fizteh.fivt.students.deserg.junit.Shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by deserg on 29.11.14.
 */
public class TableTest {

    Path dbPath;
    DbTableProviderFactory factory = new DbTableProviderFactory();
    DbTableProvider provider;

    @Before
    public void init() {

        dbPath = Paths.get("").resolve(System.getProperty("user.dir"));

        try {
            dbPath = Files.createTempDirectory(dbPath, "test");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        provider = (DbTableProvider) factory.create(dbPath.toString());

    }


    @Test
    public void testGetName() {


        try {
            String name = "ABC";
            DbTable table = (DbTable) provider.createTable(name);
            assertEquals(name, table.getName());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

    }

    @Test
    public void testPut() {

        DbTable table = (DbTable) provider.createTable("justTable");

        String smth = "smth";

        try {
            table.put(null, smth);
            assertTrue(false);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            table.put("", smth);
            assertTrue(false);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            table.put(smth, null);
            assertTrue(false);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }


        assertEquals(table.put("key1", "val1"), null);
        assertEquals(table.put("key2", "val2"), null);
        table.commit();

        table.remove("key1");
        assertEquals(table.put("key1", "val1_"), null);

        assertEquals(table.put("key1", "val1__"), "val1_");
        assertEquals(table.put("key1", "val1"), "val1__");

        assertEquals(table.put("key1", "val1_"), "val1");

    }

    @Test
    public void testCombined() {

        Map<String, String> map = new HashMap<>();
        DbTable table = (DbTable) provider.createTable("table");

        int size = 1000;
        for (int i = 0; i < size; i++) {
            String key = UUID.randomUUID().toString();
            String value = UUID.randomUUID().toString();
            map.put(key, value);
            table.put(key, value);
            if (i % 100 == 0) {
                table.commit();
            }
        }

        for (HashMap.Entry<String, String> entry: map.entrySet()) {

            String key = entry.getKey();
            String value = UUID.randomUUID().toString();
            map.put(key, value);
            table.put(key, value);

            String val1 = map.get(key);
            String val2 = table.get(key);
            assertEquals(val1, val2);

            if (key.hashCode() % 2 == 0) {
                table.commit();
            }

        }


        int curSize = size;
        for (HashMap.Entry<String, String> entry: map.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();
            String expValue = table.get(key);

            assertNotNull(expValue);
            assertEquals(expValue, value);
            assertEquals(table.size(), curSize--);
            table.remove(key);

        }

        assertTrue(table.size() == 0);

    }


    @Test
    public void testCommitRollback() {

        DbTable table = (DbTable) provider.createTable("table");
        table.put("key1", "val1");
        table.put("key2", "val2");

        table.commit();
        table.rollback();
        assertEquals(table.size(), 2);

        table.put("key3", "val3");
        table.put("key4", "val4");

        assertEquals(table.put("key1", "newVal1"), "val1");
        assertEquals(table.put("key2", "newVal2"), "val2");

        table.rollback();
        assertEquals(table.size(), 2);

    }

    @After
    public void finish() {

        Shell.delete(dbPath);

    }

}
