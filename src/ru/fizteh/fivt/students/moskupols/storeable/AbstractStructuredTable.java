package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.Table;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by moskupols on 09.12.14.
 */
public abstract class AbstractStructuredTable implements Table {
    private final List<Class<?>> signature;

    protected AbstractStructuredTable(Path path) throws IOException {
        signature = readSignature(path.resolve("signature.tsv"));
    }

    private List<Class<?>> readSignature(Path signFilePath) throws IOException {
        String line;
        try (Scanner scanner = new Scanner(new FileInputStream(signFilePath.toFile()))) {
            line = scanner.nextLine();
            if (scanner.hasNextLine()) {
                throw new IOException("signature file should not contain second line");
            }
        }
        final String[] typeNames = line.split(" +");
        List<Class<?>> signature = new ArrayList<>(typeNames.length);
        for (String typeName : typeNames) {
            final StoreableAtomType type;
            try {
                type = StoreableAtomType.withPrintedName(typeName);
            } catch (EnumConstantNotPresentException e) {
                throw new IOException("Unknown type " + typeName, e);
            }
            signature.add(type.getBoxedClass());
        }
        return signature;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return signature.get(columnIndex);
    }

    @Override
    public int getColumnsCount() {
        return signature.size();
    }
}
