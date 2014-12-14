package ru.fizteh.fivt.students.dnovikov.junit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;
import java.io.IOException;
public class DataBaseProviderFactoryTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    private TableProviderFactory factory;

    @Before
    public void setUp() {
        factory = new DataBaseProviderFactory();
    }

    @Test
    public void create() throws IOException {
        factory.create(tmpFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = LoadOrSaveException.class)
    public void createNotExistingDirectory() throws IOException {
        factory.create(tmpFolder.newFolder().toPath().resolve("test").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArgumentThrowsException() throws IOException {
        factory.create(null);
    }

    @Test(expected = LoadOrSaveException.class)
    public void createWithIllegalArgumentThrowsException() throws IOException {
        factory.create(tmpFolder.newFile().getAbsolutePath());
    }

}
