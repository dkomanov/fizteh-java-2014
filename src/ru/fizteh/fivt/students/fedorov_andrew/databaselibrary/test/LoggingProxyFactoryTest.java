package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONMaker;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONParsedObject;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONParser;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.LoggingProxyFactoryImpl;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.BAOSDuplicator;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class LoggingProxyFactoryTest {
    private static final String TIMESTAMP = "timestamp";
    private static final String CLASS = "class";
    private static final String METHOD = "method";
    private static final String ARGUMENTS = "arguments";
    private static final String THROWN = "thrown";
    private static final String RETURN_VALUE = "returnValue";

    /**
     * Matcher that always returns true.
     */
    private static final Matcher<Object> EXISTS_MATCHER = new BaseMatcher<Object>() {
        @Override
        public boolean matches(Object item) {
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("exists");
        }
    };
    /**
     * Requires presence of some sections and forbids situation when 'thrown' and 'returnValue" exist both.
     */
    private static final Matcher<JSONParsedObject> MIN_REQUIREMENTS = allOf(
            new LogPieceMatcher(TIMESTAMP, EXISTS_MATCHER),
            new LogPieceMatcher(CLASS, EXISTS_MATCHER),
            new LogPieceMatcher(METHOD, EXISTS_MATCHER),
            new LogPieceMatcher(ARGUMENTS, EXISTS_MATCHER),
            not(
                    allOf(
                            new LogPieceMatcher(THROWN, EXISTS_MATCHER),
                            new LogPieceMatcher(RETURN_VALUE, EXISTS_MATCHER))));
    private static LoggingProxyFactory factory;
    private final BAOSDuplicator out = new BAOSDuplicator(System.out);
    private final Writer writer = new OutputStreamWriter(out);
    private TestFace wrapped;

    private static Matcher<JSONParsedObject> makeRequirements(Matcher<JSONParsedObject>... restrictions) {
        return allOf(MIN_REQUIREMENTS, allOf(restrictions));
    }

    @BeforeClass
    public static void globalPrepare() {
        factory = new LoggingProxyFactoryImpl();
    }

    private String getOutput() {
        return out.toString();
    }

    @Before
    public void prepare() {
        out.reset();
        wrapped = (TestFace) factory.wrap(writer, new TestFaceImpl(), TestFace.class);
    }

    @Test
    public void testDoNothing() throws ParseException {
        wrapped.doNothing();
        JSONParsedObject parsed = JSONParser.parseJSON(getOutput());
        assertThat(parsed, makeRequirements());
    }

    @Test
    public void testBoolMethod() throws ParseException {
        wrapped.boolMethod(1, null);
        JSONParsedObject parsedObject = JSONParser.parseJSON(getOutput());
        assertThat(parsedObject, makeRequirements());
        assertThat(parsedObject.getObject(ARGUMENTS).asArray(), equalTo(new Object[] {1L, null}));
        assertThat(parsedObject, new LogPieceMatcher("returnValue", equalTo(Boolean.TRUE)));
    }

    @Test
    public void testThrowingMethod() throws ParseException {
        List<List<String>> iterable = new LinkedList<>();
        iterable.add(Arrays.asList("1_1", "1_2", "1_3"));
        iterable.add(Arrays.asList("2_1", "2_2"));
        iterable.add(null);

        try {
            wrapped.throwingMethod(iterable);
        } catch (Exception exc) {
            // Ignore it.
        }

        JSONParsedObject parsedObject = JSONParser.parseJSON(getOutput());

        assertThat(parsedObject, makeRequirements());
        assertEquals(
                JSONMaker.makeJSON(new Object[] {iterable}), JSONMaker.makeJSON(parsedObject.get(ARGUMENTS)));
    }

    @Test
    public void testEqualsNotProxied() {
        wrapped.equals(null);
        assertEquals("", getOutput());
    }

    private interface TestFace {
        default void doNothing() {
        }

        default boolean boolMethod(int a, Integer b) {
            return true;
        }

        default <T> String throwingMethod(Iterable<T> iterable) throws Exception {
            throw new Exception();
        }
    }

    private static class TestFaceImpl implements TestFace {

    }

    private static class LogPieceMatcher extends BaseMatcher<JSONParsedObject> {
        private final String fieldName;
        private final Matcher<Object> valueMatcher;

        public LogPieceMatcher(String fieldName, Matcher<Object> valueMatcher) {
            this.fieldName = fieldName;
            this.valueMatcher = valueMatcher;
        }

        @Override
        public boolean matches(Object item) {
            if (item instanceof JSONParsedObject) {
                JSONParsedObject obj = (JSONParsedObject) item;
                if (valueMatcher == null) {
                    return !obj.containsField(fieldName);
                } else {
                    return obj.containsField(fieldName) && valueMatcher.matches(obj.get(fieldName));
                }
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Field " + fieldName);
            if (valueMatcher == null) {
                description.appendText(" must not exist.");
            } else {
                description.appendText(" must match: ");
                description.appendDescriptionOf(valueMatcher);
            }
        }
    }
}
