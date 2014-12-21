package ru.fizteh.fivt.students.PotapovaSofia.storeable.Tests;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.PotapovaSofia.storeable.DbTableProvider;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.DbTableProviderFactory;

public class DbTableProviderFactoryTest {
    private DbTableProviderFactory factory;
    private final Path testFolder = Paths.get(System.getProperty("java.io.tmpdir"), "testFolder");

    @Before
    public void setUp() {
        factory = new DbTableProviderFactory();
        testFolder.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnThrowsExceptionCreatedNullTableProvider() throws IOException {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testOnThrowsExceptionCreatedForInvalidPath() throws IOException {
        factory.create("\0");
    }
    
    @After
    public void tearDown() throws IOException {
        DbTableProvider.recoursiveDelete(testFolder.toFile());
    }
}
