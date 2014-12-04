package ru.fizteh.fivt.students.deserg.storable.test;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.deserg.storable.DbTableProvider;
import ru.fizteh.fivt.students.deserg.storable.DbTableProviderFactory;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by deserg on 29.11.14.
 */
public class TableProviderTest {

    DbTableProviderFactory factory = new DbTableProviderFactory();
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

}
