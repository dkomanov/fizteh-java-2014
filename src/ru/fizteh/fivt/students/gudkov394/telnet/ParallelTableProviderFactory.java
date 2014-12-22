package ru.fizteh.fivt.students.gudkov394.telnet;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.nio.file.Paths;
import java.util.ArrayList;

public class ParallelTableProviderFactory implements TableProviderFactory, AutoCloseable {

    private ArrayList<ParallelTableProvider> listProviders = new ArrayList<>();
    private boolean closed = false;

    @Override
    public TableProvider create(String dir) throws IllegalArgumentException, IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Factory is closed");
        }
        try {
            ParallelTableProvider newProvider = new ParallelTableProvider(Paths.get("").resolve(Paths.get(dir)));
            listProviders.add(newProvider);
            return newProvider;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        for (ParallelTableProvider provider : listProviders) {
            provider.close();
        }
        closed = true;
    }
}
