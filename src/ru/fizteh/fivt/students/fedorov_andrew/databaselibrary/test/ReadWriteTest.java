package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.TablePart;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

import static org.junit.Assert.*;
import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils.*;

@RunWith(org.junit.runners.JUnit4.class)
public class ReadWriteTest {

    /**
     * One test for reading/writing map.
     * @param keysMin
     *         minimal count of keys
     * @param keysMax
     *         maximal count of keys
     * @param keyMinLength
     *         minimal length in characters of a key
     * @param keyMaxLength
     *         maximal length in characters of a key
     * @param valueMinLength
     *         minimal length in characters of a value
     * @param valueMaxLength
     *         maximal length in characters of a value
     * @throws Exception
     */
    private void performReadWriteFileMapTest(int keysMin,
                                             int keysMax,
                                             int keyMinLength,
                                             int keyMaxLength,
                                             int valueMinLength,
                                             int valueMaxLength) throws Exception {
        HashMap<String, String> map = new HashMap<>();

        int keysCount = randInt(keysMin, keysMax);

        for (int j = 0; j < keysCount; j++) {
            // duplicate key can be generated, but we do not mind.
            String key = randString(randInt(keyMinLength, keyMaxLength));
            String value = randString(randInt(valueMinLength, valueMaxLength));
            map.put(key, value);
        }

        Path testPath = Paths.get(System.getProperty("user.home"), "test", "java_test.dat");
        TablePart testFileMap = new TablePart(testPath);

        for (Entry<String, String> e : map.entrySet()) {
            testFileMap.put(e.getKey(), e.getValue());
        }

        testFileMap.writeToFile();

        testFileMap = new TablePart(testPath);
        testFileMap.readFromFile();

        assertEquals("Map sizes do not match", map.size(), testFileMap.size());

        for (String key : map.keySet()) {
            assertEquals("Values do not match", map.get(key), testFileMap.get(key));
        }
    }

    /**
     * Integrity test for reading/writing map.
     * @throws Exception
     */
    @org.junit.Test
    @Ignore
    public void testReadWriteFileMap() throws Exception {
        int tests = randInt(1000, 2000);

        int keysMin = 1;
        int keysMax = 10000;

        int keyMinLength = 1;
        int keyMaxLength = 1000;

        int valueMinLength = 1;
        int valueMaxLength = 1000;

        // no keys test
        performReadWriteFileMapTest(0, 0, 1, 2, 1, 2);

        // single key
        performReadWriteFileMapTest(1, 1, 1, 10, 1, 10);

        // standard tests
        for (int test = 0; test < tests; test++) {
            System.out.println("test " + test);
            performReadWriteFileMapTest(
                    keysMin, keysMax, keyMinLength, keyMaxLength, valueMinLength, valueMaxLength);
        }
    }
}
