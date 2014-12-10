package ru.fizteh.fivt.students.Volodin_Denis.JUnit.Tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.Table;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.database.TableByVolodden;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TableByVoloddenTest {

    public final static String name = "test123321123322";
    public final static String end = ".." + File.separator + name;
    public final static String end2 = ".." + File.separator + "ololoshki";
    public final static String key1 = "key1key1";
    public final static String val1 = "val1val1";
    public final static String val2 = "val2lav2";
    public final static String val3 = "val3lav3";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testGetName() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        assertEquals(name, table.getName());
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testGetNameIfTableExist() throws Exception {
        Table table = new TableByVolodden(name);
        assertEquals(name, table.getName());
        Files.deleteIfExists(Paths.get(temporaryFolder.toString(), ".." + File.separator, name).toAbsolutePath().normalize());
    }

    @Test
    public void testGet() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        assertEquals(null, table.get(key1));
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testGetIfExists() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        assertEquals(val1, table.get(key1));
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testPut() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        assertEquals(null, table.put(key1, val1));
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testPutIfExists() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        assertEquals(val1, table.put(key1, val2));
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testPutIfExistsInDataBase() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        table.commit();
        assertEquals(val1, table.put(key1, val1));
        table.remove(key1);
        table.commit();
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testPutIfExistsInDataBaseEqual() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        table.commit();
        table.put(key1, val2);
        assertEquals(val2, table.put(key1, val1));
        table.remove(key1);
        table.commit();
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testPutIfExistsInDataBaseNotEqual() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        table.commit();
        table.put(key1, val2);
        assertEquals(val2, table.put(key1, val3));
        table.remove(key1);
        table.commit();
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testRemove() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        assertEquals(null, table.remove(key1));
        Files.deleteIfExists(Paths.get(name));
    }


    @Test
    public void testRemoveNull() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val2);
        table.commit();
        table.remove(key1);
        assertEquals(null, table.remove(key1));
        table.commit();
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testRemoveIfExists() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        assertEquals(val1, table.remove(key1));
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testSize() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        assertEquals(0, table.size());
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testSizeWithUncommittedValues() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        table.commit();
        table.put(key1, val2);
        assertEquals(1, table.size());
        table.remove(key1);
        assertEquals(0, table.size());
        table.commit();
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testGetNumberOfUncommittedChanges() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        assertEquals(0, table.getNumberOfUncommittedChanges());
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testCommit() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        assertEquals(table.commit(), 0);
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testRollback() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        assertEquals(0, table.rollback());
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testList() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        List<String> list = table.list();
        assertEquals(0, list.size());
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testListWithValues() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        List<String> list = table.list();
        assertEquals(1, list.size());
        assertEquals(key1, list.get(0));
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testListWithValuesInDataBase() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        table.commit();
        List<String> list = table.list();
        assertEquals(1, list.size());
        assertEquals(key1, list.get(0));
        table.remove(key1);
        table.commit();
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testListWithValuesInDataBaseAndInDiff() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        table.commit();
        table.put(key1, val2);
        List<String> list = table.list();
        assertEquals(1, list.size());
        assertEquals(key1, list.get(0));
        table.remove(key1);
        table.commit();
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void testReadFromDisk() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        Table table = new TableByVolodden(name);
        table.put(key1, val1);
        table.commit();
        Table table2 = new TableByVolodden(name);
        assertEquals(val1, table2.get(key1));
        table.remove(key1);
        table.commit();
        Files.deleteIfExists(Paths.get(name));
    }
}