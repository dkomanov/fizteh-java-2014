package ru.fizteh.fivt.students.standy66_new.storage.structured.table;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.standy66_new.utility.ClassUtility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by andrew on 07.11.14.
 */
public class TableRow implements Storeable {
    private TableSignature tableSignature;
    private Object[] row;

    public TableRow(TableSignature tableSignature) {
        this.tableSignature = tableSignature;
        row = new Object[tableSignature.size()];
    }

    public static TableRow fromStoreable(TableSignature tableSignature, Storeable storeable) {
        TableRow tableRow = new TableRow(tableSignature);
        IntStream.range(0, tableSignature.size())
                .forEach(i -> tableRow.setColumnAt(i, storeable.getColumnAt(i)));
        return tableRow;
    }

    public static TableRow deserialize(TableSignature signature, String serializedValue) throws ParseException {
        //TODO: test this bullshit
        serializedValue = serializedValue.replaceAll("[\\]\\s]+$|^[\\[\\s]+", "");
        //TODO: string can contain ","
        List<String> tokens = Stream.of(serializedValue.split(","))
                .map(s -> s.trim())
                .collect(Collectors.toList());
        if (tokens.size() != signature.size()) {
            throw new ParseException("expected " + signature.size() + " arguments, found " + tokens.size(), 0);
        }
        TableRow tableRow = new TableRow(signature);
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("null")) {
                continue;
            }
            Class<?> expectedClass = signature.getClassAt(i);
            if (token.startsWith("\"")) {
                if (!token.endsWith("\"")) {
                    throw new ParseException("\" expected at the end of the token", i);
                }
                if (expectedClass != String.class) {
                    throw new ParseException("Expected: " + ClassUtility.toString(expectedClass)
                            + ", got: " + token, i);
                }
                token = token.substring(1, token.length() - 1);
            } else {
                if (expectedClass == String.class) {
                    throw new ParseException("Expected: String, got: " + token, i);
                }
            }
            if (expectedClass == String.class) {
                tableRow.setColumnAt(i, token);
            } else {
                for (Method m : expectedClass.getMethods()) {
                    if (m.getName().startsWith("parse") && m.getParameterCount() == 1) {
                        try {
                            tableRow.setColumnAt(i, expectedClass.cast(m.invoke(null, token)));
                            break;
                        } catch (IllegalAccessException e) {
                            throw new ParseException("Error parsing: " + e.getMessage(), i);
                        } catch (InvocationTargetException e) {
                            throw new ParseException("Error parsing: " + e.getTargetException().getMessage(), i);
                        }
                    }
                }
            }
        }
        return tableRow;
    }


    public String serialize() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < row.length; i++) {
            Object element = row[i];
            if (element != null && element.getClass() == String.class) {
                stringBuilder.append("\"" + String.valueOf(row[i]) + "\"");
            } else {
                stringBuilder.append(String.valueOf(row[i]));
            }
            if (i < row.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("TableRow [");
        for (int i = 0; i < row.length; i++) {
            Object element = row[i];
            if (element == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(element.getClass().toString() + ": " + element.toString());
            }
            if (i < row.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null) {
            assertClassesEqualityAtIndex(value.getClass(), columnIndex);
        }
        row[columnIndex] = value;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return row[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Integer.class, columnIndex);
        return (Integer) row[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Long.class, columnIndex);
        return (Long) row[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Byte.class, columnIndex);
        return (Byte) row[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Float.class, columnIndex);
        return (Float) row[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Double.class, columnIndex);
        return (Double) row[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(Boolean.class, columnIndex);
        return (Boolean) row[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClassesEqualityAtIndex(String.class, columnIndex);
        return (String) row[columnIndex];
    }

    @Override
    public int hashCode() {
        return tableSignature.hashCode() ^ row.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TableRow)) {
            return false;
        } else {
            TableRow other = (TableRow) obj;
            return tableSignature.equals(other.tableSignature) && Arrays.equals(row, other.row);
        }
    }

    private void assertClassesEqualityAtIndex(Class<?> desired, int columnIndex) throws ColumnFormatException {
        Class<?> columnClass = tableSignature.getClassAt(columnIndex);
        if (desired != columnClass) {
            throw new ColumnFormatException(
                    String.format("column class at: %d is %s, desired: %s", columnIndex,
                            columnClass.getSimpleName(), desired.getSimpleName()));
        }
    }
}
