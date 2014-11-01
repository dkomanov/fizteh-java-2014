package ru.fizteh.fivt.students.standy66_new.tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabaseFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Some JUnit tests on TableProvider interface.
 * Created by andrew on 01.11.14.
 */
@RunWith(value = Parameterized.class)
public class TableProviderTest {
    private TableProviderFactory factory;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    public TableProviderTest(TableProviderFactory factory) {
        this.factory = factory;
    }

    @Parameterized.Parameters
    public static Collection parameters() {
        return Arrays.asList(new Object[][]{
                {new StringDatabaseFactory()}
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableNullShouldFail() throws IOException {
        newProvider().getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableEmptyShouldFail() throws IOException {
        newProvider().getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableFileSeparatorFail() throws IOException {
        newProvider().getTable("azaza" + File.pathSeparator + "azaza");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableNullShouldFail() throws IOException {
        newProvider().createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableEmptyShouldFail() throws IOException {
        newProvider().createTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableFileSeparatorFail() throws IOException {
        newProvider().createTable("azaza" + File.pathSeparator + "azaza");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableNullShouldFail() throws IOException {
        newProvider().removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableEmptyShouldFail() throws IOException {
        newProvider().removeTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableFileSeparatorFail() throws IOException {
        newProvider().removeTable("azaza" + File.pathSeparator + "azaza");
    }

    TableProvider newProvider() throws IOException {
        return factory.create(temp.newFolder().getAbsolutePath());
    }
}
