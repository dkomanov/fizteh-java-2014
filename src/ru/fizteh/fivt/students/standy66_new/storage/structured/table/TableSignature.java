package ru.fizteh.fivt.students.standy66_new.storage.structured.table;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.standy66_new.utility.ClassUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Created by andrew on 07.11.14.
 */
public class TableSignature {
    private Class<?>[] storedObjectClasses;

    public TableSignature(Class<?>... storedObjectClasses) {
        for (Class<?> storedObjectClass : storedObjectClasses) {
            if (storedObjectClass == null) {
                throw new IllegalArgumentException("column class should not be null");
            }
        }
        this.storedObjectClasses = storedObjectClasses;
    }

    public static TableSignature forTable(Table t) {
        return new TableSignature(
                IntStream.range(0, t.getColumnsCount())
                        .mapToObj(i -> t.getColumnType(i))
                        .toArray(size -> new Class<?>[size]));
    }

    public static TableSignature readFromFile(File signatureFile) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(signatureFile)) {
            List<Class<?>> columnClasses = new ArrayList<>();
            while (scanner.hasNext()) {
                columnClasses.add(ClassUtility.forName(scanner.next()));
            }
            if (columnClasses.isEmpty()) {
                throw new IllegalStateException("empty signature file");
            }
            return new TableSignature(columnClasses.toArray(new Class<?>[columnClasses.size()]));
        }
    }

    public void writeToFile(File signatureFile) throws FileNotFoundException {
        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(signatureFile, false))) {
            printWriter.write(toString());
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < storedObjectClasses.length; i++) {
            Class<?> storedClass = storedObjectClasses[i];
            stringBuilder.append(ClassUtility.toString(storedClass));
            if (i != storedObjectClasses.length - 1) {
                stringBuilder.append(' ');
            }
        }
        return stringBuilder.toString();
    }

    public Class<?> getClassAt(int columnIndex) throws IndexOutOfBoundsException {
        return storedObjectClasses[columnIndex];
    }

    public int size() {
        return storedObjectClasses.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TableSignature)) {
            return false;
        } else {
            TableSignature other = (TableSignature) obj;
            return Arrays.equals(storedObjectClasses, other.storedObjectClasses);
        }
    }

    @Override
    public int hashCode() {
        return storedObjectClasses.hashCode();
    }
}
