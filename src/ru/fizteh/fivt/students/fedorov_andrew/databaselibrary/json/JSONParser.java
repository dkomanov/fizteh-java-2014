package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json;

import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONHelper.*;

/**
 * Powerful class that lets you parse json strings and pretend them as {@link
 * ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONParsedObject}.<br/>
 * Note that cyclic links are not parsed!
 */
public final class JSONParser {
    private JSONParser() {
    }

    private static List<Token> findTokens(String s, int begin, int end) throws ParseException {
        boolean inQuotes = false;

        /*
         * get: indicates that the symbol at current (index) position is 100% not escaped/escaped
         * set: indicates that the symbol at next (index + 1) position is 100% not escaped/escaped
         */
        boolean noEscape = true;

        List<Token> tokens = new LinkedList<>();

        // Depth level.
        int level = 0;

        for (int index = begin; index < end; index++) {
            switch (s.charAt(index)) {
            case QUOTES: {
                // True quotes.
                if (noEscape || (index > begin && s.charAt(index - 1) != ESCAPE_SYMBOL)) {
                    tokens.add(new Token(TokenType.QUOTES, index, level));
                    inQuotes = !inQuotes;
                } else {
                    // Escaped quotes.
                    noEscape = true;
                }
                break;
            }
            case ESCAPE_SYMBOL: {
                if (!inQuotes) {
                    throw new ParseException("Unexpected symbol " + ESCAPE_SYMBOL + " in " + s, index);
                }

                noEscape = !noEscape;
                break;
            }
            case OPENING_SQUARE_BRACE: {
                noEscape = true;
                if (!inQuotes) {
                    tokens.add(new Token(TokenType.ARRAY_START, index, level));
                    level++;
                }
                break;
            }
            case CLOSING_SQUARE_BRACE: {
                noEscape = true;
                if (!inQuotes) {
                    level--;
                    tokens.add(new Token(TokenType.ARRAY_END, index, level));
                }
                break;
            }
            case OPENING_CURLY_BRACE: {
                noEscape = true;
                if (!inQuotes) {
                    tokens.add(new Token(TokenType.RECORD_START, index, level));
                    level++;
                }
                break;
            }
            case CLOSING_CURLY_BRACE: {
                noEscape = true;
                if (!inQuotes) {
                    level--;
                    tokens.add(new Token(TokenType.RECORD_END, index, level));
                }
                break;
            }
            case KEY_VALUE_SEPARATOR: {
                noEscape = true;
                if (!inQuotes) {
                    tokens.add(new Token(TokenType.KEY_VALUE_SPLITTER, index, level));
                }
                break;
            }
            case ELEMENT_SEPARATOR: {
                noEscape = true;
                if (!inQuotes) {
                    tokens.add(new Token(TokenType.ELEMENT_SEPARATOR, index, level));
                }
                break;
            }
            default: {
                noEscape = true;
                break;
            }
            }
        }

        if (inQuotes) {
            throw new ParseException("Unclosed quotes", -1);
        }

        return tokens;
    }

    private static List<Integer> getElementSeparators(ArrayList<Token> tokens, int startTokenIndex) {
        Token start = tokens.get(startTokenIndex);
        int searchLevel = start.level + 1;

        List<Integer> elementSeparators = new LinkedList<>();

        for (int i = startTokenIndex + 1, len = tokens.size(); i < len; i++) {
            Token t = tokens.get(i);
            if (t.level == searchLevel && t.type == TokenType.ELEMENT_SEPARATOR) {
                elementSeparators.add(i);
            }
            if (t.level < searchLevel) {
                return elementSeparators;
            }
        }
        return elementSeparators;
    }

    private static Object jsonObjectDeepGet(JSONParsedObject source, Object... pathPieces)
            throws IllegalArgumentException {
        Object current = source;
        for (int i = 0; i < pathPieces.length; i++) {
            if (!(current instanceof JSONParsedObject)) {
                throw new IllegalArgumentException(
                        "Path piece #" + i + " cannot be applied to simple object");
            }
            if (pathPieces[i] instanceof String) {
                current = ((JSONParsedObject) current).get((String) pathPieces[i]);
            } else if (pathPieces[i] instanceof Integer) {
                current = ((JSONParsedObject) current).get((Integer) pathPieces[i]);
            } else {
                throw new IllegalArgumentException("Path pieces can only be strings or integers");
            }
        }
        return current;
    }

    /**
     * Parses the given json string and constructs {@link ru.fizteh.fivt.students.fedorov_andrew
     * .databaselibrary.json.JSONParsedObject}.<br/>
     * You can use escape symbol '\' to escape quotes and this symbol itself. Applying escape symbol to any
     * other symbol does not affect anything.
     * @param json
     *         json string. Must have root element: object or array.
     * @return constructed object. Cannot be null.
     * @throws java.text.ParseException
     */
    public static JSONParsedObject parseJSON(String json) throws ParseException {
        ArrayList<Token> tokens = new ArrayList<>(findTokens(json, 0, json.length()));
        ArrayDeque<ParsingObject> dq = new ArrayDeque<>();

        List<Integer> rootElementSeparators = getElementSeparators(tokens, 0);
        rootElementSeparators.add(0, 0);
        rootElementSeparators.add(tokens.size() - 1);

        dq.addLast(
                new ParsingObject(
                        null, tokens.get(0).type == TokenType.ARRAY_START, rootElementSeparators));

        JSONParsedObject root = null;

        while (!dq.isEmpty()) {
            // Get currently parsed object. Through this iteration we will parse on of its fields/elements.
            // It is like dfs but plain :)
            ParsingObject currentlyParsingObject = dq.getLast();

            // Object building over -> go one level higher.
            if (currentlyParsingObject.currentElementSeparatorID + 1 >= currentlyParsingObject.dataSplitters
                    .size()) {
                // Polling this object. It is parsed now!.
                dq.pollLast();
                if (dq.isEmpty()) {
                    // Congratulations, we have parsed the root object.
                    root = currentlyParsingObject.object;
                } else {
                    // We have parsed some complex object, but it is not over! We must assign it to its
                    // parent and continue with parsing the parent.
                    ParsingObject parent = dq.getLast();
                    parent.putSafely(currentlyParsingObject.name, currentlyParsingObject.object);
                }
                continue;
            }

            // Indices in 'tokens' array of this element boundaries.
            int dataStartTokenID = currentlyParsingObject.dataSplitters
                    .get(currentlyParsingObject.currentElementSeparatorID);
            int dataEndTokenID = currentlyParsingObject.dataSplitters
                    .get(currentlyParsingObject.currentElementSeparatorID + 1);
            currentlyParsingObject.currentElementSeparatorID++;

            /*
             * dataStartTokenID + valueOffset = first possible value token.
             */
            int valueOffset = 1;
            /*
             * Object/Field name. It will be null, if we are in array now.
             */
            String key = null;

            if (!currentlyParsingObject.object.isStandardArray()) {
                valueOffset = 4;
                int nameOpeningQuotesID = dataStartTokenID + 1;
                int nameClosingQuotesID = nameOpeningQuotesID + 1;
                if (nameClosingQuotesID >= tokens.size()) {
                    throw new ParseException("Quotes for field name are not found", dataStartTokenID + 1);
                }

                Token nameOpeningQuotes = tokens.get(nameOpeningQuotesID);
                Token nameClosingQuotes = tokens.get(nameClosingQuotesID);
                if (nameOpeningQuotes.type != TokenType.QUOTES
                    || nameClosingQuotes.type != TokenType.QUOTES) {
                    throw new ParseException(
                            "Quotes are expected in positions " + nameOpeningQuotes.index + ", "
                            + nameClosingQuotes.index + ", but found " + nameOpeningQuotes.type + " and "
                            + nameClosingQuotes.type, nameOpeningQuotes.index);
                }

                //name, if this is not array
                key = json.substring(nameOpeningQuotes.index + 1, nameClosingQuotes.index);

                int keyValueSplitterIndex = nameClosingQuotesID + 1;
                if (keyValueSplitterIndex >= tokens.size()) {
                    throw new ParseException(
                            "Name-value splitter symbol not found after name", nameClosingQuotes.index + 1);
                }

                Token keyValueSplitter = tokens.get(keyValueSplitterIndex);
                if (keyValueSplitter.type != TokenType.KEY_VALUE_SPLITTER) {
                    throw new ParseException(
                            "Key-value splitter is expected, but found " + keyValueSplitter.type,
                            keyValueSplitter.index);
                }
            }

            int valueStartTokenID = dataStartTokenID + valueOffset;
            int valueEndTokenID = dataEndTokenID - 1;

            Token valueStartToken = tokens.get(valueStartTokenID);
            Token valueEndToken = tokens.get(valueEndTokenID);

            // No value tokens. expected type of value: null, number, boolean.
            if (valueStartTokenID == dataEndTokenID) {
                int valueStartIndex = valueEndToken.index + 1;
                int valueEndIndex = tokens.get(dataEndTokenID).index;

                Object value;
                String valueString = json.substring(valueStartIndex, valueEndIndex).trim().toLowerCase();

                switch (valueString) {
                case "": {
                    throw new ParseException("Empty elements are not allowed in json", -1);
                }
                case NULL: {
                    value = null;
                    break;
                }
                case TRUE: {
                    value = Boolean.TRUE;
                    break;
                }
                case FALSE: {
                    value = Boolean.FALSE;
                    break;
                }
                default: {
                    if (valueString.contains(".")) {
                        value = Double.parseDouble(valueString);
                    } else {
                        value = Long.parseLong(valueString);
                    }
                    break;
                }
                }

                currentlyParsingObject.putSafely(key, value);
            } else if (valueStartToken.type == TokenType.QUOTES && valueEndToken.type == TokenType.QUOTES) {
                // Quotes. Expected type of value: string.
                if (valueStartTokenID + 1 != valueEndTokenID) {
                    Token extraToken = tokens.get(valueStartTokenID + 1);
                    throw new ParseException(
                            "Extra token between positions " + valueStartToken.index + " and "
                            + valueEndToken.index + ": " + extraToken.type, extraToken.index);
                }
                int valueStart = tokens.get(valueStartTokenID).index + 1;
                int valueEnd = tokens.get(valueEndTokenID).index;

                String value = unescape(json.substring(valueStart, valueEnd));
                currentlyParsingObject.putSafely(key, value);
            } else {
                // Some complex data: record or array.
                // Checking tokens are coupled and correct.
                switch (valueStartToken.type) {
                case ARRAY_START: {
                    if (valueEndToken.type != TokenType.ARRAY_END) {
                        throw new ParseException(
                                "Bad closing token for array: " + valueEndToken.type, valueEndToken.index);
                    }
                    break;
                }
                case RECORD_START: {
                    if (valueEndToken.type != TokenType.RECORD_END) {
                        throw new ParseException(
                                "Bad closing token for record: " + valueEndToken.type, valueEndToken.index);
                    }
                    break;
                }
                default: {
                    throw new ParseException(
                            "Bad record/array opening token: " + valueStartToken.type, valueStartToken.index);
                }
                }

                List<Integer> elementSeparators = getElementSeparators(tokens, valueStartTokenID);
                elementSeparators.add(0, valueStartTokenID);
                elementSeparators.add(valueEndTokenID);

                ParsingObject next = new ParsingObject(
                        key, tokens.get(valueStartTokenID).type == TokenType.ARRAY_START, elementSeparators);
                dq.addLast(next);
            }
        }

        return root;
    }

    private static enum TokenType {
        KEY_VALUE_SPLITTER,
        ELEMENT_SEPARATOR,
        RECORD_START,
        RECORD_END,
        ARRAY_START,
        ARRAY_END,
        QUOTES,
    }

    private static class Token {
        final TokenType type;
        final int index;
        /**
         * Host object depth in structure.
         */
        final int level;

        public Token(TokenType type, int index, int level) {
            this.type = type;
            this.index = index;
            this.level = level;
        }

        @Override
        public String toString() {
            return String.format("{type = %s, index = %d, level = %d}", type, index, level);
        }
    }

    @JSONComplexObject(singleField = true)
    private static class MapObject implements JSONParsedObject {
        @JSONField
        private final Map<String, Object> map;

        public MapObject(int size) {
            map = new HashMap<>(size);
        }

        @Override
        public void put(String key, Object value) {
            map.put(key, value);
        }

        @Override
        public void put(int index, Object value) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get(String name) {
            if (!map.containsKey(name)) {
                throw new IllegalArgumentException("Field missing: " + name);
            }
            return map.get(name);
        }

        @Override
        public Object get(int index) {
            throw new UnsupportedOperationException("This operation not supported in associative arrays");
        }

        @Override
        public boolean isStandardArray() {
            return false;
        }

        @Override
        public boolean containsField(String name) {
            return map.containsKey(name);
        }

        @Override
        public Object deepGet(Object... namePieces) {
            return jsonObjectDeepGet(this, namePieces);
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public Map<String, Object> asMap() {
            return map;
        }

        @Override
        public Object[] asArray() {
            throw new UnsupportedOperationException("Not supported in associative arrays");
        }

        @Override
        public String toString() {
            return map.toString();
        }
    }

    @JSONComplexObject(singleField = true)
    private static class ArrayObject implements JSONParsedObject {
        @JSONField
        private final Object[] array;

        public ArrayObject(int length) {
            this.array = new Object[length];
        }

        @Override
        public void put(String key, Object value) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void put(int index, Object value) throws UnsupportedOperationException {
            array[index] = value;
        }

        @Override
        public Object get(String key) {
            throw new UnsupportedOperationException("This operation not supported in standard arrays");
        }

        @Override
        public Object get(int index) {
            return array[index];
        }

        @Override
        public boolean containsField(String name) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isStandardArray() {
            return true;
        }

        @Override
        public Object deepGet(Object... namePieces) {
            return jsonObjectDeepGet(this, namePieces);
        }

        @Override
        public int size() {
            return array.length;
        }

        @Override
        public String toString() {
            return Arrays.toString(array);
        }

        @Override
        public Map<String, Object> asMap() {
            throw new UnsupportedOperationException("Not supported in standard arrays");
        }

        @Override
        public Object[] asArray() {
            return array;
        }
    }

    /**
     * Represents json object in stage of parsing.
     */
    private static class ParsingObject {
        /**
         * Incomplete object. Some fields/elements can be not set.
         */
        final JSONParsedObject object;
        /**
         * begin token index + data splitters indices + end token index
         */
        final List<Integer> dataSplitters;
        /**
         * Name of the object.
         */
        final String name;
        /**
         * Index of element separator after which we are now.
         */
        int currentElementSeparatorID;
        /**
         * Index of first not set element (in case of array).
         */
        int index;

        /**
         * @param name
         *         name of object
         * @param isArray
         *         if the object represents a standard array or not.
         * @param dataSplitters
         *         list of data splitting tokens
         */
        public ParsingObject(String name, boolean isArray, List<Integer> dataSplitters) {
            this.name = name;
            this.dataSplitters = dataSplitters;
            this.currentElementSeparatorID = 0;
            this.index = 0;

            if (isArray) {
                this.object = new ArrayObject(this.dataSplitters.size() - 1);
            } else {
                this.object = new MapObject(this.dataSplitters.size() - 1);
            }
        }

        /**
         * Put the value to named field of the object (in case of map) or set next element (in case of
         * array).
         */
        void putSafely(String key, Object value) {
            if (object.isStandardArray()) {
                if (key != null) {
                    throw new IllegalArgumentException("In case of array key must be null");
                }
                object.put(index++, value);
            } else {
                object.put(key, value);
            }
        }
    }
}
