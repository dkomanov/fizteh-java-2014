package ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Test {
    private static Random random = new Random();
    private final static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    static int randInt(int a, int b) {
	return random.nextInt(b - a + 1) + a;
    }

    static String randString(int length) {
	char[] data = new char[length];
	for (int i = 0; i < length; i++) {
	    data[i] = alphabet[random.nextInt(alphabet.length)];
	}
	return String.valueOf(data);
    }

    /**
     * One test for reading/writing map.
     * 
     * @param keysMin
     *            minimal count of keys
     * @param keysMax
     *            maximal count of keys
     * @param keyMinLength
     *            minimal length in characters of a key
     * @param keyMaxLength
     *            maximal length in characters of a key
     * @param valueMinLength
     *            minimal length in characters of a value
     * @param valueMaxLength
     *            maximal length in characters of a value
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void performReadWriteFileMapTest(int keysMin, int keysMax,
	    int keyMinLength, int keyMaxLength, int valueMinLength,
	    int valueMaxLength) throws Exception {
	HashMap<String, String> map = new HashMap<>();

	int keysCount = randInt(keysMin, keysMax);

	for (int j = 0; j < keysCount; j++) {
	    // duplicate key can be generated, but we do not mind.
	    String key = randString(randInt(keyMinLength, keyMaxLength));
	    String value = randString(randInt(valueMinLength, valueMaxLength));
	    map.put(key, value);
	}

	Path testPath = Paths.get(System.getProperty("user.home"),
				  "test",
				  "java_test.dat");
	TablePart testFileMap = new TablePart(testPath);

	testFileMap.setTablePartMap((HashMap<String, String>) map.clone());

	testFileMap.writeToFile();
	testFileMap.setTablePartMap(new HashMap<String, String>());
	testFileMap.readFromFile();

	Map<String, String> checkMap = testFileMap.getTablePartMap();

	assertEquals("Map sizes do not match", map.size(), checkMap.size());

	for (String key : map.keySet()) {
	    assertEquals("Values do not match", map.get(key), checkMap.get(key));
	}
    }

    /**
     * Integrity test for reading/writing map.
     * 
     * @throws Exception
     */
    @org.junit.Test
    public void testReadWriteFileMap() throws Exception {
	int tests = random.nextInt(1000) + 1000;

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
	    performReadWriteFileMapTest(keysMin,
					keysMax,
					keyMinLength,
					keyMaxLength,
					valueMinLength,
					valueMaxLength);
	}
    }
}
