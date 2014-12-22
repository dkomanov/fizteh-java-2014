package ru.fizteh.fivt.students.YaronskayaLiubov.StructuredDataTables;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by luba_yaronskaya on 17.11.14.
 */
@RunWith(Parameterized.class)
public class TableProviderFactoryTest {
    private TableProviderFactory factory;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    public TableProviderFactoryTest(TableProviderFactory factory) {
        this.factory = factory;
    }


    @Parameterized.Parameters
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(new Object[][]{
                {new StoreableDataTableProviderFactory()}
        });

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProviderNull() throws Exception {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProviderIllegalName() throws Exception {
        factory.create("");
    }

    @Test
    public void testCreateNewProvider() throws Exception {
        assertNotNull(factory.create(testFolder.newFolder().getCanonicalPath()));
    }
}

