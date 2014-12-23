package ru.fizteh.fivt.students.pavel_voropaev.project;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.JSONParseException;

import java.io.Closeable;

public class Parser implements Closeable {
    String string;
    int length;
    char delimiter;
    char escapeBegin;
    char escapeEnd;
    int index;

    public Parser(String string, char delimiter, char escape) {
        this(string, delimiter, escape, escape);
    }

    public Parser(String string, char delimiter, char escapeBegin, char escapeEnd) {
        this.string = string;
        length = string.length();
        this.delimiter = delimiter;
        this.escapeBegin = escapeBegin;
        this.escapeEnd = escapeEnd;
        index = 0;
    }

    public String getNext() throws JSONParseException {
        boolean empty = true;
        for (int right = index; right < length; ++right) {
            char current = string.charAt(right);
            if (current == delimiter) {
                if (empty) {
                    continue;
                }
                String value = string.substring(index, right).trim();
                index = right + 1;
                return value;
            }

            if (current == escapeBegin && empty) {
                right = string.indexOf(escapeEnd, right + 1);
                if (right == -1) {
                    throw new JSONParseException("Closing " + escapeEnd + " is not found", index);
                }
                String value = string.substring(index, right + 1).trim();
                index = right + 2;
                return value;
            }

            if (!Character.isSpaceChar(current)) {
                empty = false;
            }
        }
        if (empty) {
            return null;
        }

        String value = string.substring(index, length).trim();
        index = length;
        return value;
    }

    @Override
    public void close() {

    }
}
