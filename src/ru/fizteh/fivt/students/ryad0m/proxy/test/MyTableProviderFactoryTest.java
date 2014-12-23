package ru.fizteh.fivt.students.ryad0m.proxy.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.ryad0m.proxy.MyTableProviderFactory;

import java.io.IOException;


public class MyTableProviderFactoryTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    private MyTableProviderFactory factory;

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

    @Test(expected = IllegalStateException.class)
    public void closeAndCallMethod() throws Exception {
        factory.create(tmpFolder.newFolder("db").getAbsolutePath());
        factory.close();
        factory.create(tmpFolder.newFolder("db").getAbsolutePath());
    }

}
