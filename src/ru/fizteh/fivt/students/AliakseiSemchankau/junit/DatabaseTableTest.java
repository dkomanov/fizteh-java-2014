package ru.fizteh.fivt.students.AliakseiSemchankau.junit;


import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;

import java.util.*;

import static org.junit.Assert.*;
/**
 * Created by Aliaksei Semchankau on 12.11.2014.
 */
public class DatabaseTableTest {

    private static DatabaseFactory dFactory;
    private static DatabaseProvider dProvider;

    TemporaryFolder folder;
    String folderName;

    @Before
    public void initialize() {

        folder = new TemporaryFolder();
        folderName = folder.toString();
        dFactory = new DatabaseFactory();
        dProvider = dFactory.create(folderName);
        //dProvider = dFactory.create("C:\\JavaTests\\newTestingDatabase");

    }

    @Test
    public void testGet() {

        Table dTable = dProvider.createTable("table");

        for (int i = 0; i < 100; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i + 100);
            dTable.put(key, value);
        }

        for (int i = 0; i < 100; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i + 100);
            assertEquals(value, dTable.get(key));
        }

        for (int i = 0; i < 100; ++i) {
            String key = Integer.toString(i);
            String newValue = Integer.toString(i + 200);
            String oldValue = Integer.toString(i + 100);
            assertEquals(oldValue, dTable.put(key, newValue));
        }

        for (int i = 100; i < 200; ++i) {
            String key = Integer.toString(i);
            assertNull(dTable.get(key));
        }

        dProvider.removeTable("table");

    }

    @Test
    public void testPut() {

        Table dTable = dProvider.createTable("table");

        for (int i = 0; i < 100; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i + 100);
            assertNull(dTable.put(key, value));
        }

        for (int i = 0; i < 100; ++i) {
            String key = Integer.toString(i);
            String oldValue = Integer.toString(i + 100);
            String newValue = Integer.toString(i + 200);
            assertEquals(oldValue, dTable.put(key, newValue));
        }

        try {
            dTable.put(null, "correctValue");
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertTrue(true);
        }

        try {
            dTable.put("correctKey", null);
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertTrue(true);
        }

        dProvider.removeTable("table");

    }

    @Test
    public void testRemove() {

        Table dTable = dProvider.createTable("table");

        for (int i = 0; i < 100; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i + 100);
            dTable.put(key, value);
        }

        for (int i = 0; i < 100; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i + 100);
            assertEquals(dTable.remove(key), value);
        }

        try {
            dTable.remove(null);
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertTrue(true);
        }

        dProvider.removeTable("table");

    }

    @Test
    public void testSize() {

        Table dTable = dProvider.createTable("table");

        for (int i = 0; i < 100; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i + 100);
            dTable.put(key, value);
        }

        assertEquals(dTable.size(), 100);

        for (int i = 0; i < 50; ++i) {
            String key = Integer.toString(i);
            dTable.remove(key);
        }

        assertEquals(dTable.size(), 50);

        for (int i = 0; i < 100; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i + 100);
            dTable.put(key, value);
        }

        for (int i = 75; i < 200; ++i) {
            String key = Integer.toString(i);
            dTable.put(key, "randomString");
        }

        assertEquals(dTable.size(), 200);

        dTable.commit();

        for (int i = 150; i < 200; ++i) {
            String key = Integer.toString(i);
            dTable.remove(key);
        }

        assertEquals(dTable.size(), 150);

        dTable.rollback();

        assertEquals(dTable.size(), 200);

        dProvider.removeTable("table");

    }

    @Test
    public void testCommitRollBack() {

        Table dTable = dProvider.createTable("table");


        for (int i = 0; i < 1000; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i);
            dTable.put(key, value);
        }

        assertEquals(dTable.rollback(), 1000);

        for (int i = 0; i < 1000; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i);
            dTable.put(key, value);
        }

        assertEquals(dTable.commit(), 1000);

        for (int i = 1000; i < 2000; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i);
            dTable.put(key, value);
        }

        assertEquals(dTable.commit(), 1000);

        dProvider.removeTable("table");

    }

    @Test
    public void testList() {

        Table dTable = dProvider.createTable("table");

        ArrayList<String> listStart = new ArrayList<String>();

        for (int i = 0; i < 100; ++i) {
            String key = Integer.toString(i);
            String value = Integer.toString(i + 100);
            dTable.put(key, value);
            listStart.add(key);
        }

        ArrayList<String> listFinish = new ArrayList(dTable.list());

        Vector<String> startList = new Vector<String>();
        Vector<String> finishList = new Vector<String>();

        for (String s : listStart) {
            startList.add(s);
        }

        for (String s : listFinish) {
            finishList.add(s);
        }

        for (int i = 0; i < startList.size(); ++i) {
            for (int j = i + 1; j < startList.size(); ++j) {
                String s1 = startList.get(i);
                String s2 = startList.get(j);
                if (s1.compareTo(s2) <= 0) {
                    String tmp = s1;
                    startList.setElementAt(s2, i);
                    startList.setElementAt(tmp, j);
                }
            }
        }

        for (int i = 0; i < finishList.size(); ++i) {
            for (int j = i + 1; j < finishList.size(); ++j) {
                String s1 = finishList.get(i);
                String s2 = finishList.get(j);
                if (s1.compareTo(s2) <= 0) {
                    String tmp = s1;
                    finishList.setElementAt(s2, i);
                    finishList.setElementAt(tmp, j);
                }
            }
        }

        assertEquals(startList, finishList);

        dProvider.removeTable("table");

    }


}
