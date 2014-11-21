package ru.fizteh.fivt.students.standy66_new.tests.task4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabaseFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Some JUnit tests on TableProviderFactory interface.
 * Created by andrew on 01.11.14.
 */
@RunWith(value = Parameterized.class)
public class TableProviderFactoryTest {
    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();
    private final TableProviderFactory factory;

    public TableProviderFactoryTest(TableProviderFactory factory) {
        this.factory = factory;
    }

    @Parameterized.Parameters
    public static Collection parameters() {
        return Arrays.asList(new Object[][]{
                {new StringDatabaseFactory()}
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderNullShouldFail() {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderEmptyShouldFail() {
        factory.create("");
    }

    @Test(expected = RuntimeException.class)
    public void createProviderUnavailableShouldFail() {
        factory.create("\\etc\\passwd");
    }

    @Test
    public void createProvider() throws IOException {
        factory.create(temp.newFolder().getAbsolutePath());
    }
}
