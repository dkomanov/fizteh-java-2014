package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

import org.junit.After;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Дмитрий on 04.11.2014.
 */
public class TestCurrentTableProviderFactory {
    private TableProviderFactory factory = new CurrentTableProviderFactory();

    @Test(expected = IllegalArgumentException.class)
    public void createNullProvider() {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyProvider() {
        factory.create("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUnavailableProvider() {
        factory.create("C:\\Windows\\System32");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderInFile() {
        File f = new File(System.getProperty("fizteh.db.dir") + "a.txt");
        try {
            f.createNewFile();
        } catch (IOException e) {
            System.err.println("Can't create file " + f.getName());
            throw new IllegalArgumentException();
        }
        factory.create(f.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderInNotWritableDirectory() {
        File f = new File(System.getProperty("fizteh.db.dir") + "newTableThatNotExists");
        try {
            Files.createDirectory(f.toPath());
        } catch (IOException e) {
            System.err.println("Can't create file " + f.getName());
            throw new IllegalArgumentException();
        }
        if (!f.setWritable(false)) {
            System.err.println("Can't change permission");
            throw new IllegalArgumentException();
        }
        factory.create(f.getName());
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
