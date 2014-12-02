package ru.fizteh.fivt.students.gudkov394.Proxy.test;

/**
 * Created by kagudkov on 01.12.14.
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.gudkov394.Proxy.TableProviderFactoryWithCloseAndToString;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class testFactory {
    private TableProviderFactoryWithCloseAndToString factory;
    private String dbDirPath;
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        tmpFolder.create();
        factory = new TableProviderFactoryWithCloseAndToString();
        dbDirPath = tmpFolder.newFolder("test").getAbsolutePath();
    }

    @After
    public void after() {
        tmpFolder.delete();
    }

    @Test
    public void create() throws IOException {
        assertNotNull(factory.create(tmpFolder.newFolder("test").getAbsolutePath()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() throws IOException {
        factory.create(null);
    }

    @Test(expected = IllegalStateException.class)
    public void closeAndCallMethod() throws Exception {
        factory.create(tmpFolder.newFolder("test").getAbsolutePath());
        factory.close();
        factory.create(tmpFolder.newFolder("test").getAbsolutePath());
    }
}