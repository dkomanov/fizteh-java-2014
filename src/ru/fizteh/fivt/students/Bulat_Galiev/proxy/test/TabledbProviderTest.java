package ru.fizteh.fivt.students.Bulat_Galiev.proxy.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.proxy.Storeabledb;
import ru.fizteh.fivt.students.Bulat_Galiev.proxy.Tabledb;
import ru.fizteh.fivt.students.Bulat_Galiev.proxy.TabledbProvider;

public class TabledbProviderTest {
    private static final int INTVALUENUMBER = 139;
    private static final int SLEEPTIME = 200;
    private static ArrayList<Class<?>> typeList;
    private TableProvider provider;
    private Path testDir;
    private Path testDirTwo;
    private static boolean tableNotCreatedFlagFirst;
    private static boolean tableNotCreatedFlagSecond;

    @Before
    public final void setUp() throws Exception {
        typeList = new ArrayList<Class<?>>();
        typeList.add(String.class);
        typeList.add(Integer.class);
        String tmpDirPrefix = "Swing_";
        String tmpDirPrefixTwo = "Swing_Two";
        testDir = Files.createTempDirectory(tmpDirPrefix);
        testDirTwo = Files.createTempDirectory(tmpDirPrefixTwo);
        provider = new TabledbProvider(testDir.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPassDirectoryWithNotExistParentToCreate()
            throws Exception {
        TableProvider secondProvider = new TabledbProvider(
                testDirTwo.toString() + File.separatorChar + "IDoNot_exist"
                        + File.separatorChar + "IDoNot_existToo");
        ((TabledbProvider) secondProvider).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testHomeDirectoryContainsFiles() throws Exception {
        testDirTwo.resolve("fileName").toFile().createNewFile();
        TableProvider secondProvider = new TabledbProvider(
                testDirTwo.toString());
        ((TabledbProvider) secondProvider).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testHomeDirectoryIsFile() throws Exception {
        testDirTwo = testDirTwo.resolve("fileName");
        testDirTwo.toFile().createNewFile();
        TableProvider secondProvider = new TabledbProvider(
                testDirTwo.toString());
        ((TabledbProvider) secondProvider).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPassInvalidCharactersDir() throws Exception {
        provider.createTable("/*?*/", typeList);
    }

    @Test
    public final void testPassNormalDirectory() throws Exception {
        provider = new TabledbProvider(testDirTwo.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateNullTable() throws Exception {
        provider.createTable(null, typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateNamelessTable() throws Exception {
        provider.createTable("", typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateSpacesNameTable() throws Exception {
        provider.createTable("          ", typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateInvalidCharactersTable() throws Exception {
        provider.createTable("/*?*/", typeList);
    }

    @Test
    public final void testCreateNormalTable() throws Exception {
        Assert.assertNotNull(provider.createTable("table", typeList));
    }

    @Test
    public final void testCreateExistingTable() throws Exception {
        provider.createTable("table1", typeList);
        Assert.assertNull(provider.createTable("table1", typeList));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetNullTable() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetNamelessTable() throws Exception {
        provider.getTable("          ");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetInvalidCharactersTable() throws Exception {
        provider.getTable("/*?*/");
    }

    @Test
    public final void testGetNotExistingTable() throws Exception {
        Assert.assertNull(provider.getTable("notExistingTable"));
    }

    @Test
    public final void testGetExistingTable() throws Exception {
        Table table1 = provider.createTable("firstTable", typeList);
        Assert.assertEquals(table1, provider.getTable("firstTable"));
        Table table2 = provider.createTable("secondTable", typeList);
        Assert.assertEquals(table1, provider.getTable("firstTable"));
        Assert.assertEquals(table2, provider.getTable("secondTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveNamelessTable() throws Exception {
        provider.removeTable("          ");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveInvalidCharactersTable() throws Exception {
        provider.removeTable("/*?*/");
    }

    @Test(expected = IllegalStateException.class)
    public final void testRemoveNotExistingTable() throws Exception {
        provider.removeTable("notExistingTable");
    }

    @Test
    public final void testRemoveExistingTable() throws Exception {
        provider.createTable("table", typeList);
        provider.removeTable("table");
        Assert.assertNotNull(provider.createTable("table", typeList));
    }

    @Test
    public final void testGetTableNames() throws IOException {
        provider.createTable("table1", typeList);
        provider.createTable("table2", typeList);
        List<String> namesList = provider.getTableNames();
        List<String> answerNamesList = new ArrayList<String>();
        answerNamesList.add("table2");
        answerNamesList.add("table1");
        Assert.assertEquals(namesList, answerNamesList);
    }

    @Test
    public final void testChangeCurTable() throws Exception {
        Table singletable = provider.createTable("table", typeList);
        ((TabledbProvider) provider).changeCurTable("table");
        Assert.assertEquals(((TabledbProvider) provider).getDataBase(),
                singletable);
    }

    @Test
    public final void testGetNullDataBase() throws Exception {
        Assert.assertNull(((TabledbProvider) provider).getDataBase());
    }

    @Test
    public final void testGetNormalDataBase() throws Exception {
        provider.createTable("table", typeList);
        ((TabledbProvider) provider).changeCurTable("table");
        Assert.assertNotNull(((TabledbProvider) provider).getDataBase());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public final void testCreateForThrowsExceptionIfColumnsAndValuesAmountAreNotEqual()
            throws IOException {
        Table singleTable = provider.createTable("table", typeList);
        List<String> values = new ArrayList<>();
        values.add("stuff");
        provider.createFor(singleTable, values);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public final void testCreateForThrowsExceptionIfColumnAndValuesTypesAreNotCompatible()
            throws IOException {
        Table singleTable = provider.createTable("table", typeList);
        List<Object> values = new ArrayList<>();
        values.add(1, "stuff");
        values.add(2, null);
        provider.createFor(singleTable, values);
    }

    @Test
    public final void testCreateForNormalTable() throws IOException {
        List<Object> values = new ArrayList<>();
        values.add(0, "Testing");
        values.add(1, INTVALUENUMBER);
        Storeable storeableExpected = new Storeabledb(values);

        Table singleTable = provider.createTable("table", typeList);
        Storeable storeableValue = provider.createFor(singleTable);
        storeableValue = new Storeabledb(values);

        Assert.assertEquals(storeableValue.getStringAt(0),
                storeableExpected.getStringAt(0));
        Assert.assertEquals(storeableValue.getIntAt(1),
                storeableExpected.getIntAt(1));
    }

    @Test
    public final void testCreateForNormal() throws IOException {
        Table singleTable = provider.createTable("table", typeList);
        List<Object> values = new ArrayList<>();
        values.add(0, "Testing");
        values.add(1, INTVALUENUMBER);
        Storeable storeableExpected = new Storeabledb(values);
        Storeable result = provider.createFor(singleTable, values);
        Assert.assertEquals(result.getStringAt(0),
                storeableExpected.getStringAt(0));
        Assert.assertEquals(result.getIntAt(1), storeableExpected.getIntAt(1));
    }

    @Test
    public void testMultiThreadCreateSameTables() throws Exception {
        tableNotCreatedFlagFirst = false;
        tableNotCreatedFlagSecond = false;
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tableNotCreatedFlagFirst = (provider.createTable("table",
                            typeList) == null);
                } catch (IOException ignored) {
                    // Disable exception processing.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEPTIME);
                    tableNotCreatedFlagSecond = (provider.createTable("table",
                            typeList) == null);
                } catch (IOException ignored) {
                    // Disable exception processing.
                } catch (InterruptedException e) {
                    // Disable exception processing.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertTrue(tableNotCreatedFlagFirst ^ tableNotCreatedFlagSecond);
    }

    @Test
    public void testMultiThreadCreateRemoveSameTables() throws Exception {
        tableNotCreatedFlagFirst = false;
        tableNotCreatedFlagSecond = false;
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tableNotCreatedFlagFirst = (provider.createTable(
                            "parallel", typeList) != null);
                } catch (IOException ignored) {
                    // Disable exception processing.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEPTIME);
                    provider.removeTable("parallel");
                } catch (IOException ignored) {
                    // Disable exception processing.
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertNull(provider.getTable("parallel"));
    }

    @Test
    public void testMultiThreadCreateGetSameTables() throws Exception {
        final AtomicReference<Tabledb> ref1 = new AtomicReference<Tabledb>();
        final AtomicReference<Tabledb> ref2 = new AtomicReference<Tabledb>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ref1.set((Tabledb) provider.createTable("table1", typeList));
                } catch (IOException ignored) {
                    // Disable exception processing.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEPTIME);
                    ref2.set((Tabledb) provider.getTable("table1"));
                } catch (InterruptedException e) {
                    // Disable exception processing.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(ref1.get(), ref2.get());
    }

    @After
    public final void tearDown() throws Exception {
        Cleaner.clean(testDir.toFile());
    }

}
