package ru.fizteh.fivt.students.dnovikov.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DataBaseProviderTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DbTestDir");
    private final File testFile = Paths.get(testDir.toString(), "TestFile").toFile();
    private final String dirName = "test";
    private final Path dirPath = testDir.resolve(dirName);

    public void setUp() throws IOException {
        Files.createDirectory(testDir);
    }

    @Test(expected = LoadOrSaveException.class)
    public void testDataBaseProviderThrowsExceptionOfNonexistentDirectory() {
        new DataBaseProvider(testDir.resolve(dirPath).toString());
    }

    @Test(expected = LoadOrSaveException.class)
    public void testDataBaseProviderThrowsExceptionCreatedNotDirectory() {
        new DataBaseProvider(testFile.toString());
    }

    @Test(expected = LoadOrSaveException.class)
    public void testDataBaseProviderThrowsExceptionCreatedInvalidPath() {
        new DataBaseProvider("invalidPath");
    }

    @Test(expected = LoadOrSaveException.class)
    public void testDataBaseProviderThrowsExceptionFileInDirectory() throws IOException {
        if (!testDir.toFile().exists()) {
            Files.createDirectory(testDir);
        }
        if (!dirPath.toFile().exists())
            Files.createDirectory(dirPath);

        dirPath.resolve("fileName").toFile().createNewFile();
        new DataBaseProvider(dirPath.toString());
    }

//    @Test(expected = IllegalArgumentException.class)
//    public void testDataBaseProviderThrowsExceptionCreateNullTableName() throws IOException {
//        if (!testDir.toFile().exists()) {
//            Files.createDirectory(testDir);
//        }
//        if (!dirPath.toFile().exists())
//            Files.createDirectory(dirPath);
//        TableProvider test = new DataBaseProvider(dirPath.toString());
//        test.createTable(null);
//    }

    @After
    public void tearDown() {

    }
}