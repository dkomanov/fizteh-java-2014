package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Provides java regular expression matching.<br/>
 * For a non-string object method {@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test
 * .support.RegexMatcher#matches(Object)}
 * returns false.
 * @see String#matches(String)
 */
public class RegexMatcher extends BaseMatcher<String> {
    private final String regex;

    public RegexMatcher(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof String) {
            String s = (String) item;
            return s.matches(regex);
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(
                "matches " + regex);
    }
}
