package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.moskupols.junit.KnownDiffTable;
import ru.fizteh.fivt.students.moskupols.junit.KnownDiffTableProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by moskupols on 09.12.14.
 */
public class StringBackedStructuredTableProvider
        extends AbstractStructuredTableProvider implements TableProvider {
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
    public Table getTable(String name) {
        final KnownDiffTable stringTable = stringProvider.getTable(name);
        if (stringTable == null) {
            return null;
        }
        try {
            return new StringBackedStructuredTable(this, dbPath.resolve(name), stringTable);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (columnTypes == null) {
            throw new IllegalArgumentException("columnTypes must not be null");
        }
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
        if (name == null) {
            throw new IllegalArgumentException("columnTypes must not be null");
        }
        final Path tablePath = dbPath.resolve(name);
        removeSignatureFile(tablePath);
        stringProvider.removeTable(name);
    }

    @Override
    public List<String> getTableNames() {
        return Arrays.asList(dbPath.toFile().list((dir, name) -> !"signature.tsv".equals(name)));
    }
}
