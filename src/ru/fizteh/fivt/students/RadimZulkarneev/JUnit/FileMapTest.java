package ru.fizteh.fivt.students.RadimZulkarneev.JUnit;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ru.fizteh.fivt.students.RadimZulkarneev.DataBase.FileMap;

public class FileMapTest {
    private Path testDirectory;
    /*
     * Не советую менять эти четыре параметра, в ином случае придется подбирать соответствующие ключ и значения.
     */
    private String dirName = "02";
    private String fileName = "07";
    private String key = "asd";
    private String value = "value";

    private Path fileMapPath;
    private FileMap fileMap;

    @Before
    public void setUp() {
        testDirectory = Paths.get(System.getProperty("java.io.tmpdir"), dirName + ".dir");
        testDirectory.toFile().mkdir();
        fileMapPath = testDirectory.resolve(fileName + ".dat");
    }

    @Test
    public void fileMapCreatingNewFileAndAssertingOnTheirExisting() throws Exception {
        fileMap = new FileMap(fileMapPath);
        assertEquals(fileMap.getPath().toFile().exists(), true);
        fileMap.close();
    }

    @Test
    public void fileMapLoadFromExistingFile() throws Exception {
        Files.deleteIfExists(fileMapPath);
        Files.createFile(fileMapPath);
        fileMap = new FileMap(fileMapPath);
        fileMap.close();
    }

    @Test
    public void fileMapGetFunctionTest() throws Exception {
        fileMap = new FileMap(fileMapPath);
        fileMap.clear();
        assertEquals(null, fileMap.get(key));
        fileMap.put(key, value);
        assertEquals(value, fileMap.get(key));
        fileMap.close();
    }

    @Test
    public void fileMapSizeFuntion() throws Exception {
        fileMap = new FileMap(fileMapPath);
        assertEquals(0, fileMap.size());
        fileMap.put(key, value);
        assertEquals(1, fileMap.size());
        fileMap.close();
    }

    @Test(expected = RuntimeException.class)
    public void fileMapIsValidCorrectFunctionTest() throws Exception {
        fileMap = new FileMap(fileMapPath);
        fileMap.put(key + "lvalue", value);
    }

    @Test
    public void fileMapLoadFromNonEmptyFile() throws Exception {
        fileMap = new FileMap(fileMapPath);
        fileMap.put(key, value);
        fileMap.close();
        assertEquals(true, fileMapPath.toFile().exists());
        fileMap = new FileMap(fileMapPath);
        fileMap.close();

    }

    @Test
    public void fileMapCodeFunctionAssertion() throws Exception {
        fileMap = new FileMap(fileMapPath);
        assertEquals(dirName + fileName, fileMap.fileMapCode());
        fileMap.close();
    }

    @Test
    public void fileMapRemoveNonExistentKey() throws Exception {
        fileMap = new FileMap(fileMapPath);
        assertEquals(null, fileMap.remove(key + "lvalue"));
        //assertEquals(false, fileMap.remove(key + "lvalue", value));
        fileMap.close();
    }

    @Test
    public void fileMapRemoveExistentKeyTest() throws Exception {
        fileMap = new FileMap(fileMapPath);
        fileMap.put(key, value);
        assertEquals(value, fileMap.remove(key));
        fileMap.put(key, value);
        assertEquals(true, fileMap.remove(key, value));
        fileMap.close();
    }

    @Test
    public void fileMapClearFunctionTest() throws Exception {
        fileMap = new FileMap(fileMapPath);
        fileMap.clear();
        fileMap.put(key, value);
        assertNotEquals(0, fileMap.size());
        fileMap.clear();
        assertEquals(0, fileMap.size());
        fileMap.close();
    }

    @Test
    public void fileMapIsEmptyFunctionTest() throws Exception {
        fileMap = new FileMap(fileMapPath);
        fileMap.clear();
        assertEquals(true, fileMap.isEmpty());
        fileMap.put(key, value);
        assertEquals(false, fileMap.isEmpty());
        fileMap.close();
    }

    @Test
    public void fileMapContainsKeyFunctionTest() throws Exception {
        fileMap = new FileMap(fileMapPath);
        fileMap.clear();
        fileMap.put(key, value);
        assertEquals(true, fileMap.containsKey(key));
        fileMap.remove(key);
        assertEquals(false, fileMap.containsKey(key));
        fileMap.close();
    }

    @Test
    public void fileMapContainsValueFunctionTest() throws Exception {
        fileMap = new FileMap(fileMapPath);
        fileMap.clear();
        fileMap.put(key, value);
        assertEquals(true, fileMap.containsValue(value));
        fileMap.remove(key);
        assertEquals(false, fileMap.containsValue(value));
        fileMap.close();
    }

    @Ignore
    @Test
    public void fileMapValuesFunctionTest() throws Exception {
        Collection<String> values = new ArrayList<>();
        values.add(value);
        fileMap = new FileMap(fileMapPath);
        fileMap.clear();
        fileMap.put(key, value);
        Collection<String> retValues = fileMap.values();
        assertEquals(true, retValues.containsAll(values));
        fileMap.close();
    }

    @Test
    public void fileMapReplaceFunctionTest() throws Exception {
        fileMap = new FileMap(fileMapPath);
        fileMap.clear();
      //  assertEquals(null, fileMap.replace(key, value));
      //  assertEquals(false, fileMap.replace(key, value, value));
        fileMap.put(key, value);
     //   assertEquals(value, fileMap.replace(key, value));
      //  assertEquals(true, fileMap.replace(key, value, value));
        fileMap.close();
    }

    @Test
    public void fileMapKeySetFunctionTest() throws Exception {
        fileMap = new FileMap(fileMapPath);
        Set<String> set = new HashSet<>();
        fileMap.clear();
        Set<String> retSet = fileMap.keySet();
        assertEquals(true, set.containsAll(retSet));
        set.add(key);
        fileMap.put(key, value);
        retSet = fileMap.keySet();
        assertEquals(true, set.containsAll(retSet));
        fileMap.close();
    }

    @Test
    public void fileMapEntrySetFunctionTest() throws Exception {
        Set<Entry<String, String>> set = new HashSet<>();
        fileMap = new FileMap(fileMapPath);
        fileMap.clear();
        Set<Entry<String, String>> retSet = fileMap.entrySet();
        assertEquals(true, retSet.containsAll(set));
        fileMap.put(key, value);
        retSet = fileMap.entrySet();
        for (Entry<String, String> entry : retSet) {
            assertEquals(true, entry.getKey().equals(key) && entry.getValue().equals(value));
        }
        fileMap.close();
    }

    @After
    public void tearDown() throws Exception {
        fileMap.close();
        fileMapPath.toFile().delete();
        testDirectory.toFile().delete();
    }
}
