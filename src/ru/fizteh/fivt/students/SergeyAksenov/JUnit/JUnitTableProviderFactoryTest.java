package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class JUnitTableProviderFactoryTest {


    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void initFactory() {
        factory = new JUnitTableProviderFactory();
    }

    @Test
    public void create() throws IOException {
        factory.create(tempFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() {
        factory.create(null);
    }

    private JUnitTableProviderFactory factory;
}