package ru.fizteh.fivt.students.ilivanov.Storeable;

import ru.fizteh.fivt.students.ilivanov.Storeable.Interfaces.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class FileMapProviderFactory implements TableProviderFactory {

    public FileMapProvider create(final String location) throws IllegalArgumentException, IOException {
        if (location == null) {
            throw new IllegalArgumentException("Null location");
        }
        if (location.equals("")) {
            throw new IllegalArgumentException("Empty location");
        }
        FileMapProvider newProvider = new FileMapProvider(new File(location));
        if (!newProvider.isValidLocation()) {
            throw new IOException("database location is invalid");
        }
        if (!newProvider.isValidContent()) {
            throw new RuntimeException("database folder contains files");
        }
        return newProvider;
    }
}
