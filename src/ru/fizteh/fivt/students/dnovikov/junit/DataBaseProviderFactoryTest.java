package ru.fizteh.fivt.students.dnovikov.junit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;

import java.io.IOException;

import static org.junit.Assert.*;

public class DataBaseProviderFactoryTest {

    private TableProviderFactory factory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        factory = new DataBaseProviderFactory();
    }

    @Test
    public void create() throws IOException {
        factory.create(tmpFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgument() {
        factory.create(null);
    }

    @Test(expected = LoadOrSaveException.class)
    public void createWithIncorrectArgument() throws IOException {
        factory.create(tmpFolder.newFile().getAbsolutePath());
    }


}