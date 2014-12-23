package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Tests;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.DatabaseTableProviderFactory;

import java.io.IOException;

public class ThreadSafeTableProviderFactoryTest {
    TableProviderFactory factory;
    private static final int THREADS_COUNT = 5;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @After
    public void afterTest() {
        try {
            ((DatabaseTableProviderFactory) factory).close();
        } catch (Exception e) {
            //
        }
    }

    @Test
    public void testCreateProvider() throws Exception {
        for (int threadNumber = 0; threadNumber < THREADS_COUNT; ++threadNumber) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    createProvider();
                }
            });
            thread.start();
        }
        createProvider();
    }

    private void createProvider() {
        factory = new DatabaseTableProviderFactory();
        try {
            factory.create(folder.getRoot().getPath());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
