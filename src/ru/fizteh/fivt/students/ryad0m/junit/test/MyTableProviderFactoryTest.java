package ru.fizteh.fivt.students.ryad0m.junit.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.ryad0m.junit.BadFormatException;
import ru.fizteh.fivt.students.ryad0m.junit.MyTableProviderFactory;

import java.io.IOException;


public class MyTableProviderFactoryTest {
    @Rule
    public TemporaryFolder myTmpFolder = new TemporaryFolder();
    private TableProviderFactory factory;

    @Before
    public void before() {
        factory = new MyTableProviderFactory();
    }

    @Test
    public void create() throws IOException, BadFormatException {
        factory.create(myTmpFolder.newFolder("db").getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() throws IOException, BadFormatException {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithIncorrectArgument() throws IOException, BadFormatException {
        factory.create(myTmpFolder.newFile("file").getAbsolutePath());
    }
}
