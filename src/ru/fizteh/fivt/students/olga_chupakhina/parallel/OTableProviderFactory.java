package ru.fizteh.fivt.students.olga_chupakhina.parallel;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public class OTableProviderFactory implements TableProviderFactory {

    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new OTableProvider(path);
    }
}