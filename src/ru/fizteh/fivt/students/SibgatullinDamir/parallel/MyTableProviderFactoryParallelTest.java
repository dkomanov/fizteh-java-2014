package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

/**
 * Created by Lenovo on 03.12.2014.
 */
public class MyTableProviderFactoryParallelTest {
    private TableProviderFactory factory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() {
        factory = new MyTableProviderFactoryParallel();
    }

    @Test
    public void create() throws IOException {
        factory.create(tmpFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() throws IOException {
        factory.create(null);
    }

    @Test(expected = IOException.class)
    public void createWithIncorrectArgument() throws IllegalArgumentException, IOException {
        factory.create(tmpFolder.newFile().getAbsolutePath());
    }
}
