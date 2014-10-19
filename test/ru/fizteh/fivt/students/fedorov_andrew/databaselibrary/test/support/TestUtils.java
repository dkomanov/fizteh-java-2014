package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.DBTableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

public class TestUtils {
    // Not for constructing
    private TestUtils() {
    }

    private static Random random = new Random();
    private final static char[] alphabet = "abcdefghijklmnopqrstuvwxyz"
	    .toCharArray();

    public static int randInt(int a, int b) {
	return random.nextInt(b - a + 1) + a;
    }

    public static int randInt(int n) {
	return random.nextInt(n);
    }

    public static String randString(int length) {
	char[] data = new char[length];
	for (int i = 0; i < length; i++) {
	    data[i] = alphabet[random.nextInt(alphabet.length)];
	}
	return String.valueOf(data);
    }

    public static TableProviderFactory obtainFactory() {
	return new DBTableProviderFactory();
    }

    public static void removeFileSubtree(Path removePath) throws IOException {
	Utility.rm(removePath, "JUnit Test");
    }

    public static <T> T randElement(Collection<T> set) {
	int keysCount = set.size();
	if (keysCount == 0) {
	    return null;
	}
	int keyID = TestUtils.randInt(keysCount);

	Iterator<T> iter = set.iterator();
	while (keyID > 0) {
	    iter.next();
	    keyID--;
	}

	T element = iter.next();
	return element;
    }
    
    public static <K, V> int countDifferences (Map<K, V> source, Map<K, V> target) {
	return Utility.countDifferences(source, target);
    }
}
