package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.test;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.TableRowSerializer;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.gen.TableRowGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class ThreadDataBaseTest {

    abstract class DataBaseTableProviderRunner implements Runnable {
        DataBaseTable table;
        DataBaseTableProvider provider;
        TableRowSerializer serializer;
        ArrayList<Class<?>> signature;

        ArrayList<Storeable> values = new ArrayList<>();

        DataBaseTableProviderRunner() {
            try {
                provider = new DataBaseTableProvider("D:\\test\\database3");
            } catch (Exception e) {
                assertTrue(false);
            }
            serializer = new TableRowSerializer();
            String tableName = UUID.randomUUID().toString();
            signature = TableRowGenerator.generateSignature();
            try {
                table = (DataBaseTable) provider.createTable(tableName, signature);
            } catch (Exception e) {
                assertTrue(false);
            }
            generateValues();
        }

        void generateValues() {
            for (int i = 0; i < 100; ++i) {
                values.add(provider.createFor(table, TableRowGenerator.generateValues(signature)));
            }
        }

    }

    @Test
    public void testPutCommit() {
        DataBaseTableProviderRunner runner = new DataBaseTableProviderRunner() {
            boolean flag = false;
            boolean flagRemove = false;
            @Override
            public void run() {
                synchronized (this.getClass()) {
                    for (int i = 0; i < 100; ++i) {
                        table.put(Integer.toString(i), values.get(i));
                    }
                }
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    assertTrue(false);
                }
                synchronized (this.getClass()) {
                    try {
                        int value = table.commit();
                        if (flag) {
                            assertEquals(0, value);
                        } else {
                            assertEquals(100, value);
                        }
                        flag = true;
                    } catch (IOException e) {
                        fail();
                    }
                }
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    assertTrue(false);
                }

                synchronized (this.getClass()) {
                    for (int i = 0; i < 100; ++i) {
                        table.remove(Integer.toString(i));
                    }
                }
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    fail();
                }
                synchronized (this.getClass()) {
                    try {
                        int value = table.commit();
                        if (flagRemove) {
                            assertEquals(0, value);
                        } else {
                            assertEquals(100, value);
                        }
                        flagRemove = true;
                    } catch (IOException e) {
                        fail();
                    }
                }
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    fail();
                }
            }
        };
        Thread th1 = new Thread(runner);
        Thread th2 = new Thread(runner);

        th1.start();
        th2.start();
        try {
            th1.join();
        } catch (InterruptedException e) {
            fail();
        }
        try {
            th2.join();
        } catch (InterruptedException e) {
            fail();
        }

    }
}
