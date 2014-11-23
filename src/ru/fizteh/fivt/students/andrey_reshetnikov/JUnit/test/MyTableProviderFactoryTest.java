package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.andrey_reshetnikov.JUnit.MyTableProviderFactory;
import static org.junit.Assert.*;
import java.io.IOException;

public class MyTableProviderFactoryTest {

    private TableProviderFactory factory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() {
        assertNotNull(factory = new MyTableProviderFactory());
    }

    @Test
    public void create() throws IOException {
        assertNotNull(factory.create(tmpFolder.newFolder().getAbsolutePath()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() {
        assertNotNull(factory.create(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithIncorrectArgument() throws IOException {
        assertNotNull(factory.create(tmpFolder.newFile().getAbsolutePath()));
    }
}
