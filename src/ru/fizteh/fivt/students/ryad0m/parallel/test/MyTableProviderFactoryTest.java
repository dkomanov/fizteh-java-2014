package ru.fizteh.fivt.students.ryad0m.parallel.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.ryad0m.parallel.MyTableProviderFactory;

import java.io.IOException;


public class MyTableProviderFactoryTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    private TableProviderFactory factory;

    @Before
    public void before() throws IOException {
        tmpFolder.create();
        factory = new MyTableProviderFactory();
    }

    @After
    public void after() throws IOException {
        tmpFolder.delete();
    }

    @Test
    public void create() throws IOException {
        factory.create(tmpFolder.newFolder("db").getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() throws IOException {
        factory.create(null);
    }

    @Test(expected = IOException.class)
    public void createWithIncorrectArgument() throws IOException {
        factory.create(tmpFolder.newFile("zxczxczxc").getAbsolutePath());
    }

}
