package ru.fizteh.fivt.students.LebedevAleksey.junit;

import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AdditionalAssert {
    public static void assertListEquals(List<?> expected, List<?> actual) {
        Iterator<?> expectedIterator = expected.iterator();
        Iterator<?> actualIterator = actual.iterator();
        while (expectedIterator.hasNext() && actualIterator.hasNext()) {
            Assert.assertEquals(expectedIterator.next(), actualIterator.next());
        }
        Assert.assertTrue("Collections contains different count of elements",
                expectedIterator.hasNext() == actualIterator.hasNext());
    }

    public static void assertArrayAndListEquals(Object[] expected, List<?> actual) {
        assertListEquals(Arrays.asList(expected), actual);
    }

    public static void assertStringArrayContainsSameItemsAsList(String[] expected, List<String> actual) {
        Arrays.sort(expected);
        Collections.sort(actual);
        assertArrayAndListEquals(expected, actual);
    }
}
