package ru.fizteh.fivt.students.andrey_reshetnikov.Proxy.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.andrey_reshetnikov.Proxy.AdvancedTableProviderFactory;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class AdvancedTableProviderFactoryTest {
    private AdvancedTableProviderFactory factory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() {
        factory = new AdvancedTableProviderFactory();
    }

    @Test
    public void create() throws IOException {
        assertNotNull(factory.create(tmpFolder.newFolder().getAbsolutePath()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() throws IOException {
        factory.create(null);
    }

    @Test(expected = IOException.class)
    public void createWithIncorrectArgument() throws IllegalArgumentException, IOException {
        factory.create(tmpFolder.newFile().getAbsolutePath());
    }

    @Test(expected = IllegalStateException.class)
    public void closeAndCallMethod() throws Exception {
        factory.create(tmpFolder.newFolder().getAbsolutePath());
        factory.close();
        factory.create(tmpFolder.newFolder().getAbsolutePath());
    }
}
