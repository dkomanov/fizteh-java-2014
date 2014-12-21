package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.test;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.DataBaseTableProviderFactory;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.gen.TableRowGenerator;

import java.io.IOException;

import static org.junit.Assert.*;

public class ThreadDataBaseTableProviderTest {
    @Test
    public void testCreateDelete() {
        DataBaseProviderRunner runner = new DataBaseProviderRunner() {
            boolean flagCreated = false;
            boolean flagDropped = false;

            @Override
            public void run() {
                synchronized (this.getClass()) {
                    try {
                        Table table = provider.createTable("table2", TableRowGenerator.generateSignature());
                        if (!flagCreated) {
                            assertNotNull(table);
                        } else {
                            assertNull(table);
                        }
                        flagCreated = true;
                    } catch (IOException e) {
                        fail();
                    }
                }
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    fail();
                }
                synchronized (this.getClass()) {
                    if (flagDropped) {
                        try {
                            provider.removeTable("table2");
                            fail();
                        } catch (IllegalStateException ise) {
                            assertTrue(true);
                        }
                    } else {
                        provider.removeTable("table2");
                        flagDropped = true;
                    }
                }


            }
        };
        Thread th1 = new Thread(runner);
        Thread th2 = new Thread(runner);
        th1.start();
        th2.start();
        try {
            th1.join();
            th2.join();
        } catch (InterruptedException e) {
            fail();
        }


    }

    abstract class DataBaseProviderRunner implements Runnable {
        DataBaseTableProvider provider;
        DataBaseTableProviderFactory factory;
        TemporaryFolder folder = new TemporaryFolder();

        public DataBaseProviderRunner() {
            factory = new DataBaseTableProviderFactory();
            try {

                provider = new DataBaseTableProvider(folder.toString());
            } catch (Exception e) {
                fail();
            }
        }
    }
}
