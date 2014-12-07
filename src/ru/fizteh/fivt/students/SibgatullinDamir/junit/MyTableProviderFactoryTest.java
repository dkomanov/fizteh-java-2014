package ru.fizteh.fivt.students.SibgatullinDamir.junit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

/**
 * Created by Lenovo on 10.11.2014.
 */
public class MyTableProviderFactoryTest {

    private TableProviderFactory factory;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void initFactory() {
        factory = new MyTableProviderFactory();
    }

    @Test
    public void create() throws IOException {
        factory.create(tempFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithIncorrectArgument() throws IOException {
        factory.create(tempFolder.newFile().getAbsolutePath());
    }
}
