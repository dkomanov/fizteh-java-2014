package ru.fizteh.fivt.students.kinanAlsarmini.storable.tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.database.DatabaseTableProviderFactory;

import java.io.IOException;

public class DatabaseProviderFactoryTests {
    @Test(expected = IllegalArgumentException.class)
    public void createProviderNullDirectoryTest() {
        TableProviderFactory factory = new DatabaseTableProviderFactory();

        try {
            factory.create(null);
        } catch (IOException e) {
            // SAD
        }
    }
}
