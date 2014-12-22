package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json;

/**
 * Contains constants and useful methods for json package.
 */
final class JSONHelper {
    static final char OPENING_CURLY_BRACE = '{';
    static final char CLOSING_CURLY_BRACE = '}';
    static final char OPENING_SQUARE_BRACE = '[';
    static final char CLOSING_SQUARE_BRACE = ']';
    static final char ELEMENT_SEPARATOR = ',';
    static final char KEY_VALUE_SEPARATOR = ':';
    static final char QUOTES = '\"';
    static final char ESCAPE_SYMBOL = '\\';
    static final String TRUE = "true";
    static final String FALSE = "false";
    static final String NULL = "null";
    static final String CYCLIC = "cyclic";

    private JSONHelper() {
    }

    static String escape(String s) {
        s = s.replace("\\", "\\\\");
        s = s.replace("\"", "\\\"");
        return s;
    }

    static String unescape(String s) {
        s = s.replace("\\\"", "\"");
        s = s.replace("\\\\", "\\");
        return s;
    }
}
