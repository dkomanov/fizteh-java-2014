package ru.fizteh.fivt.students.lukina.Tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.lukina.DataBase.DBase;
import ru.fizteh.fivt.students.lukina.DataBase.DBaseProviderFactory;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DBaseTest {
    @Rule
    public TemporaryFolder root = new TemporaryFolder();
    DBase test;
    TableProvider prov;
    DBaseProviderFactory fact;

    @Before
    public void initializeAndFillingBase() {
        fact = new DBaseProviderFactory();
        try {
            prov = fact.create(root.newFolder().toString());
        } catch (IllegalArgumentException | IOException e1) {
            // not OK
        }
        ArrayList<Class<?>> list = new ArrayList<>();
        list.add(String.class);
        try {
            test = (DBase) prov.createTable("testTable", list);
        } catch (IOException e) {
            // not OK
        }
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "2");
        test.put("1", row);
        test.put("3", row);
        row.setColumnAt(0, "aasd2");
        test.put("la", row);
        row.setColumnAt(0, "значение с пробелами");
        test.put("z", row);
        row.setColumnAt(0, "l");
        test.put("we", row);
        row.setColumnAt(0, "значение_на_русском");
        test.put("ключ_на_русском", row);
    }

    @Test
    public void testGetName() {
        String name = test.getName();
        if (name == null) {
            fail("Null name");
        }
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() {
        Storeable tmp1 = test.get(null);
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testGetSpaces() {
        Storeable tmp2 = test.get(" ");
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testGetEmpty() {
        Storeable tmp3 = test.get("");
    }

    @Test
    public void testGetNotExistingKey() {
        assertNull("Get not_existing_key: Expected null",
                test.get("not_existing_key"));
    }

    @Test
    public void testGet() {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "2");
        test.put("1", row);
        assertSame(test.get("1").getStringAt(0), "2");
        test.put("3", row);
        assertSame(test.get("3").getStringAt(0), "2");
        row.setColumnAt(0, "aasd2");
        test.put("la", row);
        assertSame(test.get("la").getStringAt(0), "aasd2");
        row.setColumnAt(0, "значение с пробелами");
        test.put("z", row);
        assertSame(test.get("z").getStringAt(0), "значение с пробелами");
        row.setColumnAt(0, "l");
        test.put("we", row);
        assertSame(test.get("we").getStringAt(0), "l");
        row.setColumnAt(0, "значение_на_русском");
        test.put("ключ_на_русском", row);
        assertSame(test.get("ключ_на_русском").getStringAt(0),
                "значение_на_русском");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "value");
        test.put(null, row);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() {
        test.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "value");
        test.put("", row);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutKeyWithSpaces() {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "value");
        test.put(" ", row);
    }

    @Test
    public void testPut() {
        Storeable row1 = prov.createFor(test);
        row1.setColumnAt(0, "val1");
        assertNull(test.put("key", row1));
        Storeable row2 = prov.createFor(test);
        row2.setColumnAt(0, "val2");
        assertSame(test.put("key", row2), row1);
        row1.setColumnAt(0, "Значение на русском");
        assertSame(test.put("key", row1), row2);
        row2.setColumnAt(0, "val3");
        assertSame(test.put("key", row2), row1);
        assertSame(test.get("key"), row2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        test.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmpty() {
        test.remove("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveSpaces() {
        test.remove(" ");
    }

    @Test
    public void testRemove() {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "tmpValue");
        test.put("tmpKey", row);
        test.remove("tmpKey");
        assertNull(test.get("tmpKey"));
    }

    @Test
    public void testSize() throws IOException {
        int firstSize = test.size();
        assertTrue(firstSize >= 0);
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "2");
        test.put("1111", row);
        assertSame(test.size() - firstSize, 1);
        row.setColumnAt(0, "3");
        test.put("1111", row);
        assertSame(test.size() - firstSize, 1);
        test.remove("1111");
        assertSame(test.size() - firstSize, 0);
        row.setColumnAt(0, "2");
        test.put("1111", row);
        test.commit();
        assertSame(test.size() - firstSize, 1);
        test.put("2222", row);
        test.rollback();
        assertSame(test.size() - firstSize, 1);
    }

    @Test
    public void testCommit() throws IOException {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "v0");
        test.put("kk1", row);
        test.commit();
        row.setColumnAt(0, "v1");
        test.put("kk1", row);
        row.setColumnAt(0, "v2");
        test.put("kk1", row);
        assertSame(test.get("kk1").getStringAt(0), "v2");
        test.remove("kk1");
        assertSame(test.commit(), 1);
    }

    @Test
    public void testCountChanges() throws IOException {
        test.commit();
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "v1");
        test.put("z1", row);
        row.setColumnAt(0, "v2");
        test.put("z1", row);
        assertSame(test.getNumberOfUncommittedChanges(), 1);
        row.setColumnAt(0, "v1");
        test.put("z2", row);
        test.remove("z2");
        assertSame(test.getNumberOfUncommittedChanges(), 1);
        test.commit();
        assertSame(test.getNumberOfUncommittedChanges(), 0);
        row.setColumnAt(0, "v3");
        test.put("z1", row);
        test.remove("z1");
        assertSame(test.getNumberOfUncommittedChanges(), 1);
    }

    @Test
    public void testRollback() throws IOException {
        test.commit();
        assertSame(test.rollback(), 0);
        int startSize = test.size();
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "v1");
        test.put("r1", row);
        test.put("r2", row);
        assertSame(test.rollback(), 2);
        assertNull(test.get("r1"));
        assertNull(test.get("r2"));
        assertSame(startSize, test.size());
        test.put("r1", row);
        test.put("r2", row);
        assertSame(startSize + 2, test.size());
        test.commit();
        assertSame(test.rollback(), 0);
        assertSame(test.get("r1").getStringAt(0), "v1");
        assertSame(test.get("r2").getStringAt(0), "v1");
        assertSame(startSize + 2, test.size());
    }

    @Test
    public void testList() throws IOException {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "v1");
        test.put("r3", row);
        assertSame(test.list().contains("r3"), true);
        test.commit();
        assertSame(test.list().contains("r3"), true);
        test.put("r4", row);
        assertSame(test.list().contains("r4"), true);
        test.rollback();
        assertSame(test.list().contains("r4"), false);
    }

    @Test(expected = IllegalStateException.class)
    public void testClose1() throws Exception {
        ArrayList<Class<?>> list = new ArrayList<>();
        list.add(String.class);
        prov.createTable("testTable", list);
        DBase tmp = (DBase) prov.getTable("testTable");
        tmp.close();
        tmp.size();
    }

    @Test
    public void testClose2() throws Exception {
        ArrayList<Class<?>> list = new ArrayList<>();
        list.add(String.class);
        prov.createTable("testTable", list);
        DBase tmp = (DBase) prov.getTable("testTable");
        tmp.close();
        tmp.close();
        DBase tmp2 = (DBase) prov.getTable("testTable");
        assertNotSame(tmp, tmp2);
        tmp2.size();
    }
}
