package ru.fizteh.fivt.students.ilivanov.Telnet;

import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileMapProviderFactory implements TableProviderFactory, AutoCloseable {
    private ArrayList<FileMapProvider> providers;
    private volatile boolean valid;

    public FileMapProviderFactory() {
        providers = new ArrayList<>();
        valid = true;
    }

    public FileMapProvider create(String location) throws IOException {
        if (!valid) {
            throw new IllegalStateException("TableProvider is closed");
        }
        if (location == null) {
            throw new IllegalArgumentException("Null location");
        }
        if (location.equals("")) {
            throw new IllegalArgumentException("Empty location");
        }
        File path = new File(location);
        FileMapProvider newProvider = new FileMapProvider(path);
        if (path.exists() && !path.isDirectory()) {
            throw new IllegalArgumentException("File is located at specified location");
        }
        if (!newProvider.isValidLocation()) {
            if (!path.mkdirs()) {
                throw new IOException("Database location is invalid");
            }
        }
        if (!newProvider.isValidContent()) {
            throw new RuntimeException("Database folder contains files");
        }
        providers.add(newProvider);
        return newProvider;
    }

    public void close() {
        try {
            for (FileMapProvider provider : providers) {
                provider.close();
            }
            providers.clear();
        } finally {
            valid = false;
        }
    }
}
