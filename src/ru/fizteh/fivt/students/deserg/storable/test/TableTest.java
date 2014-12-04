package ru.fizteh.fivt.students.deserg.storable.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.deserg.storable.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    List<Class<?>> signature = new LinkedList<>();


    @Before
    public void init() {

        dbPath = Paths.get("").resolve(System.getProperty("user.dir"));

        try {
            dbPath = Files.createTempDirectory(dbPath, "test");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        provider = (DbTableProvider) factory.create(dbPath.toString());

        signature.add(Integer.class);
        signature.add(String.class);
    }


    @Test
    public void testGetName() {


        try {
            String name = "ABC";
            DbTable table = (DbTable) provider.createTable(name, signature);
            assertEquals(name, table.getName());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

    }

    @Test
    public void testPut() {

        DbTable table;

        try {

            Storeable smth = new TableRow(signature);
            smth.setColumnAt(0, 140);
            smth.setColumnAt(1, "dzcx");
            table = (DbTable) provider.createTable("justTable", signature);
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
                table.put("someKey", null);
                assertTrue(false);
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }

            Storeable val1 = new TableRow(signature);
            val1.setColumnAt(0, 123);

            Storeable val2 = new TableRow(signature);
            val1.setColumnAt(0, 345);

            Storeable val3 = new TableRow(signature);
            val1.setColumnAt(0, 456);


            assertEquals(table.put("key1", val1), null);
            assertEquals(table.put("key2", val1), null);
            table.commit();

            table.remove("key1");
            assertEquals(table.put("key1", val2), null);

            assertEquals(table.put("key1", val3), val2);
            assertEquals(table.put("key1", val1), val3);

            assertEquals(table.put("key1", val2), val1);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }




    }



    @Test
    public void testGet() {

        try {
            DbTable table = (DbTable) provider.createTable("justTable", signature);

            try {
                table.get(null);
                assertTrue(false);
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }

            try {
                table.get("");
                assertTrue(false);
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }

            Storeable val1 = new TableRow(signature);
            val1.setColumnAt(0, 123);

            Storeable val2 = new TableRow(signature);
            val1.setColumnAt(0, 345);

            table.put("key1", val1);
            table.put("key2", val1);
            table.commit();

            table.put("key3", val1);
            assertEquals(table.get("key3"), val1);

            table.put("key2", val2);
            assertEquals(table.get("key2"), val2);

            assertEquals(table.get("key1"), val1);

            assertEquals(table.get("keyX"), null);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }


    @Test
    public void testRemove() {

        try {
            DbTable table = (DbTable) provider.createTable("justTable", signature);

            try {
                table.remove(null);
                assertTrue(false);
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }

            try {
                table.remove("");
                assertTrue(false);
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }

            Storeable val1 = new TableRow(signature);
            val1.setColumnAt(0, 123);

            Storeable val2 = new TableRow(signature);
            val1.setColumnAt(0, 345);

            table.put("key1", val1);
            table.put("key2", val1);
            table.commit();

            table.put("key3", val1);
            assertEquals(table.remove("key3"), val1);

            table.put("key2", val2);
            assertEquals(table.remove("key2"), val2);

            assertEquals(table.remove("key2"), null);

            assertEquals(table.remove("key1"), val1);

            assertEquals(table.remove("keyX"), null);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Test
    public void testList() {

        try {
            DbTable table = (DbTable) provider.createTable("justTable", signature);

            Storeable val1 = new TableRow(signature);
            val1.setColumnAt(0, 123);

            List<String> list = new LinkedList<>();
            list.add("key1");
            list.add("key2");
            list.add("key3");
            list.add("key4");
            table.put("key1", val1);
            table.put("key2", val1);
            table.put("key3", val1);
            table.put("key4", val1);

            assertEquals(list, table.list());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }



    @Test
    public void testCombined() {

        try {
            Map<String, Storeable> map = new HashMap<>();
            DbTable table = (DbTable) provider.createTable("table", signature);

            int size = 1000;
            for (int i = 0; i < size; i++) {
                String key = UUID.randomUUID().toString();
                Storeable value = new TableRow(signature);
                value.setColumnAt(1, UUID.randomUUID().toString());

                map.put(key, value);
                table.put(key, value);
                if (i % 100 == 0) {
                    table.commit();
                }
            }

            for (HashMap.Entry<String, Storeable> entry : map.entrySet()) {

                String key = entry.getKey();
                Storeable value = new TableRow(signature);
                value.setColumnAt(1, UUID.randomUUID().toString());
                map.put(key, value);
                table.put(key, value);

                Storeable val1 = map.get(key);
                Storeable val2 = table.get(key);
                assertEquals(val1, val2);

                if (key.hashCode() % 2 == 0) {
                    table.commit();
                }

            }


            int curSize = size;
            for (HashMap.Entry<String, Storeable> entry : map.entrySet()) {

                String key = entry.getKey();
                Storeable value = entry.getValue();
                Storeable expValue = table.get(key);

                assertNotNull(expValue);
                assertEquals(expValue, value);
                assertEquals(table.size(), curSize--);
                table.remove(key);

            }

            assertTrue(table.size() == 0);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    @After
    public void finish() {

        Shell.delete(dbPath);

    }

}
