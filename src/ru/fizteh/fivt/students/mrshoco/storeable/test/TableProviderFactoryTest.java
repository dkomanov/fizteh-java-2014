package test;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import strings.*;

import util.*;

public class TableProviderFactoryTest {
    private TableProviderFactory factory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() {
        factory = new MyTableProviderFactory();
    }

    @Test
    public void create() throws IOException {
        factory.create(tmpFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithIncorrectArgument() throws IOException {
        factory.create(tmpFolder.newFile().getAbsolutePath());
    }
}
