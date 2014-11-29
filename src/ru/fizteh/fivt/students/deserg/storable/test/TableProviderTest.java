package ru.fizteh.fivt.students.deserg.storable.test;

import org.junit.Test;
import ru.fizteh.fivt.students.deserg.storable.DbTableProvider;
import ru.fizteh.fivt.students.deserg.storable.DbTableProviderFactory;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by deserg on 29.11.14.
 */
public class TableProviderTest {

    DbTableProviderFactory factory = new DbTableProviderFactory();

    @Test
    public void testCreateTable() {

        DbTableProvider provider = (DbTableProvider) factory.create("db");

        try {
            provider.createTable(null);
            assertTrue(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        assertNotNull(provider.createTable("Moscow"));
        assertNull(provider.createTable("Moscow"));

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
            provider.removeTable("NonExistingTableName");
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

        String name = "Table1";
        provider.createTable(name);
        assertNotNull(provider.getTable(name));
    }

    @Test
    public void testCombined() {

        DbTableProvider provider = (DbTableProvider) factory.create("db");
        ArrayList<String> names = new ArrayList<>();
        int size = 100;
        for (int i = 0; i < size; i++) {
            String name = UUID.randomUUID().toString();
            provider.createTable(name);
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
