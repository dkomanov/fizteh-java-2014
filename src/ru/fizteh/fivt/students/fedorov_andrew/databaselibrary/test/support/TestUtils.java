package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.DBTableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class provides some utility methods for testing.<br/> Note that some methods are linking to
 * {@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility} class. The
 * purpose is that test classes should not contact with utility methods from the main sources
 * directly.
 */
public class TestUtils {
    public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final Random RANDOM = new Random();

    // Not for constructing
    private TestUtils() {
    }

    public static int randInt(int a, int b) {
        return RANDOM.nextInt(b - a + 1) + a;
    }

    public static int randInt(int n) {
        return RANDOM.nextInt(n);
    }

    public static String randString(int length) {
        char[] data = new char[length];
        for (int i = 0; i < length; i++) {
            data[i] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
        }
        return String.valueOf(data);
    }

    public static void consumeCPU(int actionsCount) {
        int sum = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < actionsCount; i++) {
            sum += random.nextInt(123512);
        }
        random.nextInt(sum);
    }

    public static TableProviderFactory obtainFactory() {
        return new DBTableProviderFactory();
    }

    public static void removeFileSubtree(Path removePath) throws IOException {
        Utility.rm(removePath);
    }

    public static <T> T randElement(Collection<T> set) {
        int keysCount = set.size();
        if (keysCount == 0) {
            return null;
        }
        int keyID = TestUtils.randInt(keysCount);

        Iterator<T> iterator = set.iterator();
        while (keyID > 0) {
            iterator.next();
            keyID--;
        }

        return iterator.next();
    }

    public static <K, V> int countDifferences(Map<K, V> source, Map<K, V> target) {
        return Utility.countDifferences(source, target);
    }
}
