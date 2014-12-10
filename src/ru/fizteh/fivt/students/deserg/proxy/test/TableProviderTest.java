package ru.fizteh.fivt.students.deserg.proxy.test;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.deserg.proxy.DbTable;
import ru.fizteh.fivt.students.deserg.proxy.DbTableProvider;
import ru.fizteh.fivt.students.deserg.proxy.DbTableProviderFactory;
import ru.fizteh.fivt.students.deserg.proxy.TableRow;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by deserg on 29.11.14.
 */
public class TableProviderTest {

    DbTableProviderFactory factory = new DbTableProviderFactory();
    DbTableProvider provider = (DbTableProvider) factory.create("db");
    List<Class<?>> signature = new LinkedList<>();

    @Before
    public void init() {
        signature.add(Integer.class);
        signature.add(String.class);
    }

    @Test
    public void testCreateTable() {

        DbTableProvider provider = (DbTableProvider) factory.create("db");

        List<Class<?>> otherSignature = new LinkedList<>();
        otherSignature.add(Integer.class);

        try {
            provider.createTable(null, otherSignature);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.createTable("", otherSignature);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.createTable("name", null);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        try {
            provider.createTable("name\000", otherSignature);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            assertNotNull(provider.createTable("Moscow", otherSignature));
            assertNull(provider.createTable("Moscow", otherSignature));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        otherSignature.add(HashMap.class);
        try {
            provider.createTable("name", otherSignature);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.close();
            provider.createTable("tableName", signature);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Test
    public void testRemoveTable() {

        DbTableProvider provider = (DbTableProvider) factory.create("db");

        try {
            provider.removeTable(null);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.removeTable("");
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.removeTable("NonExistingTableName");
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.removeTable("Nam\000e");
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.close();
            provider.removeTable("tableName");
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    @Test
    public void testGetTable() {

        DbTableProvider provider = (DbTableProvider) factory.create("db");

        try {
            provider.getTable(null);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.getTable("");
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.getTable("name\000");
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        String name = "Table1";
        try {
            provider.createTable(name, signature);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        assertNotNull(provider.getTable(name));
        provider.removeTable(name);

        try {
            provider.getTable(name);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.close();
            provider.getTable("tableName");
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void testCombined() {

        DbTableProvider provider = (DbTableProvider) factory.create("db");
        ArrayList<String> names = new ArrayList<>();

        int size = 100;
        for (int i = 0; i < size; i++) {
            String name = UUID.randomUUID().toString();
            try {
                provider.createTable(name, signature);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            names.add(name);
        }

        for (String name: names) {

            try {
                assertNotNull(provider.getTable(name));
                provider.removeTable(name);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                assertTrue(false);
            }
        }

    }

    @Test
    public void testCreateFor() throws Exception{

        DbTable table = (DbTable) provider.createTable("table", signature);
        Storeable row = provider.createFor(table);
        assertNotNull(row);

        int val1 = 103;
        String val2 = "123";
        List<Object> list = new LinkedList<>();
        list.add(val1);
        list.add(val2);
        assertNotNull(provider.createFor(table, list));

        list.add("123");
        try {
            provider.createFor(table, list);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.close();
            provider.createFor(table);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            provider.close();
            provider.createFor(table, list);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Test
    public void testGetTableNames() {

        DbTableProvider provider = (DbTableProvider) factory.create("provider");
        List<String> names = new LinkedList<>();
        names.add("name1");
        names.add("name2");
        names.add("name3");

        try {
            provider.createTable("name1", signature);
            provider.createTable("name2", signature);
            provider.createTable("name3", signature);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        names.sort(Comparator.<String>naturalOrder());
        List<String> expNames = provider.getTableNames();
        expNames.sort(Comparator.<String>naturalOrder());
        assertEquals(names, expNames);

        try {
            provider.close();
            provider.getTableNames();
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Test
    public void testSerialize() {

        DbTableProvider provider = (DbTableProvider) factory.create("provider");

        try {
            DbTable table = (DbTable) provider.createTable("table", signature);


            Storeable val = new TableRow(signature);
            val.setColumnAt(0, 100500);
            val.setColumnAt(1, "serialize");
            assertEquals(provider.serialize(table, val), "[100500,\"serialize\"]");

            val.setColumnAt(0, null);
            assertEquals(provider.serialize(table, val), "[null,\"serialize\"]");

            try {
                provider.close();
                provider.serialize(table, val);
                assertTrue(false);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Test
    public void testDeserialize() {

        DbTableProvider provider = (DbTableProvider) factory.create("provider");

        try {
            DbTable table = (DbTable) provider.createTable("table", signature);

            try {
                provider.deserialize(table, "[0.12, \"af\"]");
                assertTrue(false);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            }

            try {
                provider.deserialize(table, "[]");
                assertTrue(false);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            }

            try {
                provider.deserialize(table, "[,\"ds\"]");
                assertTrue(false);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            }


            try {
                provider.deserialize(table, "[124, \"123\", 12.3]");
                assertTrue(false);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            }

            String pattern = "[1234554321,\"qwertyu\"]";
            Object[] objects = {1234554321, "qwertyu"};

            try {
                Storeable value = provider.deserialize(table, pattern);
                for (int i = 0; i < signature.size(); i++) {
                    assertEquals(objects[i], value.getColumnAt(i));
                }
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            }

            try {
                provider.close();
                provider.deserialize(table, pattern);
                assertTrue(false);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }



}
