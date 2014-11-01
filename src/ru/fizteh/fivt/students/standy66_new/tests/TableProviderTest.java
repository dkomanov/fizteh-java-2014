package ru.fizteh.fivt.students.standy66_new.tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;

/**
 * Some JUnit tests on TableProvider interface.
 * Created by andrew on 01.11.14.
 */
@RunWith(value = Parameterized.class)
public class TableProviderTest {
    private TableProviderFactory factory;

    @Rule
    private TemporaryFolder temp = new TemporaryFolder();

    public TableProviderTest(TableProviderFactory factory) {
        this.factory = factory;
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableNullShouldFail() throws IOException {
        newProvider().getTable(null);
    }


    TableProvider newProvider() throws IOException {
        return factory.create(temp.newFolder().getAbsolutePath());
    }
}
