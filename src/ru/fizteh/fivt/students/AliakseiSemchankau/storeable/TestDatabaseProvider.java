package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TestDatabaseProvider {

    public DatabaseFactory dFactory;
    public DatabaseProvider dProvider;

    @Before
    public void initialization() {
        try {
            dFactory = new DatabaseFactory();
            dProvider = dFactory.create("C:\\JavaTests\\DatabaseForTests");
        } catch (Exception exc) {
            assertTrue(false);
        }
    }

    @Test
    public void testGetTable() throws Exception {
        try {
            dProvider.getTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertTrue(true);
        }

        assertNull(dProvider.getTable("tableIsntExist"));

        List<Class<?>> listToNewTable = new LinkedList<>();
        listToNewTable.add(Integer.class);
        listToNewTable.add(String.class);
        listToNewTable.add(Float.class);


        dProvider.createTable("newTable", listToNewTable);
        assertNotNull(dProvider.getTable("newTable"));

        dProvider.removeTable("newTable");
    }

    @Test
    public void testCreateTable() throws Exception {

        List<Class<?>> listToNewTable = new LinkedList<>();
        listToNewTable.add(Integer.class);
        listToNewTable.add(String.class);
        listToNewTable.add(Float.class);

        assertNotNull(dProvider.createTable("newTable", listToNewTable));

        try {
            dProvider.createTable(null, listToNewTable);
            assertTrue(false);
        } catch (Exception exc) {
            assertTrue(true);
        }

        try {
            dProvider.createTable("table", null);
            assertTrue(false);
        } catch (Exception exc) {
            assertTrue(true);
        }

        try {
            dProvider.createTable(null, null);
            assertTrue(false);
        } catch (Exception exc) {
            assertTrue(true);
        }

        dProvider.removeTable("newTable");

    }

    @Test
    public void testRemoveTable() throws Exception {

        List<Class<?>> listToNewTable = new LinkedList<>();
        listToNewTable.add(Integer.class);
        listToNewTable.add(String.class);
        listToNewTable.add(Float.class);

        dProvider.createTable("newTable", listToNewTable);

        try {
            dProvider.removeTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException iaexc) {
            assertTrue(true);
        }

        try {
            dProvider.removeTable("nonTable");
            assertTrue(false);
        } catch (IllegalStateException isexc) {
            assertTrue(true);
        }

        dProvider.removeTable("newTable");

    }

    @Test
    public void testDeserialize() throws Exception {
       // SerializeFunctions serializer = new SerializeFunctions();
        List<Class<?>> listToNewTable = new LinkedList<>();
        listToNewTable.add(Integer.class);
        listToNewTable.add(String.class);
        listToNewTable.add(Float.class);
        listToNewTable.add(Boolean.class);
        dProvider.createTable("newTable", listToNewTable);

        String jSON = "[6,  " + "\"" + "str" + "\"" + ", 3.6" + ",  true]";
       // System.out.println(jSON);
        Storeable store = dProvider.deserialize(dProvider.getTable("newTable"), jSON);
        assertNotNull(store);
        assertTrue(store.getIntAt(0).equals(6));
        assertTrue(store.getStringAt(1).equals("str"));
        assertTrue(store.getFloatAt(2) < 3.61 && store.getFloatAt(2) > 3.59);
        //System.out.println(store.getFloatAt(2));
        assertTrue(store.getBooleanAt(3).equals(true));
        assertFalse(store.getBooleanAt(3).equals(false));

        try {
            jSON = "[3, typoStroka, 3.6, false]";
            Storeable newStore = dProvider.deserialize(dProvider.getTable("newTable"), jSON);
            assertTrue(false);
        } catch (ParseException pexc) {
            assertTrue(true);
        }

        dProvider.removeTable("newTable");

    }

    @Test
    public void testSerialize() throws Exception {
       // SerializeFunctions serializer = new SerializeFunctions();
        List<Class<?>> listToNewTable = new LinkedList<>();
        listToNewTable.add(Integer.class);
        listToNewTable.add(String.class);
        listToNewTable.add(Float.class);
        listToNewTable.add(Boolean.class);
        dProvider.createTable("newTable", listToNewTable);

        String jSON = "[6, " + "\"" + "str" + "\"" + ", 3.6" + ", true]";
        // System.out.println(jSON);
        Storeable store = dProvider.deserialize(dProvider.getTable("newTable"), jSON);

        String newjSON = dProvider.serialize(dProvider.getTable("newTable"), store);

        assertEquals(jSON, newjSON);

        try {
            String unjSON = dProvider.serialize(null, store);
            assertTrue(false);
        } catch (ColumnFormatException cfexc) {
            assertTrue(true);
        }

        try {
            String unjSON = dProvider.serialize(dProvider.getTable("newTable"), null);
            assertTrue(false);
        } catch (ColumnFormatException cfexc) {
            assertTrue(true);
        }

        try {
            String unjSON = dProvider.serialize(null, null);
            assertTrue(false);
        } catch (ColumnFormatException cfexc) {
            assertTrue(true);
        }

        dProvider.removeTable("newTable");

    }

    @Test
    public void testCreateFor() throws Exception {

        List<Class<?>> listToNewTable = new LinkedList<>();
        listToNewTable.add(Integer.class);
        listToNewTable.add(String.class);
        listToNewTable.add(Double.class);
        listToNewTable.add(Boolean.class);

        Table dTable = dProvider.createTable("newTable", listToNewTable);
        Storeable store = dProvider.createFor(dTable);



        for (int i = 0; i < dTable.getColumnsCount(); ++i) {
            assertEquals(store.getColumnAt(i), null);
        }



        List<Object> listOfObjectForTable = new LinkedList<>();
        listOfObjectForTable.add((Integer) 6);
        listOfObjectForTable.add((String) "str");
        listOfObjectForTable.add((Double) 6.5);
        listOfObjectForTable.add((Boolean) false);



        Storeable newStore = dProvider.createFor(dTable, listOfObjectForTable);

        for (int i = 0; i < dTable.getColumnsCount(); ++i) {
            assertEquals(newStore.getColumnAt(i), listOfObjectForTable.get(i));
        }


        dProvider.removeTable("newTable");


    }

}