package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Some JUnit tests on TableProvider interface.
 * Created by andrew on 01.11.14.
 */

public class TestCurrentTableProvider {
    private TableProviderFactory factory = new CurrentTableProviderFactory();

    @Test(expected = IllegalArgumentException.class)
    public void getTableNull() throws IOException {
        newProvider().getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableEmpty() throws IOException {
        newProvider().getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableNull() throws IOException {
        newProvider().createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableEmpty() throws IOException {
        newProvider().createTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableNull() throws IOException {
        newProvider().removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableEmpty() throws IOException {
        newProvider().removeTable("");
    }

    @Test
    public void createTableNotNull() throws IOException {
        Assert.assertNotNull(newProvider().createTable("newTable"));
    }

    @Test
    public void createExistingTable() throws IOException {
        TableProvider provider = newProvider();
        Assert.assertNotNull(provider.createTable("newTable"));
        Assert.assertNull(provider.createTable("newTable"));
    }

    @Test
    public void getRemovedTable() throws IOException {
        TableProvider provider = newProvider();
        Assert.assertNotNull(provider.createTable("newTable"));
        provider.removeTable("newTable");
        Assert.assertNull(provider.getTable("newTable"));
    }

    @Test
    public void getNonExistingTable() throws IOException, IllegalArgumentException {
        Assert.assertNull(newProvider().getTable("NotExists"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNonExistingTable() throws IOException {
        newProvider().removeTable("NotExists");
    }

    @After
    public void clearFolder() {
        File testDir = new File(System.getProperty("fizteh.db.dir"));
        for (File curDir : testDir.listFiles()) {
            for (File file : curDir.listFiles()) {
                file.delete();
            }
            curDir.delete();
        }
        testDir.delete();
    }

    TableProvider newProvider() throws IOException {
        return factory.create(System.getProperty("fizteh.db.dir"));
    }
}
