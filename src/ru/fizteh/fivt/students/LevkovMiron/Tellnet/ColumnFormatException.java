package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

/**
 * Бросается при попытке извлечь из колонки {@link ru.fizteh.fivt.students.LevkovMiron.Proxy.Storeable} значение не соответствующего типа,
 * либо подставить в колонку значение несоответствующего типа.
 */
public class ColumnFormatException extends IllegalArgumentException {

    public ColumnFormatException() {
    }

    public ColumnFormatException(String s) {
        super(s);
    }

    public ColumnFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ColumnFormatException(Throwable cause) {
        super(cause);
    }
}
