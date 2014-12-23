package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.test;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.MultiFileTableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MultiFileTableConcurrentTest {
    static String name = "test-db-provider";
    static List<Class<?>> signature;
    static String key = "key";
    static Storeable value;

    TableProviderFactory factory;
    TableProvider provider;
    Table table;

    @BeforeClass
    public static void setUp() {
        signature = Arrays.asList(Integer.class, String.class);

        TableProviderFactory factory = new MultiFileTableProviderFactory();
        TableProvider dummyProvider;
        Table dummyTable;

        try {
            dummyProvider = factory.create(MultiFileTableTest.rootName);
            if (dummyProvider.getTable(name) != null) {
                dummyProvider.removeTable(name);
            }
            dummyTable = dummyProvider.createTable(name, signature);
            value = dummyProvider.deserialize(dummyTable, "[0,\"value\"]");
            dummyProvider.removeTable(name);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void preRun() {
        factory = new MultiFileTableProviderFactory();
        try {
            provider = factory.create(MultiFileTableTest.rootName);
            table = provider.createTable(name, signature);
        } catch (IOException e) {
            e.printStackTrace();
        }
        exception = null;
        flag = false;
    }

    @After
    public void postRun() {
        try {
            provider.removeTable(name);
        } catch (IllegalStateException | IOException e) {
            //
        }
    }

    @AfterClass
    public static void tearDown() {
        try {
            Files.deleteIfExists(Paths.get(MultiFileTableTest.rootName));
        } catch (IOException e) {
            //
        }
    }

    Throwable exception;
    boolean flag;

    @Test
    public void testIndependent() throws Throwable {
        Runnable runnable = () -> {
            synchronized (this.getClass()) {
                try {
                    if (!flag) {
                        table.put(key, value);
                        flag = true;
                    } else {
                        assertNull(table.get(key));
                    }
                } catch (Throwable e) {
                    exception = e;
                }
            }
        };
        Thread a = new Thread(runnable);
        Thread b = new Thread(runnable);
        a.start();
        b.start();
        a.join();
        b.join();
        if (exception != null) {
            throw exception;
        }
    }

    @Test
    public void testCommit() throws Throwable {
        Runnable runnable = () -> {
            synchronized (this.getClass()) {
                try {
                    if (!flag) {
                        flag = true;
                        table.put(key, value);
                        table.commit();
                    } else {
                        assertEquals(value, table.get(key));
                    }
                } catch (Throwable e) {
                    exception = e;
                }
            }
        };
        Thread a = new Thread(runnable);
        Thread b = new Thread(runnable);
        a.start();
        b.start();
        a.join();
        b.join();
        if (exception != null) {
            throw exception;
        }
    }

    @Test
    public void testRollback() throws Throwable {
        Runnable runnable = () -> {
            synchronized (this.getClass()) {
                try {
                    if (!flag) {
                        flag = true;
                        table.put(key, value);
                        table.rollback();
                    } else {
                        assertNull(table.get(key));
                    }
                } catch (Throwable e) {
                    exception = e;
                }
            }
        };
        Thread a = new Thread(runnable);
        Thread b = new Thread(runnable);
        a.start();
        b.start();
        a.join();
        b.join();
        if (exception != null) {
            throw exception;
        }
    }
}
