package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.students.moskupols.junit.KnownDiffTable;
import ru.fizteh.fivt.students.moskupols.junit.KnownDiffTableProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by moskupols on 09.12.14.
 */
public class StringBackedStructuredTableProvider
        extends AbstractStructuredTableProvider implements KnownDiffStructuredTableProvider{
    private final Path dbPath;
    private final KnownDiffTableProvider stringProvider;

    public StringBackedStructuredTableProvider(
            Path dbPath,
            Serializer serializer,
            Deserializer deserializer,
            KnownDiffTableProvider stringProvider) {
        super(serializer, deserializer);
        this.dbPath = dbPath;
        this.stringProvider = stringProvider;
    }

    @Override
    public KnownDiffStructuredTable getTable(String name) {
        final KnownDiffTable stringTable = stringProvider.getTable(name);
        if (stringTable == null) {
            return null;
        }
        try {
            return new StringBackedStructuredTable(this, dbPath.resolve(name), stringTable);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public KnownDiffStructuredTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        final KnownDiffTable stringTable = stringProvider.createTable(name);
        if (stringTable == null) {
            return null;
        }
        final Path tableRoot = dbPath.resolve(name);
        List<StoreableAtomType> sign =
                columnTypes.stream().map(StoreableAtomType::fromBoxedClass).collect(Collectors.toList());
        writeSignatureFile(sign, tableRoot);
        return new StringBackedStructuredTable(this, tableRoot, stringTable);
    }

    @Override
    public void removeTable(String name) throws IOException {
        final Path tablePath = dbPath.resolve(name);
        removeSignatureFile(tablePath);
        stringProvider.removeTable(name);
    }
}
