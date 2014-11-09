package ru.fizteh.fivt.students.ilivanov.JUnit;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UnitTest {

    @Before
    public void setUp() {
        File file = new File(System.getProperty("user.dir"), "test");
        if (file.exists()) {
            deleteDirectoryOrFile(file);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createIllegalArgument() throws IOException {
        FileMapProviderFactory factory = new FileMapProviderFactory();
        factory.create(new File(System.getProperty("user.dir"), "test?").getAbsolutePath());
    }

    @Test
    public void createNull() {
        try {
            FileMapProviderFactory factory = new FileMapProviderFactory();
            factory.create(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "null location");
        }
    }

    @Test(expected = RuntimeException.class)
    public void notAFolderCheckTest() throws IOException {
        File file = new File(System.getProperty("user.dir"), "test");
        file.createNewFile();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        factory.create(file.getCanonicalPath());
    }

    @Test(expected = RuntimeException.class)
    public void notExistingDirectoryPassedShouldFail() throws IOException {
        File file = new File(System.getProperty("user.dir"), "test");
        FileMapProviderFactory factory = new FileMapProviderFactory();
        factory.create(file.getCanonicalPath());
    }

    @Test(expected = RuntimeException.class)
    public void filesInsideDirectoryShouldFail() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        File newFile = new File(testFolder, "file");
        newFile.createNewFile();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        factory.create(testFolder.getCanonicalPath());
    }

    @Test
    public void newlyCreatedTableIsEmptyWithNoUncommittedChanges() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertTrue(table.size() == 0);
        Assert.assertTrue(table.uncommittedChanges() == 0);
    }

    @Test
    public void createTableCreatesFolder() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        File tableFolder = new File(testFolder, "new");
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertTrue(tableFolder.exists() && tableFolder.isDirectory());
    }

    @Test
    public void tableNullStringsCheckTest() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        try {
            table.put(null, "test");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "put: null argument(-s)");
        }
        try {
            table.put("test", null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "put: null argument(-s)");
        }
        try {
            table.get(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "get: null argument");
        }
        try {
            table.remove(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "remove: null key");
        }
    }

    @Test
    public void getTableReturnsSameInstanceEveryTime() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertTrue(table == provider.getTable("new"));
        Assert.assertTrue(provider.getTable("new") == provider.getTable("new"));
    }

    @Test
    public void createTableReturnsNullIfTableExists() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table = provider.createTable("new");
        Assert.assertTrue(table == null);
    }

    @Test
    public void getTableReturnsNullIfTableDoesNotExist() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.getTable("new");
        Assert.assertTrue(table == null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableIllegalArgument() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.getTable("new?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableIllegalArgument() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableIllegalArgument() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        provider.removeTable("new?");
    }

    @Test
    public void providerNullArgumentCheckTest() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        try {
            provider.createTable(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "null name");
        }
        try {
            provider.getTable(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "null name");
        }
        try {
            provider.removeTable(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "null name");
        }
    }

    @Test
    public void removeTableRemovesDirectory() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        provider.createTable("new");
        provider.removeTable("new");
        File tableFolder = new File(testFolder, "new");
        Assert.assertFalse(tableFolder.exists());
    }

    @Test
    public void getTableForRemovedTableReturnsNull() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        provider.createTable("new");
        provider.removeTable("new");
        Assert.assertTrue(provider.getTable("new") == null);
    }

    @Test(expected = IllegalStateException.class)
    public void removingNonExistingTableShouldFail() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        provider.removeTable("new");
    }

    @Test
    public void getNameReturnsCorrectTableName() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertEquals(table.getName(), "new");
    }

    @Test
    public void ifPutValueGetReturnsSameValue() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("key", "value");
        Assert.assertEquals(table.get("key"), "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getThrowsIllegalArgument() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("key", "value");
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putThrowsIllegalArgument() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("key", "value");
        table.put(null, "value2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putThrowsIllegalArgument2() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("key", "value");
        table.put("key2", null);
    }

    @Test
    public void putReturnsNull() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertEquals(table.put("key", "value"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsIllegalArgument() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("key", "value");
        table.remove(null);
    }

    @Test
    public void putReturnsPreviouslyStoredValue() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("test1", "test2");
        Assert.assertEquals(table.put("test1", "test3"), "test2");
    }

    @Test
    public void getAndRemoveReturnNull() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertTrue(table.get("key") == null);
        Assert.assertTrue(table.remove("key") == null);
    }

    @Test
    public void commit() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertTrue(table.commit() == 0);
        table.put("test1", "test2");
        table.put("test1", "test2");
        table.put("test2", "test3");
        Assert.assertTrue(table.commit() == 2);
        table.put("test3", "test4");
        table.put("test3", "test5");
        Assert.assertTrue(table.commit() == 1);
        table.put("test1", "test7");
        table.remove("test1");
        Assert.assertTrue(table.commit() == 1);
    }

    @Test
    public void rollback() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertTrue(table.rollback() == 0);
        table.put("a", "b");
        Assert.assertTrue(table.rollback() == 1);
        Assert.assertTrue(table.get("a") == null);
        table.put("a", "b");
        table.commit();
        table.put("a", "c");
        table.put("e", "f");
        Assert.assertTrue(table.rollback() == 2);
        Assert.assertEquals(table.get("a"), "b");
    }

//    @Test
//    public void databaseSize() throws IOException {
//        File testFolder = new File(System.getProperty("user.dir"), "test");
//        testFolder.mkdir();
//        FileMapProviderFactory factory = new FileMapProviderFactory();
//        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
//        Assert.assertTrue(provider.size() == 0);
//        MultiFileMap table1 = provider.createTable("t1");
//        Assert.assertTrue(provider.size() == 0);
//        MultiFileMap table2 = provider.createTable("t2");
//        Assert.assertTrue(provider.size() == 0);
//        table1.put("1", "1");
//        table1.put("2", "2");
//        table1.put("3", "3");
//        Assert.assertTrue(provider.size() == 3);
//        table1.put("4", "4");
//        table1.put("5", "5");
//        table2.put("1", "1");
//        Assert.assertTrue(provider.size() == 6);
//        table2.put("2", "2");
//        table2.put("3", "3");
//        Assert.assertTrue(provider.size() == 8);
//    }

    @Test
    public void tableSize() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("a", "c");
        table.put("a", "d");
        table.put("b", "c");
        table.put("c", "e");
        table.remove("b");
        table.put("d", "f");
        Assert.assertTrue(table.size() == 3);
    }

    @Test
    public void removeDeletesKeyFromTable() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("test1", "test2");
        table.remove("test1");
        Assert.assertTrue(table.get("test1") == null);
    }

    @Test
    public void list() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertEquals(table.list().size(), 0);
        table.put("a", "val");
        Assert.assertEquals(table.list().size(), 1);
        Assert.assertEquals(table.list().get(0), "a");
        table.remove("a");
        Assert.assertEquals(table.list().size(), 0);
        table.put("a", "val");
        table.put("b", "val");
        Assert.assertEquals(table.list().size(), 2);
        Assert.assertEquals(table.list().get(0), "a");
        Assert.assertEquals(table.list().get(1), "b");
    }

    @After
    public void tearDown() {
        File file = new File(System.getProperty("user.dir"), "test");
        if (file.exists()) {
            deleteDirectoryOrFile(file);
        }
    }

    public static void deleteDirectoryOrFile(File file) {
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list != null) {
                for (File f : list) {
                    deleteDirectoryOrFile(f);
                }
            }
        }
        file.delete();
    }

}