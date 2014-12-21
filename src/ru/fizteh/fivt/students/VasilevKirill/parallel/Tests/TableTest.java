package ru.fizteh.fivt.students.VasilevKirill.parallel.Tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.parallel.structures.MyStorable;
import ru.fizteh.fivt.students.VasilevKirill.parallel.structures.MyTableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TableTest {
    private static TableProvider multiMap;
    private static String path;
    private static List<Class<?>> typeList;

    @BeforeClass
    public static void beforeClass() {
        try {
            //path = new File("").getCanonicalPath();
            path = Files.createTempDirectory("database").toString();
            multiMap = new MyTableProviderFactory().create(path);
            typeList = new ArrayList<>();
            typeList.add(Integer.class);
            typeList.add(String.class);
            multiMap.createTable("First", typeList);
            multiMap.createTable("Second", typeList);
            multiMap.createTable("Third", typeList);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            if (e.getMessage().equals("")) {
                System.err.println(e);
            } else {
                System.err.println(e.getMessage());
            }
        }
    }

    @AfterClass
    public static void afterClass() {
        try {
            multiMap.removeTable("First");
            multiMap.removeTable("Second");
            multiMap.removeTable("Third");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testPut() throws Exception {
        Table table = multiMap.getTable("First");
        try {
            table.put(null, null);
            assertFalse(true);
        } catch (IllegalArgumentException e) {
            assertFalse(false);
        }
        Storeable input = new MyStorable(typeList);
        input.setColumnAt(0, 1);
        input.setColumnAt(1, "one");
        table.put("key1", input);
        Storeable result = table.get("key1");
        table.remove("key1");
        assertEquals(result.getColumnAt(0), 1);
        assertEquals(result.getColumnAt(1), "one");
    }

    @Test
    public void testRemove() throws Exception {
        Table table = multiMap.getTable("First");
        try {
            table.remove(null);
            assertFalse(true);
        } catch (IllegalArgumentException e) {
            assertFalse(false);
        }
        Storeable input = new MyStorable(typeList);
        input.setColumnAt(0, 1);
        input.setColumnAt(1, "one");
        table.put("key1", input);
        table.remove("key1");
        assertNull(table.get("key1"));
        assertNull(table.remove("key2"));
    }

    @Test
    public void testSize() throws Exception {
        Table table = multiMap.getTable("First");
        assertEquals(0, table.size());
        Storeable input = new MyStorable(typeList);
        input.setColumnAt(0, 1);
        input.setColumnAt(1, "one");
        table.put("key1", input);
        assertEquals(1, table.size());
        table.remove("key1");
    }

    @Test
    public void testCommit() throws Exception {
        Table table = multiMap.getTable("First");
        Storeable input = new MyStorable(typeList);
        input.setColumnAt(0, 1);
        input.setColumnAt(1, "one");
        table.put("key1", input);
        input.setColumnAt(0, 2);
        input.setColumnAt(1, "two");
        table.put("key2", input);
        assertEquals(2, table.commit());
        table.remove("key1");
        table.remove("key2");
        assertEquals(2, table.commit());
    }

    @Test
    public void testRollback() throws Exception {
        Table table = multiMap.getTable("Second");
        Storeable input = new MyStorable(typeList);
        input.setColumnAt(0, 1);
        input.setColumnAt(1, "one");
        table.put("key1", input);
        input.setColumnAt(0, 2);
        input.setColumnAt(1, "two");
        table.put("key2", input);
        assertEquals(2, table.rollback());
    }

    @Test
    public void testGetColumnsCount() throws Exception {
        Table table = multiMap.getTable("First");
        assertEquals(2, table.getColumnsCount());
    }

    @Test
    public void testGetColumnType() throws Exception {
        Table table = multiMap.getTable("First");
        try {
            table.getColumnType(3);
            assertFalse(true);
        } catch (IndexOutOfBoundsException e) {
            assertFalse(false);
        }
        assertEquals(Integer.class, table.getColumnType(0));
    }
}

