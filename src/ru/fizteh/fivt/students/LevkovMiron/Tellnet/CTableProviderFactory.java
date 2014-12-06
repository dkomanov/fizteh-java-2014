package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by Мирон on 08.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class CTableProviderFactory implements TableProviderFactory, AutoCloseable {

    private ArrayList<CTableProvider> listProviders = new ArrayList<>();
    private boolean closed = false;

    @Override
    public TableProvider create(String dir) throws IllegalArgumentException, IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Factory is closed");
        }
        try {
            CTableProvider newProvider = new CTableProvider(Paths.get("").resolve(Paths.get(dir)));
            listProviders.add(newProvider);
            return newProvider;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        for (CTableProvider provider : listProviders) {
            provider.close();
        }
        closed = true;
    }
}
