package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.moskupols.junit.MultiFileMapTableProviderFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by moskupols on 09.12.14.
 */
public class TableProviderFactoryImpl implements TableProviderFactory {
    private final MultiFileMapTableProviderFactory backingFactory;

    public TableProviderFactoryImpl() {
        backingFactory = new MultiFileMapTableProviderFactory();
    }

    @Override
    public KnownDiffStructuredTableProvider create(String path) throws IOException {
        return new StringBackedStructuredTableProvider(
                Paths.get(path),
                new XmlSerializer(),
                new XmlDeserializer(),
                backingFactory.create(path)
        );
    }
}
