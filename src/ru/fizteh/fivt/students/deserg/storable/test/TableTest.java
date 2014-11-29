package ru.fizteh.fivt.students.deserg.storable.test;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.deserg.storable.DbTable;
import ru.fizteh.fivt.students.deserg.storable.DbTableProvider;

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

    String dbName = "database";
    DbTableProvider provider;

    @Before
    public void init() {

        String dbName = "database";
        Path path = Paths.get("").resolve(dbName);
        provider = new DbTableProvider(path);

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
    public void testPutGetRemove() {

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
        table.put("123", "kokok");
        table.put("456", "lololo");

        table.commit();
        table.rollback();
        assertEquals(table.size(), 2);

        table.put("789", "ppopo");
        table.put("MONDAY", "9:00");
        table.put("123", "uzuzu");
        table.put("456", "qwqrq");
        table.rollback();
        assertEquals(table.size(), 2);

    }

}
