package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.DBTableProvider;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Shell;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ConvenientMap;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class UtilityTest {
    private static final String QUOTED_STRING_REGEX = Utility.getQuotedStringRegex("\"", "/");
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static String[] doSplitString(String string) {
        try {
            return Shell.splitCommandsString(string).get(0);
        } catch (ParseException exc) {
            throw new RuntimeException(exc);
        }
    }

    private static int findQuotesEnd(String s) {
        try {
            return Utility.findClosingQuotes(s, 0, s.length(), '\"', '/');
        } catch (ParseException exc) {
            throw new RuntimeException(exc);
        }
    }

    private static String quoteString(String s) {
        return Utility
                .quoteString(s, DBTableProvider.QUOTE_CHARACTER + "", DBTableProvider.ESCAPE_CHARACTER + "");
    }

    private static String unquoteString(String s) {
        return Utility.unquoteString(
                s, DBTableProvider.QUOTE_CHARACTER + "", DBTableProvider.ESCAPE_CHARACTER + "");
    }

    private static String[] supplyArray(String... strings) {
        return strings;
    }

    @Test
    public void testMatchQuotedStringRegex() {
        assertFalse("\"/\"".matches(QUOTED_STRING_REGEX));
    }

    @Test
    public void testMatchQuotedStringRegex1() {
        assertTrue("\"//\"".matches(QUOTED_STRING_REGEX));
    }

    @Test
    public void testMatchQuotedStringRegex2() {
        assertFalse("\"//\"\"".matches(QUOTED_STRING_REGEX));
    }

    @Test
    public void testSplitString() {
        assertArrayEquals(
                "Invalid string split", supplyArray("create", "table"), doSplitString("create table"));
    }

    @Test
    public void testSplitString1() {
        assertArrayEquals(
                "Invalid string split",
                supplyArray("create", "table", "[", "col1,", "col2", "]"),
                doSplitString("create table [ col1, col2 ]"));
    }

    @Test
    public void testSplitString2() {
        assertArrayEquals(
                "Invalid string split",
                supplyArray("create", "table", "\"string\""),
                doSplitString("create table \"string\""));
    }

    @Test
    public void testSplitString3() {
        String part = quoteString("\"difficult argument/\"");
        assertArrayEquals(
                "Invalid string split",
                supplyArray("create", "table", "[", part, "]"),
                doSplitString("create table [ " + part + " ]"));
    }

    @Test
    public void testSplitString4() {
        String part = quoteString("\"a\"");
        assertArrayEquals(
                "Invalid string split",
                supplyArray("create", "table", "(" + part + ")"),
                doSplitString("create table (" + part + ")"));
    }

    @Test
    public void testSplitString5() {
        String part = quoteString("\"/ word \"/\"");
        assertArrayEquals(
                "Invalid string split",
                supplyArray("create", "table", "(" + part + "," + "1,", "null", ")"),
                doSplitString("create table (" + part + ",1, null " + ")"));
    }

    @Test
    public void testFindQuotesEnd() {
        assertEquals(4, findQuotesEnd("word\""));
    }

    @Test
    public void testFindQuotesEnd1() {
        assertEquals(-1, findQuotesEnd("word/\""));
    }

    @Test
    public void testFindQuotesEnd2() {
        assertEquals(-1, findQuotesEnd("/\"word///\"//"));
    }

    @Test
    public void testFindQuotesEndWithIllegalEscapedSymbol() throws ParseException {
        exception.expect(ParseException.class);
        exception.expectMessage(CoreMatchers.containsString("Unexpected escaped symbol"));

        Utility.findClosingQuotes("/e \"", 0, 4, '\"', '/');
    }

    @Test
    public void testUnquoteStringWithoutQuotes() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("String must be in quotes");

        Utility.unquoteString("no quotes", "\"", "/");
    }

    @Test
    public void testInverseMapWithTwoDuplicateValues() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Source map contains at least two duplicate values");

        Utility.inverseMap(
                new ConvenientMap<Integer, Integer>(new HashMap<Integer, Integer>()).putNext(1, 2).putNext(
                        2, 2));
    }

    @Test
    public void testInverseMap() {
        Map<Integer, Integer> map = new HashMap<>();

        map.put(1, 2);
        map.put(2, 3);
        map.put(3, 1);

        Map<Integer, Integer> inversedMap = Utility.inverseMap(map);

        assertEquals(map.size(), inversedMap.size());
        assertEquals((Integer) 3, inversedMap.get(1));
        assertEquals((Integer) 2, inversedMap.get(3));
        assertEquals((Integer) 1, inversedMap.get(2));
    }

    @Test
    public void testCheckAllTypesAreSupported() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(
                TestBase.wrongTypeMatcherAndAllOf(
                        CoreMatchers.containsString(Integer.class.getSimpleName() + " is not supported")));

        Utility.checkAllTypesAreSupported(
                Arrays.asList(Short.class, Integer.class), Arrays.asList(String.class, Short.class));
    }
}
