package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.RegexMatcher;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;

/**
 * Base test class for tests on database functionality.
 */
public abstract class TestBase {
    public static final String WRONG_TYPE_MESSAGE_REGEX = "wrong type \\([^\\(\\)]+\\)";

    /**
     * Accepts strings of this type: "wrong type ( ...description without round brackets... )"
     */
    public static final Matcher<String> WRONG_TYPE_MESSAGE_MATCHER =
            new RegexMatcher(WRONG_TYPE_MESSAGE_REGEX);
    protected static final Path DB_ROOT = Paths.get(System.getProperty("user.home"), "test", "JUnitDB");
    static final String INT_TYPE = Integer.class.getSimpleName();
    static final String STRING_TYPE = String.class.getSimpleName();
    static final String DOUBLE_TYPE = Double.class.getSimpleName();
    static final String FLOAT_TYPE = Float.class.getSimpleName();
    static final String BOOLEAN_TYPE = Boolean.class.getSimpleName();
    static final String LONG_TYPE = Long.class.getSimpleName();

    @BeforeClass
    public static void globalPrepareTestBase() throws IOException {
        if (Files.exists(DB_ROOT)) {
            Utility.rm(DB_ROOT, "JUnit Test: global prepare");
        }
    }

    public static Matcher<String> wrongTypeMatcherAndAllOf(Matcher<String>... matchers) {
        return allOf(WRONG_TYPE_MESSAGE_MATCHER, allOf(matchers));
    }

    protected void cleanDBRoot() throws IOException {
        if (Files.exists(DB_ROOT)) {
            TestUtils.removeFileSubtree(DB_ROOT);
        }
    }
}
