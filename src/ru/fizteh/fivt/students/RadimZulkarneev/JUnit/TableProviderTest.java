package ru.fizteh.fivt.students.RadimZulkarneev.JUnit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.RadimZulkarneev.DataBase.FileMap;
import ru.fizteh.fivt.students.RadimZulkarneev.DataBase.TableProviderRealize;

public class TableProviderTest {

    private Path testDirectory;
    private final String dataBaseName = "dataBase";
    private Path dataBasePath;
    private final String tableName = "table1";
    /*
     * Не советую менять эти четыре параметра, в ином случае придется подбирать соответствующие ключ и значения.
     */
    private String dirName = "02.dir";
    private String fileName = "07.dat";
    private String key = "asd";
    private String value = "value";

    @Before
    public void setUp() {
        testDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "testDir");
        testDirectory.toFile().mkdir();
        dataBasePath = testDirectory.resolve(dataBaseName);
        dataBasePath.toFile().mkdir();
    }

    @Test
    public void tableProviderCreateNewDataBaseTest() {
        new TableProviderRealize(dataBasePath.toString());
    }

    @Test
    public void tableProviderCreateDataBaseFromExistentDirectoryTest() {
        new TableProviderRealize(dataBasePath.toString());
        //dataBasePath.toFile().delete();
    }

    @Test(expected = IllegalStateException.class)
    public void tableProviderCreateFromNonDirectoryFile() throws IOException {
        Files.deleteIfExists(dataBasePath);
        dataBasePath.toFile().createNewFile();
        new TableProviderRealize(dataBasePath.toString());
    }

    @Test(expected = RuntimeException.class)
    public void tableProviderLoadDataBaseFromDirectoryWithNonDirectoryFile() throws IOException {
        dataBasePath.resolve(tableName).toFile().createNewFile();
        new TableProviderRealize(dataBasePath.toString());
        dataBasePath.resolve(tableName).toFile().delete();
    }


    @Test
    public void tableProviderLoadFromNonExistentDirectory() {
        dataBasePath.toFile().delete();
        new TableProviderRealize(dataBasePath.toString());
        assertTrue(dataBasePath.toFile().exists());
    }

    @Test
    public void tableProviderLoadFromCorrectDirectoryWithEmptyDirectoryFile() {
        dataBasePath.resolve(tableName).toFile().mkdir();
        TableProvider tableProvider = new TableProviderRealize(dataBasePath.toString());
        assertNotNull(tableProvider.getTable(tableName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tableProviderCreateNewTableFromNullPointerDirectory() {
        TableProvider tableProvider = new TableProviderRealize(dataBasePath.toString());
        tableProvider.createTable(null);
    }

    @Test
    public void tableProviderCreateNewTableFromCorrectTableName() {
        TableProvider tableProvider = new TableProviderRealize(dataBasePath.toString());
        tableProvider.createTable(tableName);
        assertTrue(dataBasePath.resolve(tableName).toFile().exists());
    }

    @Test
    public void tableProviderLoadFromNonEmptyDataBaseTest() throws Exception {
        dataBasePath.resolve(tableName).toFile().mkdir();
        dataBasePath.resolve(tableName).resolve(dirName).toFile().mkdir();
        Path fileMapPath = dataBasePath.resolve(tableName).resolve(dirName).resolve(fileName);
        FileMap fileMap = new FileMap(fileMapPath);
        fileMap.put(key, value);
        fileMap.close();
        TableProvider tableProvider = new TableProviderRealize(dataBasePath.toString());

        fileMapPath.toFile().delete();
        dataBasePath.resolve(tableName).resolve(dirName).toFile().delete();
        dataBasePath.resolve(tableName).toFile().delete();
    }

    @Test
    public void tableProviderRemoveExistentTable() throws Exception {
        dataBasePath.resolve(tableName).toFile().mkdir();
        dataBasePath.resolve(tableName).resolve(dirName).toFile().mkdir();
        Path fileMapPath = dataBasePath.resolve(tableName).resolve(dirName).resolve(fileName);
        FileMap fileMap = new FileMap(fileMapPath);
        fileMap.put(key, value);
        fileMap.close();
        TableProvider tableProvider = new TableProviderRealize(dataBasePath.toString());
        tableProvider.removeTable(tableName);
        fileMapPath.toFile().delete();
        dataBasePath.resolve(tableName).resolve(dirName).toFile().delete();
        dataBasePath.resolve(tableName).toFile().delete();
    }

    @Test(expected = IllegalStateException.class)
    public void tableProviderRemoveNonExistentTableTest() {
        TableProvider tableProvider = new TableProviderRealize(dataBasePath.toString());
        tableProvider.removeTable("nonExistentNamne");

    }

    @Test(expected = IllegalArgumentException.class)
    public void tableProviderRemoveMethodThrowsExceptionForNullArgumentTest() {
        TableProvider tableProvider = new TableProviderRealize(dataBasePath.toString());
        tableProvider.removeTable(null);
    }

    @After
    public void tearDown() throws Exception {
        for (File curFile : testDirectory.toFile().listFiles()) {
            if (curFile.isDirectory()) {
                for (File directoryFile : curFile.listFiles()) {
                    directoryFile.delete();
                }
            }
            curFile.delete();
        }
        testDirectory.toFile().delete();
    }
}
