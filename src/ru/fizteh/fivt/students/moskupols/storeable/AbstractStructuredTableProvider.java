package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by moskupols on 09.12.14.
 */
public abstract class AbstractStructuredTableProvider implements TableProvider {
    private final Serializer serializer;
    private final Deserializer deserializer;

    protected AbstractStructuredTableProvider(Serializer serializer, Deserializer deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    protected void writeSignatureFile(List<StoreableAtomType> signature, Path tableRoot)
            throws FileNotFoundException {
        Path filePath = tableRoot.resolve("signature.tsv");
        try (PrintWriter writer = new PrintWriter(filePath.toFile())) {
            final StringJoiner joiner = new StringJoiner(" ");
            for (StoreableAtomType type : signature) {
                joiner.add(type.printedName);
            }
            writer.print(joiner.toString());
            writer.flush();
        }
    }

    protected void removeSignatureFile(Path tableRoot) {
        tableRoot.toFile().delete();
    }

    private List<StoreableAtomType> signatureFor(Table table) {
        List<StoreableAtomType> signature = new ArrayList<>(table.getColumnsCount());
        for (int i = 0; i < table.getColumnsCount(); i++) {
            signature.add(StoreableAtomType.fromBoxedClass(table.getColumnType(i)));
        }
        return signature;
    }

    @Override
    public final Storeable deserialize(Table table, String value) throws ParseException {
        return deserializer.deserialize(signatureFor(table), value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return serializer.serialize(signatureFor(table), value);
    }

    @Override
    public Storeable createFor(Table table) {
        return new StoreableImpl(signatureFor(table));
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException();
        }
        Storeable ret = new StoreableImpl(signatureFor(table));
        for (int i = 0; i < values.size(); i++) {
            ret.setColumnAt(i, values.get(i));
        }
        return ret;
    }
}
