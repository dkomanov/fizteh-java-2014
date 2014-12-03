package ru.fizteh.fivt.students.standy66_new.tests.task5;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabaseFactory;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class TableProviderFactoryTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private TableProviderFactory factory;

    public TableProviderFactoryTest(TableProviderFactory tableProviderFactory) {
        factory = tableProviderFactory;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        new StructuredDatabaseFactory()
                }
        });
    }

    @Test
    public void testCreate() throws Exception {
        assertNotNull(factory.create(folder.newFolder().getAbsolutePath()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }


}
