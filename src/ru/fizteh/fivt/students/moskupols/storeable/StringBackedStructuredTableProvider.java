package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by moskupols on 09.12.14.
 */
public class StringBackedStructuredTableProvider extends AbstractStructuredTableProvider {
    private final Path dbPath;
    private final TableProvider stringProvider;

    public StringBackedStructuredTableProvider(
            Path dbPath,
            Serializer serializer,
            Deserializer deserializer,
            TableProvider stringProvider) {
        super(serializer, deserializer);
        this.dbPath = dbPath;
        this.stringProvider = stringProvider;
    }

    @Override
    public Table getTable(String name) {
        final ru.fizteh.fivt.storage.strings.Table stringTable = stringProvider.getTable(name);
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
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        final ru.fizteh.fivt.storage.strings.Table stringTable = stringProvider.createTable(name);
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
