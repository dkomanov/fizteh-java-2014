package ru.fizteh.fivt.students.andrey_reshetnikov.Parallel.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.andrey_reshetnikov.Parallel.ParallelTableProviderFactory;

import java.io.IOException;

public class ParallelTableProviderFactoryTest {
    private TableProviderFactory factory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() {
        factory = new ParallelTableProviderFactory();
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
