package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TestDatabaseTable {

    public static DatabaseProvider dProvider;
    public static Table dTable;
    SerializeFunctions serializer;
    MakeRandom randomizer;
    List<Class<?>> signature;

    @Before
    public void init() {
        dProvider = new DatabaseProvider("C:\\JavaTests\\DatabaseForTests");
        signature = new LinkedList<>();
        signature.add(Integer.class);
        signature.add(String.class);
        signature.add(Long.class);
        signature.add(Float.class);
        signature.add(Boolean.class);
        signature.add(Byte.class);
        signature.add(Double.class);
        try {
            dTable = dProvider.createTable("newTable", signature);
        } catch (IOException ioexc) {
            assertTrue(false);
        }
        serializer = new SerializeFunctions();
        randomizer = new MakeRandom();
    }

   /* @Test
    public void testGetName() throws Exception {
        assertEquals(dTable.getName(), "newTable");
    }*/

    @Test
    public void testGetPutRemove() throws Exception {

        List<Storeable> storeList = new LinkedList<>();
        for (int i = 0; i < 100; ++i) {
            System.out.println();
            DatabaseStoreable curStore = new DatabaseStoreable(randomizer.takeRandomValue(signature));
            dTable.put(Integer.toString(i), curStore);
            storeList.add(curStore);
        }

        for (int i = 0; i < 100; ++i) {
            assertEquals(storeList.get(i), dTable.get(Integer.toString(i)));
        }

        for (int i = 0; i < 100; ++i) {
            assertEquals(storeList.get(i), dTable.remove(Integer.toString(i)));
        }

        try {
            dTable.get(null);
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertTrue(true);
        }

        assertNull(dTable.get("nonkey"));

        try {
            dTable.remove(null);
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertTrue(true);
        }

       //dProvider.removeTable("newTable");

    }

     @Test
    public void testSize() throws Exception {
         for (int i = 0; i < 100; ++i) {
             dTable.put(Integer.toString(i), new DatabaseStoreable(randomizer.takeRandomValue(signature)));
         }
         assertEquals(dTable.size(), 100);

         for (int i = 0; i < 50; ++i) {
             dTable.remove(Integer.toString(i));
             assertEquals(dTable.size(), 99 - i);
         }

    }

    @Test
    public void testCommit() throws Exception {

    }

    @Test
    public void testRollback() throws Exception {

    }

    @Test
    public void testGetColumnsCount() throws Exception {

    }

    @Test
    public void testGetColumnType() throws Exception {

    }

   @After
    public void clearing() {
        dProvider.removeTable("newTable");
    }
}