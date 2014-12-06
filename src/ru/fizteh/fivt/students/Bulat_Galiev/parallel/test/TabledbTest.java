package ru.fizteh.fivt.students.Bulat_Galiev.parallel.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.TabledbProvider;

public class TabledbTest {
    private static final int CHECKNUMBERZERO = 0;
    private static final int CHECKNUMBERONE = 1;
    private static final int CHECKNUMBERTWO = 2;
    private static final int CHECKNUMBERTHREE = 3;
    private static final int SLEEPTIME = 200;
    private static List<Class<?>> typeList;
    private static List<Class<?>> typeList1;
    private TableProvider provider;
    Table table;
    Table table1;
    Path testDir;

    private String get(String key) {
        Storeable storeableValue = table.get(key);
        if (storeableValue == null) {
            return null;
        }
        String stringValue = provider.serialize(table, storeableValue);
        return stringValue.substring(2, stringValue.length() - 2);
    }

    private String put(String key, String value) throws ColumnFormatException,
            ParseException {
        Storeable storeableValue = table.put(key,
                provider.deserialize(table, "[\"" + value + "\"]"));
        if (storeableValue == null) {
            return null;
        }
        String stringValue = provider.serialize(table, storeableValue);
        return stringValue.substring(2, stringValue.length() - 2);
    }

    private String remove(String key) {
        Storeable storeableValue = table.remove(key);
        if (storeableValue == null) {
            return null;
        }
        String stringValue = provider.serialize(table, storeableValue);
        return stringValue.substring(2, stringValue.length() - 2);
    }

    private String get1(String key) {
        Storeable storeableValue = table1.get(key);
        if (storeableValue == null) {
            return null;
        }
        String stringValue = provider.serialize(table1, storeableValue);
        return stringValue.substring(1, stringValue.length() - 1);
    }

    private String put1(String key, String value, Integer intvalue)
            throws ColumnFormatException, ParseException {
        Storeable storeableValue = table1.put(
                key,
                provider.deserialize(table1, "[\"" + value + "\"" + ","
                        + Integer.toString(intvalue) + "]"));
        if (storeableValue == null) {
            return null;
        }
        String stringValue = provider.serialize(table1, storeableValue);
        return stringValue.substring(1, stringValue.length() - 1);
    }

    private String remove1(String key) {
        Storeable storeableValue = table1.remove(key);
        if (storeableValue == null) {
            return null;
        }
        String stringValue = provider.serialize(table1, storeableValue);
        return stringValue.substring(1, stringValue.length() - 1);
    }

    @Before
    public void setUp() throws Exception {
        String tmpDirPrefix = "Swing_";
        testDir = Files.createTempDirectory(tmpDirPrefix);
        provider = new TabledbProvider(testDir.toString());
        typeList = new ArrayList<Class<?>>();
        typeList1 = new ArrayList<Class<?>>();
        typeList.add(String.class);
        typeList1.add(String.class);
        typeList1.add(Integer.class);
        table = provider.createTable("test", typeList);
        table1 = provider.createTable("test1", typeList1);
    }

    @After
    public void tearDown() throws Exception {
        Cleaner.clean(testDir.toFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        put(null, "value");
    }

    @Test
    public void testPutNullValue() throws Exception {
        put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutIncorrectKey() throws Exception {
        put("     ", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncorrectKey() throws Exception {
        get("     ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveIncorrectKey() throws Exception {
        remove("      ");
    }

    @Test
    public void testPutNormal() throws Exception {
        Assert.assertNull(put("1", "2"));
    }

    @Test
    public void testPutNormalTwoValues() throws Exception {
        Assert.assertNull(put1("1", "2", CHECKNUMBERTHREE));
    }

    @Test
    public void testPutOverwrite() throws Exception {
        put("1", "2");
        Assert.assertEquals("2", put("1", "3"));
    }

    @Test
    public void testRemoveNotExistingKey() throws Exception {
        Assert.assertNull(remove("NotExistisngKey"));
    }

    @Test
    public void testRemoveNormal() throws Exception {
        put("1", "2");
        Assert.assertEquals("2", remove("1"));
    }

    @Test
    public void testRemoveNormalTwoValues() throws Exception {
        put1("1", "2", CHECKNUMBERTHREE);
        Assert.assertEquals("\"2\",3", remove1("1"));
    }

    @Test
    public void testGetNotExistingKey() throws Exception {
        Assert.assertNull(get("NotExistingKey"));
    }

    @Test
    public void testGetNormal() throws Exception {
        put("1", "2");
        Assert.assertEquals("2", get("1"));
    }

    @Test
    public void testGetNormalTwoValues() throws Exception {
        put1("1", "2", CHECKNUMBERTHREE);
        Assert.assertEquals("\"2\",3", get1("1"));
    }

    @Test
    public void testRussian() throws Exception {
        put("Ключ", "Значение");
        Assert.assertEquals("Значение", get("Ключ"));
    }

    @Test
    public void testGetOverwritten() throws Exception {
        put("1", "2");
        put("1", "3");
        Assert.assertEquals("3", get("1"));
    }

    @Test
    public void testGetRemoved() throws Exception {
        put("1", "2");
        put("3", "d");
        Assert.assertEquals("d", get("3"));
        remove("3");
        Assert.assertNull(get("3"));
    }

    @Test
    public void testCommit() throws Exception {
        Assert.assertEquals(CHECKNUMBERZERO, table.commit());
    }

    @Test
    public void testRollback() throws Exception {
        Assert.assertEquals(CHECKNUMBERZERO, table.rollback());
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(CHECKNUMBERZERO, table.size());
    }

    @Test
    public void testPutRollbackGet() throws Exception {
        put("useless", "void");
        table.rollback();
        Assert.assertNull(get("useless"));
    }

    @Test
    public void testPutCommitGet() throws Exception {
        put("1", "2");
        Assert.assertEquals(CHECKNUMBERONE, table.commit());
        Assert.assertEquals("2", get("1"));
    }

    @Test
    public void testPutCommitRemoveRollbackGet() throws Exception {
        put("key", "value");
        table.commit();
        table.remove("key");
        table.rollback();
        Assert.assertEquals("value", get("key"));
    }

    @Test
    public void testPutRemoveSize() throws Exception {
        put("1", "2");
        put("2", "3");
        remove("3");
        Assert.assertEquals(CHECKNUMBERTWO, table.size());
        remove("2");
        Assert.assertEquals(CHECKNUMBERONE, table.size());
    }

    @Test
    public void testPutCommitRollbackSize() throws Exception {
        put("1", "2");
        put("2", "3");
        put("2", "3");
        Assert.assertEquals(CHECKNUMBERTWO, table.commit());
        Assert.assertEquals(CHECKNUMBERTWO, table.size());
        remove("2");
        remove("1");
        Assert.assertEquals(CHECKNUMBERZERO, table.size());
        Assert.assertEquals(CHECKNUMBERTWO, table.rollback());
        Assert.assertEquals(CHECKNUMBERTWO, table.size());
    }

    @Test(expected = ParseException.class)
    public void testPutValuesNotMatchingTypes() throws Exception {
        ArrayList<Class<?>> newTypeList = new ArrayList<Class<?>>();
        newTypeList.add(Integer.class);
        table = provider.createTable("test2", newTypeList);
        put("key", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutWrongStyleIntoSignature() throws Exception {
        Path newTablePath = testDir.resolve("test");
        Path signature = newTablePath.resolve("signature.tsv");
        FileOutputStream fileOutputStream = new FileOutputStream(
                signature.toFile());
        String wrongStyle = "Wrong Style Signature";
        fileOutputStream.write(wrongStyle.getBytes());
        fileOutputStream.close();
        table = provider.getTable("test");
    }

    @Test
    public void testNumberOfUncommittedChanges() throws Exception {
        put("key", "value");
        put("key2", "value2");

        Assert.assertEquals(CHECKNUMBERTWO,
                table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges1() throws ParseException {
        put("key", "value");
        put("key", "value2");

        Assert.assertEquals(CHECKNUMBERONE,
                table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges2() throws ParseException {
        put("key", "value");
        remove("key");

        Assert.assertEquals(CHECKNUMBERZERO,
                table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges3() throws ParseException,
            IOException {
        put("key", "value");
        table.commit();
        remove("key");

        Assert.assertEquals(CHECKNUMBERONE,
                table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges4() throws ParseException,
            IOException {
        put("key", "value");
        table.commit();
        remove("key");
        put("key", "value");

        Assert.assertEquals(CHECKNUMBERZERO,
                table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges5() throws ParseException,
            IOException {
        put("key", "value");
        table.commit();
        remove("key");
        put("key", "value2");

        Assert.assertEquals(CHECKNUMBERONE,
                table.getNumberOfUncommittedChanges());
    }

    @Test(expected = ColumnFormatException.class)
    public void testPutOneStoreableToAnotherTable() throws IOException {
        Table table2 = provider.createTable("table2", typeList);
        Storeable storeableValue = provider.createFor(table);

        table2.put("key", storeableValue);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetColumnTypeAtBadIndex() {

        table.getColumnType(table.getColumnsCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetColumnTypeAtBadIndex1() {
        table.getColumnType(-1);
    }

    @Test
    public void testMultiThreadPut() throws Exception {
        final AtomicReference<String> ref1 = new AtomicReference<String>();
        final AtomicReference<String> ref2 = new AtomicReference<String>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ref1.set(put("key", "value"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ref2.set(put("key", "value"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertNull(ref1.get());
        Assert.assertNull(ref1.get());
    }

    @Test
    public void testMultiThreadPutCommit() throws Exception {
        final AtomicReference<Integer> ref1 = new AtomicReference<Integer>();
        final AtomicReference<Integer> ref2 = new AtomicReference<Integer>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    put("key1", "value");
                    Thread.sleep(SLEEPTIME);
                    ref1.set(table.commit());
                } catch (Exception e) {
                    // Disable exception processing.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    put("key2", "value");
                    Thread.sleep(SLEEPTIME);
                    ref2.set(table.commit());
                } catch (Exception e) {
                    // Disable exception processing.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(1, ref1.get().intValue());
        Assert.assertEquals(1, ref2.get().intValue());
    }

    @Test
    public void testMultiThreadPutSize() throws Exception {
        final AtomicReference<Integer> ref1 = new AtomicReference<Integer>();
        final AtomicReference<Integer> ref2 = new AtomicReference<Integer>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    put("key", "value");
                    ref1.set(table.size());
                } catch (ParseException e) {
                    // Disable exception processing.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    put("key1", "value1");
                    put("key2", "value2");
                    ref2.set(table.size());
                } catch (ParseException e) {
                    // Disable exception processing.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(1, ref1.get().intValue());
        Assert.assertEquals(2, ref2.get().intValue());
    }

    @Test
    public void testMultiThreadPutCommitSecond() throws Exception {
        final AtomicReference<Integer> ref1 = new AtomicReference<Integer>();
        final AtomicReference<Integer> ref2 = new AtomicReference<Integer>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    put("key", "value");
                    ref1.set(table.commit());
                } catch (ParseException e) {
                    // Disable exception processing.
                } catch (IOException e) {
                    // Disable exception processing.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    put("key", "value");
                    put("key1", "value1");
                    ref2.set(table.commit());
                } catch (ParseException e) {
                    // Disable exception processing.
                } catch (IOException e) {
                    // Disable exception processing.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(1, ref1.get().intValue());
        Assert.assertEquals(2, ref2.get().intValue());
    }

    @Test
    public void testMultiThreadPutCommitRollbackGet() throws Exception {
        final AtomicReference<Integer> ref1 = new AtomicReference<Integer>();
        final AtomicReference<Integer> ref2 = new AtomicReference<Integer>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    put("key", "value");
                    ref1.set(table.commit());
                } catch (Exception e) {
                    // Disable exception processing.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEPTIME);
                    put("key", "value");
                    ref2.set(table.rollback());
                } catch (Exception e) {
                    // Disable exception processing.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(1, ref1.get().intValue());
        Assert.assertEquals(1, ref2.get().intValue());
    }

    @Test
    public void testMultiThreadPutPutCommit() throws Exception {
        final AtomicReference<Integer> ref1 = new AtomicReference<Integer>();
        put("key", "value");
        table.commit();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    put("key", "value");
                    Thread.sleep(SLEEPTIME);
                    ref1.set(table.commit());
                } catch (Exception e) {
                    // Disable exception processing.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEPTIME);
                    put("key", "value1");
                    table.commit();
                } catch (Exception e) {
                    // Disable exception processing.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(1, ref1.get().intValue());
    }

}
