package ru.fizteh.fivt.students.alexpodkin.Tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.alexpodkin.Storeable.StoreableTableProviderFactory;

import java.io.IOException;

/**
 * Created by Alex on 25.11.14.
 */
public class StoreableTableProviderFactoryTest {
    private static TableProviderFactory tableProviderFactory;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        tableProviderFactory = new StoreableTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull() throws IOException {
        tableProviderFactory.create(null);
    }

    @Test
    public void create() throws IOException {
        tableProviderFactory.create("db");
    }
}
