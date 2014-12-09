package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Tests;

import org.junit.After;
import org.junit.Test;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.StoreableTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StoreableTableProviderFactoryTest {
    private StoreableTableProviderFactory factory = new StoreableTableProviderFactory();

    @Test(expected = IllegalArgumentException.class)
    public void createNullProvider() throws IOException {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyProvider() throws IOException {
        factory.create("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUnavailableProvider() throws IOException {
        factory.create("C:\\Windows\\System32");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderInFile() throws IOException {
        File f = new File(System.getProperty("fizteh.db.dir") + File.separator + "a.txt");
        try {
            f.createNewFile();
        } catch (IOException e) {
            System.err.println("Can't create file " + f.getName());
            throw new IllegalArgumentException();
        }
        factory.create(f.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderInNotWritableDirectory() throws IOException {
        File tempfile = new File(System.getProperty("fizteh.db.dir") + "newTableThatNotExists");
        try {
            Files.createDirectory(tempfile.toPath());
        } catch (IOException e) {
            System.err.println("Can't create file " + tempfile.getName());
            throw new IllegalArgumentException();
        }
        if (!tempfile.setWritable(false)) {
            System.err.println("Can't change permission");
            throw new IllegalArgumentException();
        }
        factory.create(tempfile.getName());
    }

    @Test
    public void createProvider() throws IOException {
        factory.create(System.getProperty("fizteh.db.dir"));
    }

    @After
    public void clearAll() {
        try {
            File testDir = new File(System.getProperty("fizteh.db.dir"));
            for (File curDir : testDir.listFiles()) {
                for (File file : curDir.listFiles()) {
                    file.delete();
                }
                curDir.delete();
            }
            testDir.delete();
        } catch (NullPointerException e) {
            //if it happens, than it means, that we were trying to create something in other directory;
        }

    }
}