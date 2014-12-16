package ru.fizteh.fivt.students.elina_denisova.j_unit.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.elina_denisova.j_unit.MyTableProviderFactory;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;

public class MyTableProviderFactoryTest {

    private TableProviderFactory factory;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void before() {
        factory = new MyTableProviderFactory();
    }

    @Test
    public void create() throws IOException {
        assertNotNull(factory.create(folder.newFolder().getCanonicalPath()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithIncorrectArgument() throws IOException {
        factory.create("");
    }
}