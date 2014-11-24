package ru.fizteh.fivt.students.lukina.Tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.lukina.DataBase.DBaseProvider;
import ru.fizteh.fivt.students.lukina.DataBase.DBaseProviderFactory;

import java.io.IOException;

import static org.junit.Assert.*;

public class DBaseProviderFactoryTest {
    @Rule
    public TemporaryFolder root = new TemporaryFolder();
    DBaseProviderFactory test;

    @Before
    public void init() throws IOException {
        System.setProperty("fizteh.db.dir", root.newFolder().getAbsolutePath());
        test = new DBaseProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws IOException {
        test.create(null);
    }

    @Test
    public void testCreateNotExisting() throws IOException {
        test.create(root.newFolder().getAbsolutePath() + "not_existing_name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFile() throws IOException {
        test.create(root.newFile().getAbsolutePath());
    }

    @Test
    public void testCreate() throws IOException {
        assertNotNull(test.create(root.newFolder().getAbsolutePath()));
    }

    @Test(expected = IllegalStateException.class)
    public void testClose1() throws Exception {
        DBaseProviderFactory tmp = new DBaseProviderFactory();
        tmp.close();
        tmp.create(root.newFolder().toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testClose2() throws Exception {
        DBaseProviderFactory tmp = new DBaseProviderFactory();
        DBaseProvider prov = (DBaseProvider) tmp.create(root.newFolder()
                .toString());
        tmp.close();
        prov.createTable("table", null);
    }
}
