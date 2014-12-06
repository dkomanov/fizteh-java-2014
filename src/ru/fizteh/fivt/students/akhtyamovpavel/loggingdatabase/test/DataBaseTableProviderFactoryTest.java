package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.DataBaseTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DataBaseTableProviderFactoryTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCreate() throws Exception {
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        try {
            factory.create(null);
            fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        folder.create();
        String folderName = folder.getRoot().toPath().toString();
        File result = folder.newFolder("test");
        try {
            factory.create(Paths.get(folderName, "test").toString(), true);
        } catch (Exception e) {
            fail();
        }

        assertNull(factory.create(Paths.get(folderName, "non_exist", "non_exist").toString(), true));
    }

    @Test
    public void testClose() {
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        try {
            folder = new TemporaryFolder();
            assertNotNull(factory.create(folder.toString()));
        } catch (IOException e) {
            fail();
        }
        try {
            factory.close();
        } catch (Exception e) {
            fail();
        }

        try {
            factory.create(folder.toString());
            fail();
        } catch (IOException e) {
            fail();
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }


}

