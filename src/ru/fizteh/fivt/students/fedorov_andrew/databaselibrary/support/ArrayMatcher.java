package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.reflect.Array;

/**
 * Matcher for array that stores own matcher for each element.
 * @deprecated Developed but alternative way has been found for the moment.
 */
@Deprecated
class ArrayMatcher extends BaseMatcher<Object> {
    private final Matcher<Object>[] matchers;

    /**
     * Constructs array matcher from array of matchers.
     * @param matchers
     *         array of matchers. Note that this array is not copied, so changes are backed.
     */
    public ArrayMatcher(Matcher<Object>... matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean matches(Object item) {
        int length = Array.getLength(item);
        if (matchers.length != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (!matchers[i].matches(Array.get(item, i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Array must have length " + matchers.length + " and match: ");
        boolean comma = false;
        for (Matcher<Object> matcher : matchers) {
            if (comma) {
                description.appendText(", ");
            }
            comma = true;
            description.appendDescriptionOf(matcher);
        }
    }
}
