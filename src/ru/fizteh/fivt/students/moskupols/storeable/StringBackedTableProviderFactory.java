package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.moskupols.junit.KnownDiffTableProviderFactory;
import ru.fizteh.fivt.students.moskupols.junit.MultiFileMapTableProviderFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by moskupols on 09.12.14.
 */
public class StringBackedTableProviderFactory implements TableProviderFactory {
    private final KnownDiffTableProviderFactory stringTableProviderFactory;

    public StringBackedTableProviderFactory() {
        this(new MultiFileMapTableProviderFactory());
    }

    public StringBackedTableProviderFactory(KnownDiffTableProviderFactory stringTableProviderFactory) {
        this.stringTableProviderFactory = stringTableProviderFactory;
    }

    @Override
    public TableProvider create(String path) throws IOException {
        return new StringBackedStructuredTableProvider(
                Paths.get(path),
                new XmlSerializer(),
                new XmlDeserializer(),
                stringTableProviderFactory.create(path)
        );
    }
}
