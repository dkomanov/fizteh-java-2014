package ru.fizteh.fivt.students.ilivanov.JUnit;

import ru.fizteh.fivt.students.ilivanov.JUnit.TableInterfaces.TableProviderFactory;

import java.io.File;

public class FileMapProviderFactory implements TableProviderFactory {

    public FileMapProvider create(final String location) {
        if (location == null) {
            throw new IllegalArgumentException("null location");
        }
        FileMapProvider newProvider = new FileMapProvider(new File(location));
        if (!newProvider.isValidLocation()) {
            throw new IllegalArgumentException("database location is invalid");
        }
        if (!newProvider.isValidContent()) {
            throw new IllegalArgumentException("database folder contains files");
        }
        return newProvider;
    }
}
