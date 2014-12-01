package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.TableSystem.DatabaseTableProviderFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeTableTest {
    private static final int THREADS_NUMBER = 10;
    private static final int KEYS_NUMBER = 10;
    private static final String DATABASE_DIRECTORY = "\\temp\\storeable_table_test";
    private static final String TABLE_NAME = "table";

    private TableProvider provider;
    private Table currentTable;

    private AtomicInteger atomicCounter = new AtomicInteger(0);

    @Before
    public void setUp() throws Exception {
        TableProviderFactory factory = new DatabaseTableProviderFactory();
        provider = factory.create(DATABASE_DIRECTORY);
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(String.class);
        currentTable = provider.createTable(TABLE_NAME, columnTypes);
    }

    @After
    public void cleanUp() throws Exception {
        provider.removeTable(TABLE_NAME);
    }

    @Test
    public void testOnlyOneThreadHasAccess() {
        List<Thread> threads = new ArrayList<Thread>();
        for (int index = 0; index < THREADS_NUMBER; ++index) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    checkOnlyOneThreadHasAccess();
                }
            }));
            threads.get(index).start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                return;
            }
        }
        Assert.assertEquals(THREADS_NUMBER * KEYS_NUMBER, atomicCounter.get());
    }

    private void checkOnlyOneThreadHasAccess() {
        for (int index = 0; index < KEYS_NUMBER; ++index) {
            String key = String.format("key%d", index);
            currentTable.put(key, makeStoreable(index));
        }

        for (int index = 0; index < KEYS_NUMBER; ++index) {
            String key = String.format("key%d", index);
            Storeable value1 = makeStoreable(index);
            Storeable value2 = currentTable.get(key);
            if (value1 == null || value2 == null) {
                System.out.println(this.getClass().toString() + ": Program works incorrect! Error in method get.");
            }
            if (value1.toString().equals(value2.toString())) {
                atomicCounter.getAndIncrement();
            }
        }
    }

    private Storeable makeStoreable(int value) {
        String xml = String.format("<row><col>value%d%d</col></row>", Thread.currentThread().getId(), value);
        try {
            return provider.deserialize(currentTable, xml);
        } catch (ParseException e) {
            return null;
        }
    }
}
