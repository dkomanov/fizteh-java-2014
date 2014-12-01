package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.MultiFileTableProviderFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MultiFileTableProviderConcurrentTest {
    static String root = "test/junit-parallel";
    static String name = "test-db-provider";
    static List<Class<?>> signature;

    TableProviderFactory factory;
    TableProvider provider;

    @BeforeClass
    public static void setUp() {
        signature = Arrays.asList(Integer.class, String.class);

        TableProviderFactory factory = new MultiFileTableProviderFactory();
        TableProvider dummyProvider;

        try {
            dummyProvider = factory.create(root);
            dummyProvider.removeTable(name);
        } catch (IllegalStateException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void preRun() {
        factory = new MultiFileTableProviderFactory();
        try {
            provider = factory.create(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        exists = false;
        exception = null;
    }

    @After
    public void postRun() {
        try {
            provider.removeTable(name);
        } catch (IllegalStateException | IOException e) {
            //
        }
    }

    volatile boolean exists;
    Throwable exception;

    @Test
    public void testCreate() throws Throwable {
        Runnable runnable = () -> {
            synchronized (this.getClass()) {
                try {
                    Table table = provider.createTable(name, signature);
                    if (exists) {
                        assertNull(table);
                    } else {
                        assertNotNull(table);
                        exists = true;
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
    public void testRemove() throws Throwable {
        Runnable runnable = () -> {
            synchronized (this.getClass()) {
                if (exists) {
                    try {
                        provider.removeTable(name);
                        exists = false;
                    } catch (Throwable e) {
                        exception = e;
                    }
                } else {
                    try {
                        provider.removeTable(name);
                    } catch (IllegalStateException e) {
                        return;
                    } catch (Throwable e) {
                        exception = e;
                    }
                    fail();
                }
            }
        };
        provider.createTable(name, signature);
        exists = true;
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
    public void testGet() throws Throwable {
        Runnable runnable = () -> {
            synchronized (this.getClass()) {
                try {
                    Table table = provider.getTable(name);
                    if (exists) {
                        assertNotNull(table);
                    } else {
                        assertNull(table);
                        provider.createTable(name, signature);
                        exists = true;
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
